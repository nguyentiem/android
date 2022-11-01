package tools.certification.latency.auto.ui.settings.data

@kotlinx.serialization.Serializable
data class Configuration(
    val utterance: String? = null,
    val mode: String,
    val repeat: Int,
    val volume: Float,
    val minVolume: Float,
    val maxVolume: Float,
    val noise: Float,
    val minNoise: Float,
    val maxNoise: Float,
    val noiseType: String
)
