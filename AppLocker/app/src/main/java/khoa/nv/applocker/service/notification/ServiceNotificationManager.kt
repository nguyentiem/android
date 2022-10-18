package khoa.nv.applocker.service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import khoa.nv.applocker.R
import khoa.nv.applocker.service.LockUnlockReceiver
import khoa.nv.applocker.service.LockUnlockReceiver.Companion.NOTIFICATION_LOCK_APP_RECEIVER
import khoa.nv.applocker.ui.fakeapps.FakeAppDataProvider
import khoa.nv.applocker.ui.main.MainActivity
import khoa.nv.applocker.ui.calculator.CalculatorActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceNotificationManager @Inject constructor(@ApplicationContext private val context: Context) {

    fun createNotification(fakeAppId: Int): Notification {
        createAppLockerServiceChannel()

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val resultPendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, CalculatorActivity::class.java),
                flag
            )

        val fakeAppItem = FakeAppDataProvider.fakeApps[fakeAppId]

        return NotificationCompat.Builder(context, CHANNEL_ID_APPLOCKER_SERVICE)
            .setSmallIcon(fakeAppItem.appIconRes)
            .setContentTitle(fakeAppItem.content)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setContentIntent(resultPendingIntent)
            .build()
    }

    fun createPermissionNeedNotification(): Notification {
        createAppLockerNotificationChannel()

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val resultPendingIntent =
            PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), flag)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_APPLOCKER_NOTIFICATION)
            .setSmallIcon(R.drawable.ic_round_lock_24px)
            .setContentTitle(context.getString(R.string.notification_permission_need_title))
            .setContentText(context.getString(R.string.notification_permission_need_description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setContentIntent(resultPendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED, notification)
        return notification
    }

    fun createNewAppInstalledNotification(packageName: String): Notification {
        createAppLockerNotificationChannel()
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val resultPendingIntent = PendingIntent.getActivity(
                context, 0,
                MainActivity.newInstalledAppIntent(context, packageName), flag
            )

        val lockIntent = Intent(context, LockUnlockReceiver::class.java).apply {
            action = NOTIFICATION_LOCK_APP_RECEIVER
            putExtra(Intent.EXTRA_TEXT, packageName)
        }
        val lockPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(context, 0, lockIntent, 0)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_APPLOCKER_NOTIFICATION)
            .setSmallIcon(R.drawable.ic_round_lock_24px)
            .setContentTitle("New app installed")
            .setContentText("Do you want to block this app")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(resultPendingIntent)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Do you want to block this app"))
            .addAction(R.drawable.ic_locked_24px, "Lock", lockPendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID_NEW_APP_INSTALLED, notification)
        return notification
    }

    fun hidePermissionNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED)
    }

    fun hideNewAppInstalledNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_NEW_APP_INSTALLED)
    }

    fun hideAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }

    private fun createAppLockerServiceChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID_APPLOCKER_SERVICE, "Lock app service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setAllowBubbles(false)
                }
                setSound(null, null)
                setShowBadge(false)
                enableVibration(false)
                enableLights(false)
                description = "Show status of lock app service"
                lockscreenVisibility = Notification.VISIBILITY_SECRET
            }.also {
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(it)
            }
        }
    }

    private fun createAppLockerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID_APPLOCKER_NOTIFICATION, "Other notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).also {
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(it)
            }
        }
    }

    companion object {
        private const val CHANNEL_ID_APPLOCKER_SERVICE = "CHANNEL_ID_APPLOCKER_SERVICE"
        private const val CHANNEL_ID_APPLOCKER_NOTIFICATION = "CHANNEL_ID_APPLOCKER_NOTIFICATION"
        const val NOTIFICATION_ID_APPLOCKER_SERVICE = 1
        private const val NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED = 2
        private const val NOTIFICATION_ID_NEW_APP_INSTALLED = 3
    }
}