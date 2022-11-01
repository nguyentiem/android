package tools.certification.latency.auto.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@kotlinx.serialization.Serializable
@Entity(tableName = "test_result")
data class TestResult(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    @Embedded(prefix = "wake_up")
    val wakeUp: Utterance = Utterance("Hi Bixby"),
    @Embedded(prefix = "ting")
    val ting: Utterance = Utterance("Ting"),
    @Embedded(prefix = "request")
    val request: Utterance,
    @Embedded(prefix = "response")
    val response: Utterance = Utterance(""),
    @ColumnInfo(name = "latency")
    val latency: Double = -1.0,
    @ColumnInfo(name = "accuracy")
    val accuracy: Int = 0,
    @ColumnInfo(name = "is_finished")
    val isFinished: Boolean = false,
    @ColumnInfo(name = "volume")
    var volume: Float = 0f,
    @ColumnInfo(name = "noise")
    var noise: Float = 0f,
    @ColumnInfo(name = "wake_up_latency")
    var wakeUpLatency: Long = 0L,
    @ColumnInfo(name = "response_latency")
    var responseLatency: Long = 0L,
) {
    fun updateResult() {
        if (this.isFinished) {
            wakeUpLatency = this.ting.start - this.wakeUp.end
            if (this.response.command.isNotEmpty())
                responseLatency = this.response.start - this.request.end
        }
    }
}
