package khoa.nv.applocker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import khoa.nv.applocker.data.database.lockedapps.LockedAppDao
import khoa.nv.applocker.data.database.lockedapps.LockedAppEntity
import khoa.nv.applocker.data.database.pattern.PatternDao
import khoa.nv.applocker.data.database.pattern.PatternEntity

@Database(entities = [PatternEntity::class, LockedAppEntity::class], version = 1)
abstract class AppLockerDatabase : RoomDatabase() {
    abstract fun getPatternDao(): PatternDao
    abstract fun getLockedAppDao(): LockedAppDao
}