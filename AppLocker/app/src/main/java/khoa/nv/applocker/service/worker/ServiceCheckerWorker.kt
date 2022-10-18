package khoa.nv.applocker.service.worker

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import khoa.nv.applocker.service.AppLockerService

class ServiceCheckerWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    override fun doWork(): Result {
        ContextCompat.startForegroundService(applicationContext, Intent(applicationContext, AppLockerService::class.java))
        return Result.success()
    }
}