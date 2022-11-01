package tools.certification.latency.auto.ui.quickcommand.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import tools.certification.latency.auto.data.database.entity.Utterance

@kotlinx.serialization.Serializable
data class QuickcommandResult (
    val id: Long = System.currentTimeMillis(),
    val wakeUp: Utterance = Utterance("Hi Bixby"),
    val ting: Utterance = Utterance("Ting"),
    val qcRequest: Utterance,
    val firstRespone: Utterance = Utterance(""),
    val secondRespone: Utterance = Utterance(""),
    val thirdRespone: Utterance = Utterance(""),
    val latency: Double = -1.0,
    val accuracy: Int = 0,
    val isFinished: Boolean = false,
)