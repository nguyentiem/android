package tools.certification.latency.auto.ui.quickcommand.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import tools.certification.latency.auto.data.database.entity.TestResult
import tools.certification.latency.auto.data.database.entity.Utterance
import tools.certification.latency.auto.ui.main.data.BixbyRecognitionType
import tools.certification.latency.auto.ui.main.viewmodel.LatencyTestViewModel
import tools.certification.latency.auto.ui.quickcommand.data.QuickcommandRecognizeType
import tools.certification.latency.auto.ui.quickcommand.data.QuickcommandResult
import tools.certification.latency.auto.utils.AccuracyUtils
import java.util.*

@SuppressLint("StaticFieldLeak")
class QuickcommandViewModel(application: Application) : AndroidViewModel(application) {

    var textToSpeech: TextToSpeech? = null
    var app: Application = application
    private val _isRunning = MutableLiveData<Boolean>()
    val isRunning: LiveData<Boolean> = _isRunning

    lateinit var testResult: QuickcommandResult
    private val _curTest = MutableLiveData<QuickcommandResult?>(null)
    val curTest: LiveData<QuickcommandResult?> = _curTest

    private val bixbyRecognitionListener = object : RecognitionListener {
        private var mType: QuickcommandRecognizeType? = null
        private var hasError = true
        private var flag = false
        private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)

        private var speechRecognizer: SpeechRecognizer? = null

        fun startListening(type: QuickcommandRecognizeType)/* = viewModelScope.launch*/ {
            setupSpeechRecognizer(type)
        }

        private fun setupSpeechRecognizer(type: QuickcommandRecognizeType) {
            hasError = true
            mType = type
            flag = false
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(application).also {
                it.setRecognitionListener(this)
                it.startListening(intent)
            }
        }

        fun stopListening() = speechRecognizer?.run {
//            viewModelScope.launch {
                stopListening()
                onCleared()
//            }
        }

        fun onCleared() = speechRecognizer?.run {
            cancel()
            destroy()
            speechRecognizer = null
        }

        override fun onReadyForSpeech(params: Bundle?) {
            Timber.d("onReadyForSpeech" + mType)
        }

        override fun onBeginningOfSpeech() {
            Timber.d("onBeginningOfSpeech")
            val curMillis = System.currentTimeMillis()
//            Timber.d("onBeginningOfSpeech: $curMillis")
            when (mType) {
                QuickcommandRecognizeType.RESPONE_FIRST -> {
                    val curTest = _curTest.value ?: return
                    if (curTest.firstRespone.isStarted && curMillis - curTest.firstRespone.start > 500L || curTest.firstRespone.isNotStarted)
                        _curTest.value?.let {
                            val newResponse = it.firstRespone.copy(start = curMillis - 300L)
                            _curTest.value = (it.copy(firstRespone = newResponse))
                        }
                }

                QuickcommandRecognizeType.RESPONE_SECOND -> {
                    val curTest = _curTest.value ?: return
                    if (curTest.secondRespone.isStarted && curMillis - curTest.secondRespone.start > 500L || curTest.secondRespone.isNotStarted)
                        _curTest.value?.let {
                            val newResponse = it.secondRespone.copy(start = curMillis - 300L)
                            _curTest.value = (it.copy(secondRespone = newResponse))
                        }
                }
                QuickcommandRecognizeType.RESPONSE_THIRD -> {
                    val curTest = _curTest.value ?: return
                    if (curTest.thirdRespone.isStarted && curMillis - curTest.thirdRespone.start > 500L || curTest.thirdRespone.isNotStarted)
                        _curTest.value?.let {
                            val newResponse = it.thirdRespone.copy(start = curMillis - 300L)
                            _curTest.value = (it.copy(thirdRespone = newResponse))
                        }
                }
                else -> {
                    return
                }
            }

        }

