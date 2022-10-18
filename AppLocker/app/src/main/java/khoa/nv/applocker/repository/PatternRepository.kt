package khoa.nv.applocker.repository

import androidx.annotation.WorkerThread
import khoa.nv.applocker.data.database.pattern.PatternDao
import khoa.nv.applocker.data.database.pattern.PatternEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@WorkerThread
@Singleton
class PatternRepository @Inject constructor(private val patternDao: PatternDao) {

    fun createPattern(patternEntity: PatternEntity) = flow<Unit> {
        patternDao.createPattern(patternEntity)
    }.flowOn(Dispatchers.IO)

    fun getPattern() = flow {
        emit(patternDao.getPattern())
    }.flowOn(Dispatchers.IO)

    fun isPatternCreated() = flow {
        emit(patternDao.isPatternCreated() != 0)
    }.flowOn(Dispatchers.IO)
}