package khoa.nv.applocker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import khoa.nv.applocker.data.AppData
import khoa.nv.applocker.data.AppDataProvider
import khoa.nv.applocker.data.database.lockedapps.LockedAppEntity
import khoa.nv.applocker.repository.LockedAppRepository
import khoa.nv.applocker.repository.PatternRepository
import khoa.nv.applocker.ui.main.lockedapps.AppLockItemViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appDataProvider: AppDataProvider,
    private val lockedAppRepository: LockedAppRepository,
    private val patternRepository: PatternRepository
) : ViewModel() {

    private val _appLock = MutableStateFlow(emptyList<AppLockItemViewState>())
    val appLock: StateFlow<List<AppLockItemViewState>> = _appLock

    val isPatternCreatedFlow = patternRepository.isPatternCreated()

    fun lockApp(packageName: String) {
        lockedAppRepository.lockApp(LockedAppEntity(packageName)).launchIn(viewModelScope)
    }

    fun unlockApp(packageName: String) {
        lockedAppRepository.unlockApp(packageName).launchIn(viewModelScope)
    }

    fun getAppsLock() {
        appDataProvider.fetchInstalledApps()
            .combine(lockedAppRepository.getLockedApps()) { appsData, lockedApps ->
                _appLock.value = orderAppsByLockStatus(appsData, lockedApps)
            }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun orderAppsByLockStatus(
        appsData: List<AppData>,
        lockedApps: List<LockedAppEntity>
    ): List<AppLockItemViewState> {

        val lockedAppsData = lockedApps.flatMap { lockedApp ->
            appsData.filter { appData ->
                lockedApp.packageName == appData.packageName
            }
        }

        val notLockedAppsData = appsData.subtract(lockedAppsData)

        val results = lockedAppsData.map { AppLockItemViewState(it, true) } +
                notLockedAppsData.map { AppLockItemViewState(it) }

        return results.sortedWith(
            compareBy(
                AppLockItemViewState::isNotLocked,
                AppLockItemViewState::getAppName
            )
        )
    }
}