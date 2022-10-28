package com.example.autolatety.text2speech.viewmodel

import android.content.Context
import android.widget.Toast
import com.example.autolatety.text2speech.data.Utterance
import com.example.autolatety.text2speech.viewmodel.TTSViewModel


class SpeakerScreenViewModel(private val context: Context) : TTSViewModel(context) {

    fun play(delayTime: Int, repeat: Int) {
        setDelay(delayTime)
        setRepeat(repeat)
        super.play()
    }

    override fun addSpeech(utterance: Utterance) {
        if (getSpeeches().size >= MAX_UTTERANCE)
            removeSpeech(getSpeeches().last().content)
        super.addSpeech(utterance)
    }

    override fun removeSpeech(utter: String) {
        if (utter == HI_BIXBY) {
            Toast.makeText(context, "Can't remove $HI_BIXBY", Toast.LENGTH_SHORT).show()
            return
        }
        super.removeSpeech(utter)
    }

    override fun MutableList<Utterance>.getMenus() {
        this.add(Utterance("What time is it?"))
        this.add(Utterance("What day is today?"))
        this.add(Utterance("What is weather now?"))
    }

    override fun MutableList<Utterance>.getSpeeches() {
        this.add(Utterance(HI_BIXBY))
    }

    companion object {
        private const val HI_BIXBY = "Hi bixby"
        private const val MAX_UTTERANCE = 2
    }
}