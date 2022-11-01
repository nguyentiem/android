package tools.certification.latency.auto.utils

import android.os.Environment

object StorageUtils {
    fun isExternalStorageAvailable(): Boolean {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == extStorageState
    }

    fun isExternalStorageReadOnly(): Boolean {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState
    }
}
