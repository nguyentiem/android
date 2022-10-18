package khoa.nv.applocker.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import khoa.nv.applocker.util.helper.LOCK_APP_RECEIVER

class LockUnlockReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == NOTIFICATION_LOCK_APP_RECEIVER) {
            intent.action = LOCK_APP_RECEIVER
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
        }
    }

    companion object {
        const val NOTIFICATION_LOCK_APP_RECEIVER = "khoa.nv.applocker.NOTIFICATION_LOCK"

        fun newIntent(context: Context, packageName: String) =
            Intent(context, LockUnlockReceiver::class.java)
                .setAction(NOTIFICATION_LOCK_APP_RECEIVER)
                .putExtra(Intent.EXTRA_TEXT, packageName)
    }
}