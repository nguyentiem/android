package tools.certification.latency.auto.ui.settings.data

import android.os.Parcelable
import androidx.annotation.RawRes
import kotlinx.parcelize.Parcelize
import tools.certification.latency.auto.R

@Parcelize
data class SpeakerSettings(
    val mode: SpeakerMode = SpeakerMode.NORMAL,
    val repeat: Int = 1,
    val speaker: Float = 0.75f,
    val minSpeaker: Float = 0.1f,
    val maxSpeaker: Float = 1f,
    val noise: Float = 0f,
    val minNoise: Float = 0f,
    val maxNoise: Float = 1f,
    @RawRes val noiseType: Int = R.raw.noise_rain_light
) : Parcelable
