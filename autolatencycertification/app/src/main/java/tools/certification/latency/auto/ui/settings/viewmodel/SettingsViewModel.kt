package tools.certification.latency.auto.ui.settings.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tools.certification.latency.auto.R
import tools.certification.latency.auto.ext.showToast
import tools.certification.latency.auto.ui.settings.data.Configuration
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val _menuList = MutableLiveData<List<String>>()
    val menuList: LiveData<List<String>> get() = _menuList
    private val menus = mutableListOf<String>()

    private val _speechList = MutableLiveData<List<String>>()
    val speechList: LiveData<List<String>> get() = _speechList
    private val speeches = mutableListOf<String>()

    private val _mode = MutableLiveData<String>()
    val mode: LiveData<String> get() = _mode

    var volumeNormal = 0.75f
    var minVolume = 0.1f // mode Volume
    var maxVolume = 1f

    var noiseNormal = 0f
    var minNoise = 0f // mode Noise
    var maxNoise = 1f

    private val utteranceMap = mapOf(
        Pair(HI_BIXBY, R.raw.utterance_hi_bixby),
        Pair("\"What time is it?\"", R.raw.utterance_what_time_is_it),
        Pair("\"What day is today?\"", R.raw.utterance_what_day_is_today),
        Pair("\"What is weather now?\"", R.raw.utterance_what_is_weather_now)
    )

    var modeMap = mapOf(
        Pair("Normal test", "normal"),
        Pair("Test with volume range", "volume"),
        Pair("Test with noise range", "noise")
    )
    var modeMapInverse = modeMap.inverseMap()

    var noiseMap = mutableMapOf<String, Int>()

    fun addSpeech(utterance: String) {
        if (speeches.size >= MAX_UTTERANCE)
            removeSpeech(speeches.last())
        speeches.add(utterance)
        _speechList.value = speeches

        menus.removeIf {
            it == utterance
        }
        _menuList.value = menus
    }

    fun removeSpeech(utter: String) {
        if (utter == HI_BIXBY) {
            Toast.makeText(getApplication(), "Can't remove $HI_BIXBY", Toast.LENGTH_SHORT).show()
            return
        }
        speeches.removeIf {
            it == utter
        }
        _speechList.value = speeches

        menus.add(utter)
        _menuList.value = menus
    }

    fun setMode(key: String) {
        _mode.postValue(modeMap[key])
    }

    fun initData() {
        val fields = R.raw::class.java.fields
        fields.forEach {
            if (!it.name.startsWith("noise"))
                return@forEach
            var s = it.name
            s = "N" + s.replace("_", " ").substring(1)
            noiseMap[s] = it.getInt(it)
        }

        menus.clear()
        speeches.clear()
        menus.getMenus()
        speeches.getSpeeches()
        _menuList.value = menus
        _speechList.value = speeches

        volumeNormal = 0.75f
        minVolume = 0.1f
        maxVolume = 1f

        noiseNormal = 0f
        minNoise = 0f
        maxNoise = 1f
    }

    private fun MutableList<String>.getMenus() {
        addAll(utteranceMap.keys)
        remove(HI_BIXBY)
    }

    private fun MutableList<String>.getSpeeches() {
        add(HI_BIXBY)
    }

    private fun <K, V> Map<K, V>.inverseMap() = map { Pair(it.value, it.key) }.toMap()

    @SuppressLint("SimpleDateFormat")
    fun exportSettings(context: Context, config: Configuration) =
        viewModelScope.launch(Dispatchers.IO) {
            val formatter = SimpleDateFormat("yyyyMMdd_HHmmss")
            val dateTime = formatter.format(Date())
            val folder = File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), "configs")
            if (!folder.exists()) {
                folder.mkdir()
            }
            val fileName = "config_$dateTime.json"
            val file = File(folder, fileName)
            val json = Json.encodeToString(config)
            file.outputStream().use { it.write(json.toByteArray()) }
            withContext(Dispatchers.Main) {
                context.showToast("Exported to $fileName")
            }
        }

    companion object {
        private const val HI_BIXBY = "\"Hi bixby\""
        private const val MAX_UTTERANCE = 2
    }
}