        override fun onRmsChanged(rmsdB: Float) {
            if (rmsdB <= RMS_THRESHOLD) {
                return
            }
            when (mType) {
                QuickcommandRecognizeType.WAKE_UP -> {
//                    if (!flag) {
                    Timber.d("onRmsChanged: rmsB = $rmsdB, type = $mType")
//                        flag = true
//                        hasError = false
                    mType = null
                    _curTest.value?.let {
                        val currentTimeMillis = System.currentTimeMillis()
                        val newTing = it.ting.copy(start = currentTimeMillis, end = currentTimeMillis)
                        _curTest.value = it.copy(ting = newTing)
                    }
                    stopListening()
                    playCommand()
//                    }
                }

                QuickcommandRecognizeType.REQUEST -> {
//                    if (!flag) {
                    Timber.d("onRmsChanged: rmsB = $rmsdB, type = $mType")
//                        flag = true
//                        hasError = false
                    mType = null
                    stopListening()
                    startListening(QuickcommandRecognizeType.RESPONE_FIRST)
//                    }
                }

                QuickcommandRecognizeType.RESPONE_FIRST -> {
                    if (_curTest.value?.firstRespone?.start == -1L) {
                        Timber.d("onRmsChanged: rmsB = $rmsdB, type = $mType")
                        _curTest.value?.let {
                            val newResponse = it.firstRespone.copy(start = System.currentTimeMillis())
                            _curTest.value = (it.copy(firstRespone = newResponse))
                        }
                    }
                }

                QuickcommandRecognizeType.RESPONE_SECOND -> {
                    if (_curTest.value?.secondRespone?.start == -1L) {
                        Timber.d("onRmsChanged: rmsB = $rmsdB, type = $mType")
                        _curTest.value?.let {
                            val newResponse = it.secondRespone.copy(start = System.currentTimeMillis())
                            _curTest.value = (it.copy(secondRespone = newResponse))
                        }
                    }

                }

                QuickcommandRecognizeType.RESPONSE_THIRD -> {
                    if (_curTest.value?.thirdRespone?.start == -1L) {
                        Timber.d("onRmsChanged: rmsB = $rmsdB, type = $mType")
                        _curTest.value?.let {
                            val newResponse = it.thirdRespone.copy(start = System.currentTimeMillis())
                            _curTest.value = (it.copy(thirdRespone = newResponse))
                        }
                    }
                }
                else -> {

                }
            }

        }

        override fun onBufferReceived(buffer: ByteArray?) {
            Timber.d("onBufferReceived")
        }

        override fun onEndOfSpeech() {
            Timber.d("onEndOfSpeech")
            when (mType) {
                QuickcommandRecognizeType.RESPONE_FIRST -> {
                    _curTest.value?.let {
                        val newResponse = it.firstRespone.copy(end = System.currentTimeMillis())
                        _curTest.value = it.copy(firstRespone = newResponse)
                    }
                }
                QuickcommandRecognizeType.RESPONE_SECOND -> {
                    _curTest.value?.let {
                        val newResponse = it.secondRespone.copy(end = System.currentTimeMillis())
                        _curTest.value = it.copy(secondRespone = newResponse)
                    }

                }
                QuickcommandRecognizeType.RESPONSE_THIRD -> {
                    _curTest.value?.let {
                        val newResponse = it.thirdRespone.copy(end = System.currentTimeMillis())
                        _curTest.value = it.copy(thirdRespone = newResponse)
                    }
                }
                else -> {
                }
            }
        }

        override fun onError(error: Int) {

            if (hasError) {
                Timber.d("onError: error = $error, hasError = $hasError " + mType)
                hasError = false
                mType = null
                stopListening()
                _curTest.value?.let {
                    val result = it.copy(accuracy = -1, isFinished = true)
                    _curTest.value = (result)

                }
            }
        }

        override fun onResults(bundle: Bundle?) {
            Timber.d("onResults" + bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0))
            when (mType) {
                QuickcommandRecognizeType.RESPONSE_THIRD -> {
                    bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)?.let {
                        _curTest.value?.let { oldResult ->
                            val hasAnyKeywordMatched = it.split(" ").any {
                                AccuracyUtils.map[oldResult.thirdRespone.command]?.contains(it) == true
                            }
                            val result = oldResult.copy(
                                thirdRespone = oldResult.thirdRespone.copy(command = it),
                                accuracy = if (hasAnyKeywordMatched) 1 else 0,
                                isFinished = true
                            )
                            _curTest.postValue(result)
                        }
                    }
                }

