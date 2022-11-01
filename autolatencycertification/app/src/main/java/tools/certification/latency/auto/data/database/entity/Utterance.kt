package tools.certification.latency.auto.data.database.entity

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

@kotlinx.serialization.Serializable
data class Utterance(
    val command: String,
    val start: Long = -1L,
    val end: Long = -1L
) {
    val isStarted get() = start != -1L

    val isNotStarted get() = start == -1L

    val isCompleted get() = end != -1L

    val isNotCompleted get() = end == -1L

    val startText get() = formatByPattern(start)

    val endText get() = formatByPattern(end)

    @SuppressLint("SimpleDateFormat")
    private fun formatByPattern(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS")
        return formatter.format(Date(millis))
    }
}
