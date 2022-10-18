package khoa.nv.applocker.ui.overlay

import khoa.nv.applocker.ui.overlay.FingerPrintResult.*

enum class FingerPrintResult {
    SUCCESS, NOT_MATCHED, ERROR
}

data class FingerPrintResultData(
    val fingerPrintResult: FingerPrintResult,
    val availableTimes: Int = 0,
    val errorMessage: String? = null
) {

    fun isSuccess() = fingerPrintResult == SUCCESS

    fun isNotSuccess() = fingerPrintResult != SUCCESS

    companion object {

        fun matched() = FingerPrintResultData(SUCCESS)

        fun notMatched(availableTimes: Int) = FingerPrintResultData(
            fingerPrintResult = NOT_MATCHED,
            availableTimes = availableTimes
        )

        fun error(errorMessage: String) =
            FingerPrintResultData(
                fingerPrintResult = ERROR,
                errorMessage = errorMessage
            )
    }
}