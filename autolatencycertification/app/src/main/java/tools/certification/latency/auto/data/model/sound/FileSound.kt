package tools.certification.latency.auto.data.model.sound

import java.io.File
import java.nio.ByteBuffer
import java.nio.ShortBuffer

data class FileSound(
    private val inputFile: File? = null,
    private val fileType: String? = null,
    private val fileSize: Int = 0,
    private val avgBitRate: Int = 0,
    private val sampleRate: Int = 0,
    private val channels: Int = 0,
    private val numSamples: Int = 0,
    private val decodedBytes: ByteBuffer? = null,
    private val decodedSamples: ShortBuffer? = null,
    private val numFrames: Int = 0,
    private val frameGains: List<Int> = emptyList(),
    private val frameLens: List<Int> = emptyList(),
    private val frameOffsets: List<Int> = emptyList()
) {
    val samplesPerFrame = 1024
}
