package khoa.nv.applocker.util.helper

import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun overlayIntent(packageName: String): Intent {
    return Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
}

fun usageAccessIntent(): Intent {
    return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
}

const val LOCK_APP_RECEIVER = "khoa.nv.applocker.LOCK"
const val UNLOCK_APP_RECEIVER = "khoa.nv.applocker.UNLOCK"