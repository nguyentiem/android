package khoa.nv.applocker.ui.fakeapps

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import khoa.nv.applocker.data.AppLockerPreferences
import khoa.nv.applocker.service.AppLockerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FakeAppViewModel @Inject constructor(
    private val app: Application,
    private val appLockerPreferences: AppLockerPreferences
) : AndroidViewModel(app) {
    private val _fakeAppState = MutableStateFlow(emptyList<FakeAppItemViewState>())
    val fakeAppState: StateFlow<List<FakeAppItemViewState>> = _fakeAppState

    init {
        viewModelScope.launch(Dispatchers.Default) {
            val selectedFakeAppId = appLockerPreferences.getSelectedFakeAppId()
            FakeAppDataProvider.fakeApps.apply {
                this[selectedFakeAppId].isChecked = true
            }.also {
                _fakeAppState.value = it
            }
        }
    }

    fun onSelectedItemChanged(old: FakeAppItemViewState, selected: FakeAppItemViewState) {
        appLockerPreferences.setSelectedFakeAppId(selected.id)
        val pm = app.packageManager
        pm.setComponentEnabledSetting(
            ComponentName(app, "${app.packageName}${old.alias}"),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        pm.setComponentEnabledSetting(
            ComponentName(app, "${app.packageName}${selected.alias}"),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        AppLockerService.startForeground(app)
    }
}