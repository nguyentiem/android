package tools.certification.latency.auto.ext

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(@StringRes id: Int) {
    Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
}
