package com.example.autolatety.text2speech.viewmodel

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.autolatety.data.Result
import com.example.autolatety.listener.RecognizeListener
import com.example.autolatety.text2speech.data.Utterance
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


open class TTSViewModel(context: Context) {
     lateinit var speech: TextToSpeech
    private lateinit var context:Context
//    private lateinit var result:Result
    private lateinit var listener: RecognizeListener
    private val _menuList = MutableLiveData<List<Utterance>>()
    val menuList: LiveData<List<Utterance>> get() = _menuList
    private val menus = mutableListOf<Utterance>()

    private val _speechList = MutableLiveData<List<Utterance>>()
    val speechList: LiveData<List<Utterance>> get() = _speechList
    private val speeches = mutableListOf<Utterance>()

    private val _isSpeaking = MutableLiveData<Boolean>()
    val isSpeaking: LiveData<Boolean> get() = _isSpeaking

    private var curUtterance = -1
    private var delayTime = -1
    private var repeatTime = 6 // by default
    private var repeat = 0
    private val params: Bundle = Bundle()

    init {
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "")
        this.context = context
        speakMode()
    }

    fun speakMode(){
        speech = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                speech.language = Locale.US
                speech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(p0: String?) {

                        _isSpeaking.postValue(true)
                    }

                    override fun onDone(p0: String?) {
                        GlobalScope.launch {
                            if (speeches[curUtterance++] != speeches.last())
                                delay(delayTime * 500L)
                            if (curUtterance == speeches.size) {
                                if (--repeat > 0) {
                                    curUtterance = 0
                                    delay(delayTime * 1000L)
                                } else {
                                    curUtterance = -1
                                    _isSpeaking.postValue(false)
                                }
                            }
                            speakNext()
                        }
                    }

                    override fun onError(p0: String?) {
                        _isSpeaking.postValue(false)
                    }
                })
            }
        }
    }

    fun setListener( lis:RecognizeListener){
        listener =lis
    }

    fun speakNext() {
        if (curUtterance == -1) return
        speech.speak(speeches[curUtterance].content, TextToSpeech.QUEUE_ADD, params, "UniqueID")
    }

    fun play() {
        curUtterance = 0
        speakNext()
    }


    fun stop() {
        speech.stop()
        _isSpeaking.postValue(false)
    }

    fun pause(){
        speech.stop()
    }

    fun resume() {
        speakNext()
    }

    fun setDelay(delayTime: Int) {
        this.delayTime = delayTime
    }

    fun setRepeat(repeat: Int) {
        this.repeat = repeat
    }

    fun setSpeed(rate: Float) {
        val rateSpeed = if (rate == 0f) 0.01f else rate
        speech.setSpeechRate(rateSpeed)
    }

    fun setPitch(pitch: Float) {
        val pitchSpeed = if (pitch == 0f) 0.01f else pitch
        speech.setPitch(pitchSpeed)
    }

    fun shutdown() {
        speech.shutdown()
    }

    fun getSpeeches() = speeches

    open fun addSpeech(utterance: Utterance) {
        speeches.add(utterance)
        _speechList.value = speeches

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            menus.removeIf {
                it.content == utterance.content
            }
        }
        _menuList.value = menus
    }

    open fun removeSpeech(utter: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            speeches.removeIf {
                it.content == utter
            }
        }
        _speechList.value = speeches

        menus.add(Utterance(utter))
        _menuList.value = menus
    }

    fun initData() {
        menus.clear()
        speeches.clear()
        menus.getMenus()
        speeches.getSpeeches()
        _menuList.value = menus
        _speechList.value = speeches
    }

    open fun MutableList<Utterance>.getMenus() {}
    open fun MutableList<Utterance>.getSpeeches() {}
}