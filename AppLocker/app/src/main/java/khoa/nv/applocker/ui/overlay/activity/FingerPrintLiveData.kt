package khoa.nv.applocker.ui.overlay.activity

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint
import khoa.nv.applocker.ui.overlay.FingerPrintResultData

class FingerPrintLiveData(context: Context) : MutableLiveData<FingerPrintResultData>(),
    BaseFingerprint.ExceptionListener, BaseFingerprint.IdentifyListener {

    private val fingerprintIdentify: FingerprintIdentify = FingerprintIdentify(context)

    init {
        try {
            fingerprintIdentify.setSupportAndroidL(true)
            fingerprintIdentify.init()
        } catch (exception: Exception) {

        }
    }

    override fun onActive() {
        super.onActive()
        fingerprintIdentify.startIdentify(3, this)
    }

    override fun onCatchException(exception: Throwable?) {
        value = FingerPrintResultData.error(exception?.message ?: "")
    }

    override fun onSucceed() {
        value = FingerPrintResultData.matched()
    }

    override fun onNotMatch(availableTimes: Int) {
        value = FingerPrintResultData.notMatched(availableTimes)
    }

    override fun onFailed(isDeviceLocked: Boolean) {
        value = FingerPrintResultData.error("Fingerprint error")
    }

    override fun onStartFailedByDeviceLocked() {
        value = FingerPrintResultData.error("Fingerprint error")
    }

    override fun onInactive() {
        super.onInactive()
        fingerprintIdentify.cancelIdentify()
    }
}