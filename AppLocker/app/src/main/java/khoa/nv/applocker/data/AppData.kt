package khoa.nv.applocker.data

import android.graphics.drawable.Drawable
import khoa.nv.applocker.data.database.lockedapps.LockedAppEntity

data class AppData(
    val appName: String,
    val packageName: String,
    val appIconDrawable: Drawable
) {
    fun toEntity() = LockedAppEntity(packageName)
}