package khoa.nv.applocker.ui.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import dagger.hilt.android.lifecycle.HiltViewModel
import khoa.nv.applocker.data.AppDataProvider
import khoa.nv.applocker.data.AppLockerPreferences
import khoa.nv.applocker.repository.LockedAppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val app: Application,
    private val appDataProvider: AppDataProvider,
    private val lockedAppRepository: LockedAppRepository,
    private val appLockerPreferences: AppLockerPreferences
) : ViewModel() {
    private val _settingState = MutableStateFlow(
        SettingsViewState(
            appLockerPreferences.getHiddenDrawingMode(),
            appLockerPreferences.getFingerPrintEnabled()
        )
    )
    private val _fingerPrintStatusState = MutableStateFlow(
        with(FingerprintIdentify(app)) {
            init()
            FingerPrintStatusViewState(
                isHardwareEnable,
                isRegisteredFingerprint
            )
        }
    )
    private val _newAppAlertState = MutableStateFlow(appLockerPreferences.getIsNewAppAlert())
    val newAppAlertState: StateFlow<Boolean> = _newAppAlertState
    val settingState: StateFlow<SettingsViewState> = _settingState
    val fingerPrintStatusState: StateFlow<FingerPrintStatusViewState> = _fingerPrintStatusState

    fun setNewAppAlert(newAppAlert: Boolean) {
        appLockerPreferences.setIsNewAppAlert(newAppAlert)
        _newAppAlertState.value = newAppAlert
    }

    fun setHiddenDrawingMode(hiddenDrawingMode: Boolean) {
        appLockerPreferences.setHiddenDrawingMode(hiddenDrawingMode)
        val currentSettingState = _settingState.value
        currentSettingState.isHiddenDrawingMode = hiddenDrawingMode
        _settingState.value = currentSettingState
    }

    fun setEnableFingerPrint(fingerPrintEnabled: Boolean) {
        appLockerPreferences.setFingerPrintEnable(fingerPrintEnabled)
        val currentSettingState = _settingState.value
        currentSettingState.isFingerPrintEnabled = fingerPrintEnabled
        _settingState.value = currentSettingState
    }
}