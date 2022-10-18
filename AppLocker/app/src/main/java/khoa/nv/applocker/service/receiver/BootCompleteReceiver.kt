package khoa.nv.applocker.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import khoa.nv.applocker.data.AppLockerPreferences
import khoa.nv.applocker.service.AppLockerService
import javax.inject.Inject

@AndroidEntryPoint
class BootCompleteReceiver : BroadcastReceiver() {

    @Inject
    lateinit var appLockerPreferences: AppLockerPreferences

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            if (context != null && appLockerPreferences.getServiceEnabled()) {
                ContextCompat.startForegroundService(context, AppLockerService.newIntent(context))
            }
        }
    }
}