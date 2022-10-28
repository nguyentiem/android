package com.example.autolatety.text2speech.viewmodel

import android.content.Context
import com.example.autolatety.text2speech.data.Utterance
import kotlin.collections.ArrayList

class CertificateScreenViewModel(context: Context) : TTSViewModel(context) {

    fun setSpeeches(speeches: ArrayList<Utterance>) {
        speeches.forEach {
            addSpeech(it)
        }
    }

    fun setOption( repeat: Int, rate: Float, pitch: Float) {

        setRepeat(repeat)
        setSpeed(rate)
        setPitch(pitch)
    }
}