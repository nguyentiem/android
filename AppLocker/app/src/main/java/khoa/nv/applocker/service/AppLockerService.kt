package khoa.nv.applocker.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.hilt.android.AndroidEntryPoint
import khoa.nv.applocker.data.AppLockerPreferences
import khoa.nv.applocker.data.SystemPackages
import khoa.nv.applocker.data.database.lockedapps.LockedAppEntity
import khoa.nv.applocker.repository.LockedAppRepository
import khoa.nv.applocker.repository.PatternRepository
import khoa.nv.applocker.service.notification.ServiceNotificationManager
import khoa.nv.applocker.service.notification.ServiceNotificationManager.Companion.NOTIFICATION_ID_APPLOCKER_SERVICE
import khoa.nv.applocker.service.observable.AppForegroundObservable
import khoa.nv.applocker.service.observable.PermissionCheckerObservable
import khoa.nv.applocker.ui.overlay.activity.OverlayValidationActivity
import khoa.nv.applocker.ui.overlay.view.OverlayLayoutParams
import khoa.nv.applocker.ui.overlay.view.OverlayPatternView
import khoa.nv.applocker.ui.overlay.activity.NewInstalledAppActivity
import khoa.nv.applocker.util.helper.LOCK_APP_RECEIVER
import khoa.nv.applocker.util.helper.UNLOCK_APP_RECEIVER
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppLockerService : Service() {

    @Inject
    lateinit var serviceNotificationManager: ServiceNotificationManager

    @Inject
    lateinit var appForegroundObservable: AppForegroundObservable

    @Inject
    lateinit var permissionCheckerObservable: PermissionCheckerObservable

    @Inject
    lateinit var lockedAppRepository: LockedAppRepository

    @Inject
    lateinit var patternRepository: PatternRepository

    @Inject
    lateinit var appLockerPreferences: AppLockerPreferences

    private lateinit var overlayPatternView: OverlayPatternView
    private val windowManager: WindowManager by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }
    private lateinit var overlayParams: WindowManager.LayoutParams

    private var isOverlayShowing = false
    private var lastForegroundAppPackage: String? = null
    private var foregroundAppJob: Job? = null
    private var lockedAppJob: Job? = null
    private val mainScope = MainScope()
    private val lockedAppPackageSet: HashSet<String> = HashSet()

    private val screenOnOffReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> observeForegroundApplication()
                Intent.ACTION_SCREEN_OFF -> stopForegroundApplicationObserver()
            }
        }
    }

    private val lockUnlockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                LOCK_APP_RECEIVER -> {
                    lockApp(intent.getStringExtra(Intent.EXTRA_TEXT)!!)
                    serviceNotificationManager.hideNewAppInstalledNotification()
                }
                UNLOCK_APP_RECEIVER -> unlockApp(intent.getStringExtra(Intent.EXTRA_TEXT)!!)
            }
        }
    }

    private val installUninstallReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_PACKAGE_ADDED -> {
                    if (!intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                        val packageName = intent.data!!.encodedSchemeSpecificPart
                        if (appLockerPreferences.getIsNewAppAlert()) {
                            showNewInstalledAppPopup(packageName)
                        } else {
                            serviceNotificationManager.createNewAppInstalledNotification(packageName)
                        }
                    }
                }
                Intent.ACTION_PACKAGE_FULLY_REMOVED -> {
                    unlockApp(intent.data!!.encodedSchemeSpecificPart)
                }
            }
        }
    }

    init {
        lockedAppPackageSet.addAll(SystemPackages.getSystemPackages())
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()

//        initOverlayView()
        registerScreenReceiver()
        registerInstallUninstallReceiver()
        registerLockUnlockReceiver()
        observeLockedApps()
        observeForegroundApplication()
        observePermissionChecker()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val fakeAppId = appLockerPreferences.getSelectedFakeAppId()
        serviceNotificationManager.createNotification(fakeAppId).also {
            startForeground(NOTIFICATION_ID_APPLOCKER_SERVICE, it)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        unregisterScreenReceiver()
        unregisterInstallUninstallReceiver()
        unregisterLockUnlockReceiver()
        stopForegroundApplicationObserver()
        super.onDestroy()
    }

    private fun initOverlayView() {
        overlayPatternView = OverlayPatternView(applicationContext).apply {
            observePattern { pattern -> checkPattern(pattern) }
        }
        overlayParams = OverlayLayoutParams.get()
    }

    private fun showNewInstalledAppPopup(packageName: String) {
        NewInstalledAppActivity.newIntent(applicationContext, packageName)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .also { startActivity(it) }
    }

    private fun observeLockedApps() {
        if (lockedAppJob?.isActive == true)
            return
        lockedAppJob = mainScope.launch {
            lockedAppRepository.getLockedApps()
                .collect { lockedApps ->
                    lockedAppPackageSet.apply {
                        clear()
                        addAll(lockedApps.map { it.packageName })
                        addAll(SystemPackages.getSystemPackages())
                    }
                }
        }
    }

    private fun checkPattern(pattern: String) {
        mainScope.launch {
            patternRepository.getPattern().collect { savedPattern ->
                onPatternValidated(savedPattern.pattern == pattern)
            }
        }
    }

    private fun observeForegroundApplication() {
        if (foregroundAppJob?.isActive == true)
            return
        foregroundAppJob = mainScope.launch {
            appForegroundObservable.get()
                .collect { foregroundAppPackage -> onAppForeground(foregroundAppPackage) }
        }
    }

    private fun stopForegroundApplicationObserver() {
        foregroundAppJob?.cancel()
    }

    private fun observePermissionChecker() {
        mainScope.launch {
            permissionCheckerObservable.get()
                .collect { isPermissionNeed ->
                    if (isPermissionNeed) {
                        serviceNotificationManager.createPermissionNeedNotification()
                        stopSelf()
                    } else {
                        serviceNotificationManager.hidePermissionNotification()
                    }
                }
        }
    }

    private fun onPatternValidated(isPatternCorrect: Boolean) {
        if (isPatternCorrect) {
            overlayPatternView.notifyDrawnCorrect()
            hideOverlay()
        } else {
            overlayPatternView.notifyDrawnWrong()
        }
    }

    private fun onAppForeground(foregroundAppPackage: String) {
//        hideOverlay()

        if (lockedAppPackageSet.contains(foregroundAppPackage)) {
            val intent =
                OverlayValidationActivity.newIntent(applicationContext, foregroundAppPackage)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (lastForegroundAppPackage != applicationContext.packageName) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
        }

        lastForegroundAppPackage = foregroundAppPackage
    }

    private fun hideOverlay() {
        if (isOverlayShowing) {
            isOverlayShowing = false
            windowManager.removeViewImmediate(overlayPatternView)
        }
    }

    private fun showOverlay(lockedAppPackageName: String) {
        if (isOverlayShowing.not()) {
            isOverlayShowing = true
            with(overlayPatternView) {
                setHiddenDrawingMode(appLockerPreferences.getHiddenDrawingMode())
                setAppPackageName(lockedAppPackageName)
                windowManager.addView(this, overlayParams)
            }
        }
    }

    private fun lockApp(packageName: String) {
        mainScope.launch {
            val lockedAppEntity = LockedAppEntity(packageName)
            lockedAppRepository.lockApp(lockedAppEntity).collect()
            lockedAppPackageSet.add(packageName)
        }
    }

    private fun unlockApp(packageName: String) {
        mainScope.launch {
            lockedAppRepository.unlockApp(packageName).collect()
            lockedAppPackageSet.remove(packageName)
        }
    }

    private fun registerScreenReceiver() {
        IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }.also {
            registerReceiver(screenOnOffReceiver, it)
        }
    }

    private fun registerInstallUninstallReceiver() {
        IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
            addDataScheme("package")
        }.also {
            registerReceiver(installUninstallReceiver, it)
        }
    }

    private fun registerLockUnlockReceiver() {
        IntentFilter().apply {
            addAction(LOCK_APP_RECEIVER)
            addAction(UNLOCK_APP_RECEIVER)
        }.also {
            LocalBroadcastManager.getInstance(applicationContext)
                .registerReceiver(lockUnlockReceiver, it)
        }
    }

    private fun unregisterLockUnlockReceiver() {
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(lockUnlockReceiver)
    }

    private fun unregisterScreenReceiver() {
        unregisterReceiver(screenOnOffReceiver)
    }

    private fun unregisterInstallUninstallReceiver() {
        unregisterReceiver(installUninstallReceiver)
    }

    companion object {
        private const val TAG = "AppLockerService"

        fun newIntent(context: Context) = Intent(context, AppLockerService::class.java)

        fun startForeground(context: Context) =
            ContextCompat.startForegroundService(context, newIntent(context))
    }
}