                QuickcommandRecognizeType.RESPONE_FIRST -> {
                    bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)?.let {
                        _curTest.value?.let { oldResult ->
                            val hasAnyKeywordMatched = it.split(" ").any {
                                AccuracyUtils.map[oldResult.firstRespone.command]?.contains(it) == true
                            }
                            val result = oldResult.copy(
                                firstRespone = oldResult.firstRespone.copy(command = it),
                                accuracy = if (hasAnyKeywordMatched) 1 else 0,
                                isFinished = true
                            )
                            _curTest.postValue(result)
                            stopListening()
                            startListening(QuickcommandRecognizeType.RESPONE_SECOND)
                        }
                    }

                }
                QuickcommandRecognizeType.RESPONE_SECOND -> {
                    bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)?.let {
                        _curTest.value?.let { oldResult ->
                            val hasAnyKeywordMatched = it.split(" ").any {
                                AccuracyUtils.map[oldResult.secondRespone.command]?.contains(it) == true
                            }
                            val result = oldResult.copy(
                                secondRespone = oldResult.secondRespone.copy(command = it),
                                accuracy = if (hasAnyKeywordMatched) 1 else 0,
                                isFinished = true
                            )
                            _curTest.postValue(result)
                            stopListening()
                            startListening(QuickcommandRecognizeType.RESPONSE_THIRD)
                        }
                    }

                }
                else -> {
                }
            }

        }

        override fun onPartialResults(partialResults: Bundle?) {
            Timber.d("onPartialResults")
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            Timber.d("onEvent")
        }
    }

    private fun playWakeUpBixby() {
        val curTest = _curTest.value ?: return
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Timber.d("WakeUpBixby onStart with utteranceId = $utteranceId")
                val newHi = curTest.wakeUp.copy(start = System.currentTimeMillis())
                _curTest.postValue(curTest.copy(wakeUp = newHi))
            }

            override fun onDone(utteranceId: String?) {
                Timber.d("WakeUpBixby onDone with utteranceId = $utteranceId")
                val newHi = curTest.wakeUp.copy(end = System.currentTimeMillis())
                _curTest.postValue(curTest.copy(wakeUp = newHi))
//                viewModelScope.launch {
               MainScope().launch {
                   bixbyRecognitionListener.startListening(QuickcommandRecognizeType.WAKE_UP)
               }
//                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _curTest.value?.let {
                    _curTest.postValue(it.copy(accuracy = -1, isFinished = true))
                }
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                super.onError(utteranceId, errorCode)
                Timber.d("WakeUpBixby onError with utteranceId = $utteranceId, errorCode = $errorCode")
            }
        })
        textToSpeech?.speak(
            curTest.wakeUp.command, TextToSpeech.QUEUE_ADD, null,
            TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID
        )
    }

    private fun playCommand() {
        val curTest = _curTest.value ?: return
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Timber.d("RequestBixby onStart with utteranceId = $utteranceId")
                val newRequest = curTest.qcRequest.copy(start = System.currentTimeMillis())
                _curTest.postValue(curTest.copy(qcRequest = newRequest))
            }

            override fun onDone(utteranceId: String?) {
                Timber.d("RequestBixby onDone with utteranceId = $utteranceId")
                val newRequest = curTest.qcRequest.copy(end = System.currentTimeMillis())
                _curTest.postValue(curTest.copy(qcRequest = newRequest))
//                viewModelScope.launch {
                MainScope().launch {
                    bixbyRecognitionListener.startListening(QuickcommandRecognizeType.REQUEST)
                }

//                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _curTest.value = curTest.copy(accuracy = -1, isFinished = true)
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                super.onError(utteranceId, errorCode)
                Timber.d("RequestBixby onError with utteranceId = $utteranceId, errorCode = $errorCode")
            }
        })
        textToSpeech?.speak(
            curTest.qcRequest.command, TextToSpeech.QUEUE_ADD, null,
            TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID
        )
    }

    fun runTest(quickcomand: String) /*= viewModelScope.launch*/ {
        if (textToSpeech!=null && textToSpeech?.isSpeaking==true){
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
        bixbyRecognitionListener.stopListening()
        val utt = Utterance(command = quickcomand)
        testResult = QuickcommandResult(qcRequest = utt)
        _curTest.value = testResult.copy(id = System.currentTimeMillis())
        textToSpeech = TextToSpeech(app) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech?.language = Locale.US
                playWakeUpBixby()
            }
        }
    }

    public fun cleared() {
        if (textToSpeech != null && textToSpeech?.isSpeaking == true) {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
        if (bixbyRecognitionListener != null)
            bixbyRecognitionListener.stopListening()
    }

    companion object {
        private const val TEST_DELAY = 1000L
        private const val RMS_THRESHOLD = 7f
        private const val RECOGNIZE_WAITING = 10000L
    }
}