package tools.certification.latency.auto.ui.settings.view

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.provider.DocumentsContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.google.android.material.slider.RangeSlider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import timber.log.Timber
import tools.certification.latency.auto.R
import tools.certification.latency.auto.databinding.SettingsActivityBinding
import tools.certification.latency.auto.ext.showToast
import tools.certification.latency.auto.external.service.SpeakerService
import tools.certification.latency.auto.ui.main.view.LatencyTestActivity
import tools.certification.latency.auto.ui.quickcommand.view.AddQuickCommandCertificateActivity
import tools.certification.latency.auto.ui.quickcommand.view.QuickcommandUttTestActivity
import tools.certification.latency.auto.ui.quickcommand.viewmodel.QuickcommandViewModel
import tools.certification.latency.auto.ui.settings.data.Configuration
import tools.certification.latency.auto.ui.settings.viewmodel.SettingsViewModel
import tools.certification.latency.auto.utils.StorageUtils
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.max

class SettingsActivity : AppCompatActivity() {

    private val viewModel by viewModels<SettingsViewModel>()
    private lateinit var binding: SettingsActivityBinding
    private var minRepeat = 1
    private var maxRepeat = Int.MAX_VALUE
    private lateinit var speakerService: SpeakerService
    private var countDone = 0
    private var repeat = 1
    private val fakeTime = 2
    private lateinit var arrayNoise: ArrayAdapter<String>
    private lateinit var arrayMode: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.settings_activity
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setSupportActionBar(binding.toolbar)
        requestPermissionsIfNeeded()

        initViewAndData()
        viewModel.menuList.observe(this) {
            binding.menuList.adapter?.notifyDataSetChanged()
        }
        viewModel.speechList.observe(this) {
            binding.speechList.adapter?.notifyDataSetChanged()
        }

        val speechAdapter = SettingsAdapter {
            viewModel.removeSpeech(it)
        }
        speechAdapter.data = viewModel.speechList.value.orEmpty()
        binding.speechList.adapter = speechAdapter

        val menuAdapter = SettingsAdapter {
            viewModel.addSpeech(it)
        }
        menuAdapter.data = viewModel.menuList.value.orEmpty()
        binding.menuList.adapter = menuAdapter

        binding.toggleButton.setOnClickListener {
            if (isSpeechesReady())
                if (!binding.toggleButton.isChecked) {
                    countDone = 0
                    startService(
                        addExtras(Intent(this, SpeakerService::class.java))
                            .putExtra("test", true)
                    )
                } else {
                    speakerService.stop()
                    // wait to close all relate service
                    binding.toggleButton.isEnabled = false
                    MainScope().launch {
                        delay(fakeTime * 1000L)
                        binding.toggleButton.isEnabled = true
                    }
                }
            else
                binding.toggleButton.isChecked = true
        }

        binding.btnReset.setOnClickListener {
//            initViewAndData()
            intent = Intent(this,AddQuickCommandCertificateActivity::class.java)
            startActivity(intent)
        }

        binding.btnPlay.setOnClickListener {
            if (!isSpeechesReady()) return@setOnClickListener
            if (!binding.toggleButton.isChecked) {
                GlobalScope.launch {
                    val job = GlobalScope.launch {
                        speakerService.stop()
                    }
                    job.join()
                }
            }
            startService(addExtras(Intent(this, SpeakerService::class.java)))
            startActivity(addExtras(Intent(this, LatencyTestActivity::class.java)))
        }

