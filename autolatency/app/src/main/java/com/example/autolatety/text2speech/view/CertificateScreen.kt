package com.example.autolatety.text2speech.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.autolatety.R
import com.example.autolatety.databinding.CertificateScreenBinding
import com.example.autolatety.text2speech.data.Utterance
import com.example.autolatety.text2speech.viewmodel.CertificateScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CertificateScreen : AppCompatActivity() {
    private lateinit var viewModel: CertificateScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = CertificateScreenViewModel(applicationContext)
        val binding = DataBindingUtil.setContentView<CertificateScreenBinding>(
            this,
            R.layout.certificate_screen
        )
        binding.lifecycleOwner = this
        binding.vm = viewModel

        val speeches = intent.getSerializableExtra("Speeches")
        viewModel.setSpeeches(speeches as ArrayList<Utterance>)

        val repeat = intent.getIntExtra("repeat", -1)
        val speed = intent.getFloatExtra("speed", 1f)
        val pitch = intent.getFloatExtra("pitch", 1f)
        viewModel.setOption(repeat, speed, pitch)

        lifecycleScope.launch {
            delay(500L) // delay for CertificateScreenViewModel init successfully -- remove if unnecessary
            viewModel.play()
        }

        binding.btnStop.setOnClickListener {
            if (viewModel.isSpeaking.value == true)
                viewModel.stop() // TODO handle result success
            else
                finish()
        }

        binding.toggleButton.setOnClickListener {
            if (binding.toggleButton.isChecked) {
                viewModel.pause()
            } else {
                if (viewModel.isSpeaking.value == true) viewModel.resume()
                else recreate()
            }
        }

        viewModel.isSpeaking.observe(this) {
            if (!it) {
                binding.toggleButton.textOn = getString(R.string.restart)
                binding.toggleButton.isChecked = true
            } else binding.toggleButton.textOn = getString(R.string.resume)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.shutdown()
    }
}