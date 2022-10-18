package khoa.nv.applocker.service.observable

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import khoa.nv.applocker.util.helper.PermissionChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionCheckerObservable @Inject constructor(@ApplicationContext private val context: Context) {

    private val delay = 1000L * 60 * 30

    fun get() = flow {
        while (currentCoroutineContext().isActive) {
            emit(!PermissionChecker.isAllPermissionChecked(context))
            delay(delay)
        }
    }.flowOn(Dispatchers.IO)
}