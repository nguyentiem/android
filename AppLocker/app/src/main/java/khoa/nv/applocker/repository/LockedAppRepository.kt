package khoa.nv.applocker.repository

import androidx.annotation.WorkerThread
import khoa.nv.applocker.data.database.lockedapps.LockedAppDao
import khoa.nv.applocker.data.database.lockedapps.LockedAppEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@WorkerThread
@Singleton
class LockedAppRepository @Inject constructor(private val lockedAppDao: LockedAppDao) {

    fun lockApp(lockedAppEntity: LockedAppEntity) = flow<Unit> {
        lockedAppDao.lockApp(lockedAppEntity)
    }.flowOn(Dispatchers.IO)

    fun lockApps(lockedAppEntities: List<LockedAppEntity>) = flow<Unit> {
        lockedAppDao.lockApps(lockedAppEntities)
    }.flowOn(Dispatchers.IO)

    fun getLockedApps() = flow {
        emit(lockedAppDao.getLockedApps())
    }.flowOn(Dispatchers.IO)

    fun unlockApp(packageName: String) = flow<Unit> {
        lockedAppDao.unlockApp(packageName)
    }.flowOn(Dispatchers.IO)

    fun unlockAll() = flow<Unit> {
        lockedAppDao.unlockAll()
    }.flowOn(Dispatchers.IO)

}