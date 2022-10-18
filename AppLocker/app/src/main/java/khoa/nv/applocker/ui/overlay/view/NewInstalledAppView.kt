package khoa.nv.applocker.ui.overlay.view

import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import khoa.nv.applocker.R
import khoa.nv.applocker.databinding.ViewNewAppInstalledBinding

class NewInstalledAppView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private var onLockAppButtonClicked: (() -> Unit)? = null
    private var onLaterButtonClicked: (() -> Unit)? = null

    private val binding: ViewNewAppInstalledBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.view_new_app_installed,
            this,
            true
        )

    init {
        binding.buttonLater.setOnClickListener {
            onLaterButtonClicked?.invoke()
        }
        binding.buttonLock.setOnClickListener {
            onLockAppButtonClicked?.invoke()
        }
        binding.root.setOnClickListener {
            onLaterButtonClicked?.invoke()
        }
    }

    fun setOnLockAppButtonClicked(onLockAppButtonClicked: () -> Unit) {
        this.onLockAppButtonClicked = onLockAppButtonClicked
    }

    fun setOnLaterButtonClicked(onLaterButtonClicked: () -> Unit) {
        this.onLaterButtonClicked = onLaterButtonClicked
    }

    fun setAppPackageName(appPackageName: String) {
        val packageManager = context.packageManager
        val applicationInfo = packageManager.getApplicationInfo(appPackageName, PackageManager.GET_META_DATA)
        binding.title.text = packageManager.getApplicationLabel(applicationInfo)
        try {
            val icon = context.packageManager.getApplicationIcon(appPackageName)
            binding.iconApp.setImageDrawable(icon)
        } catch (e: PackageManager.NameNotFoundException) {
            binding.iconApp.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_round_lock_24px
                )
            )
        }
    }
}