package tools.certification.latency.auto.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tools.certification.latency.auto.data.database.dao.TestResultDao
import tools.certification.latency.auto.data.database.entity.TestResult

@Database(entities = [TestResult::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun testResultDao(): TestResultDao

    companion object {
        fun getInstance(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "database-name").build()
    }
}
