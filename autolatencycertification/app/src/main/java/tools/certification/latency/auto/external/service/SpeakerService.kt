package tools.certification.latency.auto.external.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import tools.certification.latency.auto.R

class SpeakerService : Service() {
    private val speakerBinder = SpeakerBinder()

    private var utteranceMedia = MediaPlayer()
    private var noiseMedia = MediaPlayer()

    var speeches = listOf<String>()

    private val _isSpeaking = MutableLiveData<Boolean>() // check service running
    val isSpeaking: LiveData<Boolean> get() = _isSpeaking

    private val _isDone = MutableLiveData<Boolean>() // check per speech done or yet
    val isDone: LiveData<Boolean> get() = _isDone

    private var mode: String = "normal"

    private var curUtterance = 0
    var _repeat = 1
    var repeat = 1 // if not repeat default 1

    private var volumeNormal = 0.75f
    private var minVolume = 0.1f // mode Volume
    private var maxVolume = 1f
    private var volumeList = ArrayList<Float>()
    private var indexVol = 0

    private var typeNoise = 0
    private var noiseNormal = 0f
    private var minNoise = 0f // mode Noise
    private var maxNoise = 1f
    private var noiseList = ArrayList<Float>()
    private var indexNoise = 0


    private val utteranceMap = mapOf(
        HI_BIXBY to R.raw.utterance_hi_bixby,
        "\"What time is it?\"" to R.raw.utterance_what_time_is_it,
        "\"What day is today?\"" to R.raw.utterance_what_day_is_today,
        "\"What is weather now?\"" to R.raw.utterance_what_is_weather_now
    )

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        intent.apply {
            speeches = getStringArrayListExtra("speeches").orEmpty().toList()
            mode = getStringExtra("mode").toString()
            repeat = getIntExtra("repeat", 1)

            volumeNormal = getFloatExtra("volumeNormal", 0.75f)
            minVolume = getFloatExtra("minVolume", 0.75f)
            maxVolume = getFloatExtra("maxVolume", 0.75f)

            typeNoise = getIntExtra("typeNoise", 0)
            noiseNormal = getFloatExtra("noiseNormal", 0f)
            minNoise = getFloatExtra("minNoise", 0f)
            maxNoise = getFloatExtra("maxNoise", 1f)
        }

        setup()
        if (intent.getBooleanExtra("test", false)) {
            start()
        }
        return START_STICKY
    }

    private fun speakNext() {
        if (curUtterance == -1) return
        utteranceMedia = MediaPlayer()
        utteranceMedia.apply {
            setDataSource(
                this@SpeakerService,
                Uri.parse(
                    "android.resource://$packageName/${
                        utteranceMap.getValue(speeches[curUtterance])
                    }"
                )
            )
            setVolume(volumeList[indexVol], volumeList[indexVol])
            setOnPreparedListener {
                setOnCompletionListener {
                    release()
                    noiseMedia.pause()

                    // prepare to next utter
                    curUtterance++
                    if (curUtterance == speeches.size) {
                        if (--_repeat > 0) {
                            // TODO next repeat start here
                            curUtterance = 0
                            if (mode == "noise") {
                                indexNoise++
                            } else if (mode == "volume")
                                indexVol++
                        } else {
                            curUtterance = 0
                            noiseMedia.stop()
                            _isSpeaking.postValue(false)
                        }
                    }
                    _isDone.postValue(true)
                }
                _isDone.postValue(false)
                start()
            }
            prepareAsync()
        }
    }

    private fun noiseNext() {
        if (curUtterance == -1) return
        noiseMedia.apply {
            setVolume(noiseList[indexNoise], noiseList[indexNoise])
            start()
        }
    }

    fun setup() {
        utteranceMedia = MediaPlayer()
        noiseMedia = MediaPlayer.create(this, typeNoise)
        noiseMedia.isLooping = true
        volumeList.clear()
        noiseList.clear()
        indexVol = 0
        indexNoise = 0
        curUtterance = 0
        _isDone.value = false
        when (mode) {
            "volume" -> {
                val step = (maxVolume - minVolume).div(repeat - 1)
                var vol = minVolume
                do {
                    volumeList.add(vol)
                    vol += step
                } while (vol <= maxVolume)
                noiseList.add(noiseNormal)
            }
            "noise" -> {
                val step = (maxNoise - minNoise).div(repeat - 1)
                var vol = minNoise
                do {
                    noiseList.add(vol)
                    vol += step
                } while (vol <= maxNoise)
                volumeList.add(volumeNormal)
            }
            else -> {
                volumeList.add(volumeNormal)
                noiseList.add(noiseNormal)
            }
        }
        _repeat = repeat
    }

    fun getCurrentVolume(): Float {
        return volumeList[indexVol]
    }

    fun getCurrentNoise(): Float {
        return noiseList[indexNoise]
    }

    fun skip() {
        utteranceMedia.release()
        curUtterance = 0
        if (mode == "noise") {
            indexNoise++
        } else if (mode == "volume")
            indexVol++
    }

    fun start() {
        noiseNext()
        speakNext()
        _isSpeaking.postValue(true)
    }

    fun stop() {
        utteranceMedia.release()
        noiseMedia.stop()
        curUtterance = 0
        _isSpeaking.postValue(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        utteranceMedia.release()
        noiseMedia.stop()
    }

    override fun onBind(p0: Intent?): IBinder {
        return speakerBinder
    }

    inner class SpeakerBinder : Binder() {
        fun getService() = this@SpeakerService
    }

    companion object {
        private const val HI_BIXBY = "\"Hi bixby\""
    }
}
