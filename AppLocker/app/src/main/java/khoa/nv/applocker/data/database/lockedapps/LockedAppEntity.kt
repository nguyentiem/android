package khoa.nv.applocker.data.database.lockedapps

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locked_app")
data class LockedAppEntity(
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    val packageName: String
) {
    fun parsePackageName(): String {
        val index = packageName.indexOf("/")
        if (index == -1) {
            return packageName
        }
        return packageName.substring(0, index)
    }
}
