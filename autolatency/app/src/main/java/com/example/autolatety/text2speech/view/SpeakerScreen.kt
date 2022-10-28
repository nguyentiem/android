package com.example.autolatety.text2speech.view

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.autolatety.R
import com.example.autolatety.databinding.SpeakerScreenBinding
import com.example.autolatety.text2speech.data.Utterance
import com.example.autolatety.text2speech.viewmodel.SpeakerScreenViewModel
import com.example.autolatety.ui.ResultActivity


import kotlinx.coroutines.launch

class SpeakerScreen : AppCompatActivity() {
    private lateinit var viewModel: SpeakerScreenViewModel
    private lateinit var binding: SpeakerScreenBinding
    private lateinit var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = SpeakerScreenViewModel(applicationContext)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.speaker_screen
        )
        binding.lifecycleOwner = this
        binding.vm = viewModel
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        initViewAndData()
        viewModel.menuList.observe(this) {
            binding.menuList.adapter?.notifyDataSetChanged()
        }
        viewModel.speechList.observe(this) {
            binding.speechList.adapter?.notifyDataSetChanged()
        }
        viewModel.isSpeaking.observe(this) {
            binding.toggleButton.isChecked = !it
        }

        val speechAdapter = UtteranceListAdapter {
            viewModel.removeSpeech(it)
        }
        speechAdapter.data = viewModel.speechList.value
        binding.speechList.adapter = speechAdapter

        val menuAdapter = UtteranceListAdapter {
            viewModel.addSpeech(Utterance(it))
        }
        menuAdapter.data = viewModel.menuList.value
        binding.menuList.adapter = menuAdapter

        binding.toggleButton.setOnClickListener {
            if (!binding.toggleButton.isChecked) {
                lifecycleScope.launch {
                    val repeat = getRepeat()
                    binding.vm?.play(3, repeat)
                }
            } else binding.vm?.stop()
        }

        binding.btnReset.setOnClickListener {
            initViewAndData()
        }

        binding.btnPlay.setOnClickListener {
           if(viewModel.speechList.value?.size?:1<2){
               Toast.makeText(applicationContext,"Please chose a command!",Toast.LENGTH_SHORT).show()
           }else{
               val repeat = getRepeat()
               val speed = getSpeed()
               val pitch = getPitch()

               val intent = Intent(this, ResultActivity::class.java)
               intent.putExtra(
                   "Speeches",
                   viewModel.speechList.value?.let { speechList -> ArrayList(speechList) })

               intent.putExtra("repeat", repeat)
               intent.putExtra("speed", speed)
               intent.putExtra("pitch", pitch)
               startActivity(intent)
           }

        }

        binding.speedBar.setOnSeekBarChangeListener(seekBarChangeListener {
            viewModel.setSpeed(getSpeed())
        })

        binding.pitchBar.setOnSeekBarChangeListener(seekBarChangeListener {
            viewModel.setPitch(getPitch())
        })

        binding.volumeBar.progress =
            (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 200f / 15).toInt()

        binding.volumeBar.setOnSeekBarChangeListener(seekBarChangeListener {
            val volume = binding.volumeBar.progress.div(200f) * maxVolume
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume.toInt(), 0)
        })



        binding.tvRepeatPlus.setOnClickListener {
            val d = binding.tvRepeatDelay.text.toString().toInt() + 1
            binding.tvRepeatDelay.text = d.toString()
        }

        binding.tvRepeatSub.setOnClickListener {
            var d = binding.tvRepeatDelay.text.toString().toInt() - 1
            d = if (d < 1) 1 else d
            binding.tvRepeatDelay.text = d.toString()
        }
    }

    private fun initViewAndData() {
        viewModel.initData()

        binding.swRepeat.isChecked = true
        binding.tvRepeatDelay.text = "1"
        binding.speedBar.progress = 100
        binding.pitchBar.progress = 100
    }



    private fun getRepeat() =  binding.tvRepeatDelay.text.toString().toInt()

    private fun getSpeed() = binding.speedBar.progress.div(100f)

    private fun getPitch() = binding.pitchBar.progress.div(100f)

    private fun seekBarChangeListener(onStop: () -> Unit) =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                onStop.invoke()
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.shutdown()
    }
}