        arrayMode =
            ArrayAdapter(
                this,
                R.layout.spiner_item,
                viewModel.modeMap.keys.map { it })
        arrayMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spMode.adapter = arrayMode
        binding.spMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                val key = p0?.getItemAtPosition(position) as String
                setMode(key)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        binding.swRepeat.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.repeatView.visibility = View.VISIBLE
            } else {
                binding.repeatView.visibility = View.INVISIBLE
            }
        }

        binding.tvRepeatPlus.setOnClickListener {
            var d = binding.tvRepeatDelay.text.toString().toInt() + 1
            d = if (d > maxRepeat) maxRepeat else d
            binding.tvRepeatDelay.text = d.toString()
        }

        binding.tvRepeatSub.setOnClickListener {
            var d = binding.tvRepeatDelay.text.toString().toInt() - 1
            d = if (d < minRepeat) minRepeat else d
            binding.tvRepeatDelay.text = d.toString()
        }

        binding.volumeBar.setOnSeekBarChangeListener(
            seekBarChangeListener({
                val volume = binding.volumeBar.progress
                binding.tvVolume.text = volume.toString()
            }, {
                val volume = binding.volumeBar.progress.div(100f)
                viewModel.volumeNormal = volume
            })
        )

        binding.volumeSlider.addOnChangeListener(
            onChangeListener(
                binding.tvVolumeFrom,
                binding.tvVolumeTo
            )
        )
        binding.volumeSlider.addOnSliderTouchListener(sliderTouchListener { slider ->
            viewModel.minVolume = slider.values[0].div(100)
            viewModel.maxVolume = slider.values[1].div(100)
        })
        binding.volumeSlider.setMinSeparationValue(5f)

        arrayNoise =
            ArrayAdapter(
                this,
                R.layout.spiner_item,
                viewModel.noiseMap.keys.map { it })
        arrayNoise.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spNoise.adapter = arrayNoise

        binding.noiseBar.setOnSeekBarChangeListener(
            seekBarChangeListener({
                val noise = binding.noiseBar.progress
                binding.tvNoise.text = noise.toString()
            }, {
                val noise = binding.noiseBar.progress.div(100f)
                viewModel.noiseNormal = noise
            })
        )

        binding.noiseSlider.addOnChangeListener(
            onChangeListener(
                binding.tvNoiseFrom,
                binding.tvNoiseTo
            )
        )
        binding.noiseSlider.addOnSliderTouchListener(sliderTouchListener { slider ->
            viewModel.minNoise = slider.values[0].div(100)
            viewModel.maxNoise = slider.values[1].div(100)
        })
        binding.noiseSlider.setMinSeparationValue(5f)

    }

    private fun addExtras(intent: Intent): Intent {
        intent.putStringArrayListExtra(
            "speeches",
            viewModel.speechList.value as ArrayList<String>?
        )
        repeat = getRepeat()
        return intent.apply {
            putExtra("mode", viewModel.mode.value)
            putExtra("repeat", repeat)
            putExtra("volumeNormal", viewModel.volumeNormal)
            putExtra("minVolume", viewModel.minVolume)
            putExtra("maxVolume", viewModel.maxVolume)
            putExtra("typeNoise", getTypeNoise())
            putExtra("noiseNormal", viewModel.noiseNormal)
            putExtra("minNoise", viewModel.minNoise)
            putExtra("maxNoise", viewModel.maxNoise)
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as SpeakerService.SpeakerBinder
            speakerService = binder.getService()

            speakerService.isSpeaking.removeObservers(this@SettingsActivity)
            speakerService.isSpeaking.observe(this@SettingsActivity) {
                binding.toggleButton.isChecked = !it
            }

            speakerService.isDone.removeObservers(this@SettingsActivity)
            // wait isDone
            speakerService.isDone.observe(this@SettingsActivity) {
                if (it) {
                    countDone++
                    if (countDone == 1) { // first done -> wait ting ting (fake 2s)
                        Timer().schedule(timerTask {
                            if (speakerService.isSpeaking.value == true) // if not press Stop
                                speakerService.start() // ting ting -> play next utter
                        }, fakeTime * 1000L)
                    } else if (countDone == 2) {
                        countDone = 0 // ready next repeat
                        if (--repeat > 0)
                            Timer().schedule(timerTask {
                                if (speakerService.isSpeaking.value == true) // if not press Stop
                                    speakerService.start()
                            }, fakeTime * 1000L)
                    }
                }
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {}
    }

    override fun onStart() {
        super.onStart()
        bindService(
            Intent(this, SpeakerService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

    private fun setMode(key: String) {
        viewModel.setMode(key)
        when (viewModel.modeMap[key]) {
            "volume" -> setViewWithModeVolume()
            "noise" -> setViewWithModeNoise()
            else -> setViewWithModeNormal()
        }
    }

    private fun setViewWithModeNormal() {
        setVolumeNormal()
        setNoiseNormal()
        binding.swRepeat.isClickable = true
        minRepeat = 1
        maxRepeat = Int.MAX_VALUE
    }

    private fun setViewWithModeVolume() {
        val min = viewModel.minVolume * 100
        val max = viewModel.maxVolume * 100
        binding.apply {
            tvVolumeFrom.text = min.toInt().toString()
            tvVolumeTo.text = max.toInt().toString()
            volumeSlider.setValues(min, max)
            volumeSlider.isTickVisible = false
        }
        setRepeatNotNormal()
        setNoiseNormal()
    }

    private fun setViewWithModeNoise() {
        val min = viewModel.minNoise * 100
        val max = viewModel.maxNoise * 100
        binding.apply {
            tvNoiseFrom.text = min.toInt().toString()
            tvNoiseTo.text = max.toInt().toString()
            noiseSlider.setValues(min, max)
            noiseSlider.isTickVisible = false
        }
        setRepeatNotNormal()
        setVolumeNormal()
    }

    private fun setVolumeNormal() {
        val vol = (viewModel.volumeNormal * 100).toInt()
        binding.tvVolume.text = vol.toString()
        binding.volumeBar.progress = vol
    }

    private fun setNoiseNormal() {
        val noise = (viewModel.noiseNormal * 100).toInt()
        binding.tvNoise.text = noise.toString()
        binding.noiseBar.progress = noise
    }

    private fun setRepeatNotNormal() {
        binding.swRepeat.isChecked = true
        binding.swRepeat.isClickable = false
        minRepeat = 2
        if (getRepeat() < 2)
            binding.tvRepeatDelay.text = "2"
    }

    private fun initViewAndData() {
        viewModel.initData()
        binding.apply {
            spMode.setSelection(0)
            spNoise.setSelection(0)
            swRepeat.isChecked = false
            swRepeat.isClickable = true
            tvRepeatDelay.text = "1"
        }
    }

    private fun isSpeechesReady(): Boolean {
        if (viewModel.speechList.value?.size!! < 2) {
            Toast.makeText(this@SettingsActivity, "Please choose a command!", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true
    }

    private fun getRepeat() = if (!binding.swRepeat.isChecked) 1
    else binding.tvRepeatDelay.text.toString().toInt()

    private fun getTypeNoise() = viewModel.noiseMap[binding.spNoise.selectedItem]

    private fun seekBarChangeListener(onChange: () -> Unit, onStop: () -> Unit) =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                onChange.invoke()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                onStop.invoke()
            }
        }

    private fun onChangeListener(from: TextView, to: TextView) =
        RangeSlider.OnChangeListener { slider, _, _ ->
            val min = slider.values[0].toInt()
            val max = slider.values[1].toInt()
            from.text = min.toString()
            to.text = max.toString()
            maxRepeat = max(max - min + 1, minRepeat)
            if (getRepeat() > maxRepeat)
                binding.tvRepeatDelay.text = "$maxRepeat"
        }

    private fun sliderTouchListener(onStop: (RangeSlider) -> Unit) =
        object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {
            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                onStop.invoke(slider)
            }
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (StorageUtils.isExternalStorageAvailable() && !StorageUtils.isExternalStorageReadOnly()) {
            menuInflater.inflate(R.menu.settings_menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    @Throws(IOException::class)
    private fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val json = readTextFromUri(uri)
                    val config: Configuration = Json.decodeFromString(json)
                    Timber.d("config: $config")
                    config.utterance?.let { utterance -> viewModel.addSpeech(utterance) }
                    binding.tvRepeatDelay.text = config.repeat.toString()
                    viewModel.apply {
                        volumeNormal = config.volume
                        minVolume = config.minVolume
                        maxVolume = config.maxVolume
                        noiseNormal = config.noise
                        minNoise = config.minNoise
                        maxNoise = config.maxNoise
                    }
                    arrayNoise.getPosition(config.noiseType).let {
                        binding.spNoise.setSelection(it)
                    }
                    val mode = viewModel.modeMapInverse[config.mode]
                    arrayMode.getPosition(mode).let {
                        if (binding.spMode.selectedItemPosition != it)
                            binding.spMode.setSelection(it)
                        else setMode(mode!!)
                    }
                }
            }
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.import_settings -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "application/json"
                val folder = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "configs"
                )
                if (!folder.exists()) {
                    folder.mkdir()
                }
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, folder.toUri())
                resultLauncher.launch(intent)
                true
            }
            R.id.export_settings -> {
                val utterance = viewModel.speechList.value?.let {
                    if (it.size > 1) it[1] else null
                }
                val config = Configuration(
                    utterance = utterance,
                    mode = viewModel.mode.value!!,
                    repeat = getRepeat(),
                    volume = viewModel.volumeNormal,
                    minVolume = viewModel.minVolume,
                    maxVolume = viewModel.maxVolume,
                    noise = viewModel.noiseNormal,
                    minNoise = viewModel.minNoise,
                    maxNoise = viewModel.maxNoise,
                    noiseType = binding.spNoise.selectedItem.toString()
                )
                viewModel.exportSettings(this, config)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun requestPermissionsIfNeeded() {
        val write = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val read = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val record = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        val permissions = mutableListOf<String>()
        if (write != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (record != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO)
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSIONS_RC)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_RC && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            Timber.d("Request all permissions successfully!")
            showToast("Request all permissions successfully!")
        } else {
            Timber.d("Request all permissions failed!")
            showToast("Request all permissions failed!")
        }
    }

    companion object {
        private const val PERMISSIONS_RC = 113
    }
}
