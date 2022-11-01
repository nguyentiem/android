package tools.certification.latency.auto.ui.main.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import tools.certification.latency.auto.data.database.AppDatabase
import tools.certification.latency.auto.data.database.entity.TestResult
import tools.certification.latency.auto.data.database.entity.Utterance
import tools.certification.latency.auto.ext.showToast
import tools.certification.latency.auto.external.service.SpeakerService
import tools.certification.latency.auto.ui.main.data.BixbyRecognitionType
import tools.certification.latency.auto.utils.AccuracyUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StaticFieldLeak")
class LatencyTestViewModel(application: Application) : AndroidViewModel(application) {
    private val repository by lazy { AppDatabase.getInstance(application).testResultDao() }

    private var countDone = 0
    private var speakerService: SpeakerService? = null
    private var serviceConnection: ServiceConnection? = null

    private val bixbyRecognitionListener = object : RecognitionListener {
        private var mType: BixbyRecognitionType? = null
        private var hasError = true

        private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, RECOGNIZE_MAX_RESULT)
            .putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, RECOGNIZE_WAITING)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())
            .putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
        private var speechRecognizer: SpeechRecognizer? = null

        fun startListening(type: BixbyRecognitionType) = viewModelScope.launch {
            setupSpeechRecognizer(type)
        }

        private fun setupSpeechRecognizer(type: BixbyRecognitionType) {
            hasError = true
            mType = type
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(application).also {
                it.setRecognitionListener(this)
                it.startListening(intent)
            }
        }

        fun stopListening() = speechRecognizer?.run {
            viewModelScope.launch {
                stopListening()
                onCleared()
            }
        }

        fun onCleared() = speechRecognizer?.run {
            cancel()
            destroy()
            speechRecognizer = null
        }

        override fun onReadyForSpeech(params: Bundle?) {
            Timber.d("onReadyForSpeech")
        }

        override fun onBeginningOfSpeech() {
            Timber.d("onBeginningOfSpeech")

            if (mType == BixbyRecognitionType.RESPONSE) {
                val curMillis = System.currentTimeMillis()
                Timber.d("onBeginningOfSpeech: $curMillis")
                val curTest = _curTest.value ?: return
                if (curTest.response.isStarted && curMillis - curTest.response.start > 500L || curTest.response.isNotStarted)
                    _curTest.value?.let {
                        val newResponse = it.response.copy(start = curMillis - 300L)
                        _curTest.value = (it.copy(response = newResponse))
                    }
            }
        }

        override fun onRmsChanged(rmsdB: Float) {
            Timber.d("onRmsChanged: rmsB = $rmsdB, type = $mType")
            if (rmsdB <= RMS_THRESHOLD) {
                return
            }
            if (mType == BixbyRecognitionType.WAKE_UP) {
                mType = null
                _curTest.value?.let {
                    val currentTimeMillis = System.currentTimeMillis()
                    val newTing = it.ting.copy(start = currentTimeMillis, end = currentTimeMillis)
                    _curTest.value = it.copy(ting = newTing)
                }
                stopListening()
                onCommandStart()
            } else if (mType == BixbyRecognitionType.REQUEST) {
                mType = null
                stopListening()
                startListening(BixbyRecognitionType.RESPONSE)
            } else if (mType == BixbyRecognitionType.RESPONSE && _curTest.value?.response?.start == -1L) {
                _curTest.value?.let {
                    val newResponse = it.response.copy(start = System.currentTimeMillis())
                    _curTest.value = (it.copy(response = newResponse))
                }
            }
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            Timber.d("onBufferReceived")
        }

        override fun onEndOfSpeech() {
            Timber.d("onEndOfSpeech")
            if (mType == BixbyRecognitionType.RESPONSE) {
                _curTest.value?.let {
                    val newResponse = it.response.copy(end = System.currentTimeMillis())
                    _curTest.value = it.copy(response = newResponse)
                }
            }
        }

        override fun onError(error: Int) {
            Timber.d("onError: error = $error, hasError = $hasError")
            if (hasError) {
                hasError = false
                mType = null
                stopListening()
                _curTest.value?.let {
                    val result = it.copy(accuracy = -1, isFinished = true)
                    _curTest.value = (result)
                    val oldResults = _results.value?.toMutableList() ?: mutableListOf()
                    oldResults.add(result)
                    _results.postValue(oldResults)
                }
                countDone = 0
                speakerService?.skip()
                runNextTest()
            }
        }

        override fun onResults(bundle: Bundle?) {
            Timber.d("onResults")
            if (mType == BixbyRecognitionType.RESPONSE) {
                bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)?.let {
                    _curTest.value?.let { oldResult ->
                        val hasAnyKeywordMatched = it.split(" ").any {
                            AccuracyUtils.map[oldResult.request.command]?.contains(it) == true
                        }
                        val result = oldResult.copy(
                            response = oldResult.response.copy(command = it),
                            accuracy = if (hasAnyKeywordMatched) 1 else 0,
                            isFinished = true
                        )
                        _curTest.value = (result)
                        val oldResults = _results.value?.toMutableList() ?: mutableListOf()
                        oldResults.add(result)
                        _results.postValue(oldResults)
                    }
                }
                runNextTest()
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            Timber.d("onPartialResults")
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            Timber.d("onEvent")
        }
    }

    private lateinit var testResults: TestResult

    private val _results = MutableLiveData<List<TestResult>>(emptyList())
    val results: LiveData<List<TestResult>> = _results

    private val _isRunning = MutableLiveData<Boolean>()
    val isRunning: LiveData<Boolean> = _isRunning

    private val _testNumber = MutableLiveData(0)
    val testNumber: LiveData<Int> = _testNumber

    private val _curTest = MutableStateFlow<TestResult?>(null)
    val curTest: StateFlow<TestResult?> = _curTest

    fun load() = viewModelScope.launch {
        val oldResults = withContext(Dispatchers.IO) {
            repository.findAll().first()
        }
        _results.value = oldResults
    }

    private fun onHiBixbyDone() {
        Timber.d("hiBixbyDone()")
        val curTest = _curTest.value ?: return
        val newHi = curTest.wakeUp.copy(end = System.currentTimeMillis())
        _curTest.value = (curTest.copy(wakeUp = newHi))
        bixbyRecognitionListener.startListening(BixbyRecognitionType.WAKE_UP)
    }

    private fun onCommandDone() {
        Timber.d("onCommandDone()")
        val curTest = _curTest.value ?: return
        val newRequest = curTest.request.copy(end = System.currentTimeMillis())
        _curTest.value = (curTest.copy(request = newRequest))
        bixbyRecognitionListener.startListening(BixbyRecognitionType.REQUEST)
    }

    private fun onHiBixbyStart() = viewModelScope.launch(Dispatchers.IO) {
        Timber.d("hiBixby()")
        val curTest = _curTest.value ?: return@launch
        speakerService?.start()
        curTest.volume = speakerService?.getCurrentVolume() ?: 0f
        curTest.noise = speakerService?.getCurrentNoise() ?: 0f
        val newHi = curTest.wakeUp.copy(start = System.currentTimeMillis())
        _curTest.value = (curTest.copy(wakeUp = newHi))
    }

    private fun onCommandStart() = viewModelScope.launch(Dispatchers.IO) {
        Timber.d("startCommand()")
        val curTest = _curTest.value ?: return@launch
        speakerService?.start()
        val newRequest = curTest.request.copy(start = System.currentTimeMillis())
        _curTest.value = (curTest.copy(request = newRequest))
    }

    private fun runNextTest() = viewModelScope.launch {
        _testNumber.value?.let {
            if (it == 0) {
                speakerService?.setup()
            }
            val repeat = speakerService?.repeat ?: 1
            if (it < repeat) {
                if (it > 0) {
                    withContext(Dispatchers.IO) {
                        delay(TEST_DELAY)
                    }
                }
                _testNumber.value = (it + 1)
                _curTest.value = testResults.copy(id = System.currentTimeMillis())
//                playWakeUpBixby()
                onHiBixbyStart()
            } else {
                _curTest.value = null
                _testNumber.value = 0
                _isRunning.postValue(false)
            }
        }
    }

    fun stopOrRunTest() {
        val isRunning = isRunning.value == true
        if (isRunning) {
            _curTest.value = null
            _testNumber.value = 0
            bixbyRecognitionListener.stopListening()
            countDone = 0
            speakerService?.stop()
            viewModelScope.coroutineContext.cancelChildren()
        } else {
            _results.postValue(emptyList())
            runNextTest()
        }
        _isRunning.postValue(!isRunning)
    }

    override fun onCleared() {
        bixbyRecognitionListener.onCleared()
        super.onCleared()
    }

    @SuppressLint("SimpleDateFormat")
    fun exportTestResults(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        _results.value?.let {
            val formatter = SimpleDateFormat("yyyyMMdd_HHmmss")
            val dateTime = formatter.format(Date())
            val folder = File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), "test_results")
            if (!folder.exists()) {
                folder.mkdir()
            }
            val fileName = "test_result_$dateTime.json"
            val file = File(folder, fileName)
            for (testResult in it) {
                testResult.updateResult()
            }
            val json = Json.encodeToString(it)
            file.outputStream().use { fos -> fos.write(json.toByteArray()) }
            withContext(Dispatchers.Main) {
                context.showToast("Exported to $fileName")
            }
        }
    }

    fun bindService(activity: AppCompatActivity) {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                val binder = p1 as SpeakerService.SpeakerBinder
                speakerService = binder.getService().also {
                    testResults = TestResult(request = it.speeches[1].let {
                        Utterance(it.substring(1, it.length - 1))
                    })
                    it.isSpeaking.removeObservers(activity)
                    it.isSpeaking.observe(activity) {
                        _isRunning.postValue(it)
                    }
                    it.isDone.removeObservers(activity)
                    // wait isDone
                    it.isDone.observe(activity) { isDone ->
                        Timber.d("isDone = $isDone, countDone = $countDone, repeat = ${it._repeat}")
                        if (isDone) {
                            countDone++
                            if (countDone == 1) { // first done -> wait ting ting (fake 2s)
                                onHiBixbyDone()
                            } else if (countDone == 2) {
                                // certificate here
                                countDone = 0 // ready next repeat
                                if (it._repeat >= 0)
                                    onCommandDone()
                            }
                        }
                    }
                }
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                Timber.d("onServiceDisconnected")
            }
        }.also {
            activity.bindService(
                Intent(activity, SpeakerService::class.java),
                it,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    fun unbindService(activity: AppCompatActivity) {
        serviceConnection?.let {
            activity.unbindService(it)
        }
    }

    fun onStop() {
        speakerService?.stop()
    }

    companion object {
        private const val TEST_DELAY = 7000L
        private const val RMS_THRESHOLD = 7f
        private const val RECOGNIZE_WAITING = 10000L
        private const val RECOGNIZE_MAX_RESULT = 1
    }
}
