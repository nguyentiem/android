package khoa.nv.applocker.data.database.pattern

import androidx.room.*

@Dao
interface PatternDao {
    @Transaction
    suspend fun createPattern(patternEntity: PatternEntity) {
        deletePattern()
        insertPattern(patternEntity)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPattern(patternEntity: PatternEntity)

    @Query("SELECT * FROM pattern LIMIT 1")
    suspend fun getPattern(): PatternEntity

    @Query("SELECT count(*) FROM pattern")
    suspend fun isPatternCreated(): Int

    @Query("DELETE FROM pattern")
    suspend fun deletePattern()
}