package khoa.nv.applocker.data

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDataProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun fetchInstalledApps(): Flow<List<AppData>> = flow {
//        val appsData = context.packageManager.run {
//            getInstalledApplications(0)
//                .filter { appInfo -> appInfo.packageName != context.packageName }
//                .map { appInfo ->
//                    AppData(
//                        getApplicationLabel(appInfo).toString(),
//                        appInfo.packageName,
//                        getApplicationIcon(appInfo)
//                    )
//                }
//        }

        val appsData = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            .run { context.packageManager.queryIntentActivities(this, 0) }
            .filter { resolveInfo -> resolveInfo.activityInfo.packageName != context.packageName }
            .map { resolveInfo ->
                with(resolveInfo) {
                    AppData(
                        loadLabel(context.packageManager).toString(),
                        activityInfo.packageName,
                        loadIcon(context.packageManager)
                    )
                }
            }
        emit(appsData)
    }.flowOn(Dispatchers.IO)
}