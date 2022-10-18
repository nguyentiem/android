package khoa.nv.applocker.service.observable

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppForegroundObservable @Inject constructor(@ApplicationContext private val context: Context) {

    fun get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> getForegroundObservableHigherLollipop()
        else -> getForegroundObservableLowerLollipop()
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getForegroundObservableHigherLollipop() = flow {
        val usm = context.getSystemService("usagestats") as UsageStatsManager

        while (currentCoroutineContext().isActive) {
            var packageName: String? = null
            val time = System.currentTimeMillis()
            val usageEvents = usm.queryEvents(time - 5000, time)
            val event = UsageEvents.Event()
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    packageName = event.packageName
                }
            }
            emit(packageName)
            delay(100)
        }
    }
        .conflate()
        .filterNotNull()
        .filter { it != context.packageName }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    private fun getForegroundObservableLowerLollipop() = flow {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        while (currentCoroutineContext().isActive) {
            emit(am.getRunningTasks(1)[0].topActivity)
            delay(100)
        }
    }
        .conflate()
        .mapNotNull { it?.packageName }
        .filter { it != context.packageName }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)
}