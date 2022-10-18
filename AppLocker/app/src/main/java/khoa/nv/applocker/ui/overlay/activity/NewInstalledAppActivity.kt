package khoa.nv.applocker.ui.overlay.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import khoa.nv.applocker.R
import khoa.nv.applocker.databinding.ActivityNewInstalledAppBinding
import khoa.nv.applocker.service.LockUnlockReceiver

class NewInstalledAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewInstalledAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_installed_app)
        setAppPackageName()
    }

    private fun setAppPackageName() {
        val appPackageName = intent.getStringExtra(KEY_PACKAGE_NAME)!!
        val applicationInfo =
            packageManager.getApplicationInfo(appPackageName, PackageManager.GET_META_DATA)
        binding.title.text = packageManager.getApplicationLabel(applicationInfo)
        try {
            val icon = packageManager.getApplicationIcon(appPackageName)
            binding.iconApp.setImageDrawable(icon)
        } catch (e: PackageManager.NameNotFoundException) {
            binding.iconApp.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_round_lock_24px
                )
            )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUi()
        }
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    companion object {

        private const val KEY_PACKAGE_NAME = "KEY_PACKAGE_NAME"

        fun newIntent(context: Context, packageName: String): Intent {
            val intent = Intent(context, NewInstalledAppActivity::class.java)
            intent.putExtra(KEY_PACKAGE_NAME, packageName)
            return intent
        }
    }

    fun onLaterButtonClicked(view: android.view.View) {
        finish()
    }

    fun onLockButtonClicked(view: android.view.View) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(
            LockUnlockReceiver.newIntent(this, intent.getStringExtra(KEY_PACKAGE_NAME)!!)
        )
        finish()
    }
}