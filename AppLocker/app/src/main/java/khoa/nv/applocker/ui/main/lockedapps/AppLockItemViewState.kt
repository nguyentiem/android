package khoa.nv.applocker.ui.main.lockedapps

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import khoa.nv.applocker.R
import khoa.nv.applocker.data.AppData

class AppLockItemViewState(
    private val appData: AppData,
    var isLocked: Boolean = false
) {
    fun getAppName() = appData.appName

    fun getPackageName() = appData.packageName

    fun getLockIcon(context: Context): Drawable? {
        return if (isLocked) {
            ContextCompat.getDrawable(context, R.drawable.ic_locked_24px)
        } else {
            ContextCompat.getDrawable(context, R.drawable.ic_lock_open_24px)
        }
    }

    fun isNotLocked() = !isLocked

    fun getAppIcon(): Drawable = appData.appIconDrawable
}