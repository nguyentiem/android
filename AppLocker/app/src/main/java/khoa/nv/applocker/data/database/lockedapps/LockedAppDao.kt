package khoa.nv.applocker.data.database.lockedapps

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LockedAppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun lockApp(lockedAppEntity: LockedAppEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun lockApps(lockedAppEntityList: List<LockedAppEntity>)

    @Query("SELECT * FROM locked_app")
    suspend fun getLockedApps(): List<LockedAppEntity>

    @Query("DELETE FROM locked_app WHERE package_name = :packageName")
    suspend fun unlockApp(packageName: String)

    @Query("DELETE FROM locked_app")
    suspend fun unlockAll()
}