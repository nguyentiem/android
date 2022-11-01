package tools.certification.latency.auto.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import tools.certification.latency.auto.data.database.entity.TestResult

@Dao
interface TestResultDao {
    @Query("SELECT * FROM test_result")
    fun findAll(): Flow<List<TestResult>>

    @Insert
    fun saveAll(vararg testResults: TestResult)

    @Update
    fun update(testResult: TestResult)

    @Delete
    fun delete(testResult: TestResult)

    @Query("DELETE FROM test_result")
    fun clear()
}
