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
class AppBackgroundObservable @Inject constructor(@ApplicationContext private val context: Context) {

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getBackgroundObservableHigherLollipop() = flow {
        val usm = context.getSystemService("usagestats") as UsageStatsManager

        while (currentCoroutineContext().isActive) {
            var packageName: String? = null
            val time = System.currentTimeMillis()
            val usageEvents = usm.queryEvents(time - 5000, time)
            val event = UsageEvents.Event()
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.ACTIVITY_STOPPED) {
                    packageName = event.packageName
                }
            }
            emit(packageName)
            delay(100)
        }
    }
        .conflate()
        .filterNotNull()
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.M)
    fun getBackgroundObservableLowerLollipop() = flow {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        while (currentCoroutineContext().isActive) {
            emit(am.appTasks[0].taskInfo.topActivity)
            delay(100)
        }
    }
        .conflate()
        .mapNotNull { it?.packageName }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)
}