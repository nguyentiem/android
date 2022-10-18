package khoa.nv.applocker.ui.overlay.view

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.andrognito.patternlockview.PatternLockView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint
import khoa.nv.applocker.R
import khoa.nv.applocker.data.AppLockerPreferences
import khoa.nv.applocker.databinding.ViewPatternOverlayBinding
import khoa.nv.applocker.ui.background.GradientBackgroundDataProvider
import khoa.nv.applocker.ui.newpattern.SimplePatternListener
import khoa.nv.applocker.ui.overlay.OverlayValidateType
import khoa.nv.applocker.ui.overlay.OverlayViewState

class OverlayPatternView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val appLockerPreferences = AppLockerPreferences(context.applicationContext)
    private var onPatternCompleted: ((String) -> Unit)? = null
    private val fingerprintIdentify: FingerprintIdentify = FingerprintIdentify(context.applicationContext)
    private val fingerprintListener = object : BaseFingerprint.IdentifyListener {
        override fun onSucceed() {

        }

        override fun onNotMatch(availableTimes: Int) {
        }

        override fun onFailed(isDeviceLocked: Boolean) {
        }

        override fun onStartFailedByDeviceLocked() {
        }

    }

    private val binding: ViewPatternOverlayBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.view_pattern_overlay,
            this,
            true
        )

    init {
        binding.patternLockView.addPatternLockListener(object : SimplePatternListener() {
            override fun onComplete(pattern: List<PatternLockView.Dot>) {
                onPatternCompleted?.invoke(pattern.map { it.id }.joinToString())
            }
        })
        try {
            fingerprintIdentify.setSupportAndroidL(true)
            fingerprintIdentify.init()
        } catch (exception: Exception) {

        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        hideSystemUI()
        fingerprintIdentify.startIdentify(3, fingerprintListener)
        updateSelectedBackground()
        binding.patternLockView.clearPattern()
        binding.viewState = OverlayViewState()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        fingerprintIdentify.cancelIdentify()
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowInsetsController?.apply {
                hide(WindowInsets.Type.displayCutout())
                hide(WindowInsets.Type.systemBars())
            }
        } else {
            binding.root.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    fun notifyDrawnCorrect() {
        binding.patternLockView.clearPattern()
        binding.viewState =
            OverlayViewState(OverlayValidateType.TYPE_PATTERN, true)
    }

    fun notifyDrawnWrong() {
        binding.patternLockView.clearPattern()
        binding.viewState =
            OverlayViewState(OverlayValidateType.TYPE_PATTERN, false)
        YoYo.with(Techniques.Shake)
            .duration(700)
            .playOn(binding.textViewPrompt)
    }

    fun observePattern(onPatternCompleted: (String) -> Unit) {
        this.onPatternCompleted = onPatternCompleted
    }

    fun setHiddenDrawingMode(isHiddenDrawingMode: Boolean) {
        binding.patternLockView.isInStealthMode = isHiddenDrawingMode
    }

    fun setAppPackageName(appPackageName: String) {
        try {
            val icon = context.packageManager.getApplicationIcon(appPackageName)
            binding.avatarLock.setImageDrawable(icon)
        } catch (e: PackageManager.NameNotFoundException) {
            binding.avatarLock.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_round_lock_24px
                )
            )
        }
    }

    private fun updateSelectedBackground() {
        val selectedBackgroundId = appLockerPreferences.getSelectedBackgroundId()
        val item = GradientBackgroundDataProvider.gradientViewStateList[selectedBackgroundId]
        binding.mainBackground.background = item.getGradientDrawable(context)
    }
}