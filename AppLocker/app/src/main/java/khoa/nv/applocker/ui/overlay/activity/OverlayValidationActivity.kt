package khoa.nv.applocker.ui.overlay.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.andrognito.patternlockview.PatternLockView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import dagger.hilt.android.AndroidEntryPoint
import khoa.nv.applocker.R
import khoa.nv.applocker.databinding.ActivityOverlayValidationBinding
import khoa.nv.applocker.ui.background.GradientBackgroundDataProvider
import khoa.nv.applocker.ui.newpattern.SimplePatternListener

@AndroidEntryPoint
class OverlayValidationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOverlayValidationBinding
    private val viewModel: OverlayValidationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_overlay_validation)
        updateLaunchingAppIcon(intent.getStringExtra(KEY_PACKAGE_NAME)!!)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        listenState()

        binding.patternLockView.addPatternLockListener(object : SimplePatternListener() {
            override fun onComplete(pattern: List<PatternLockView.Dot>) {
                viewModel.onPatternCompleted(pattern.map { it.id }.joinToString())
                binding.patternLockView.clearPattern()
            }

        })
    }

    private fun listenState() {
        viewModel.selectedBackground.observe(this) {
            val item = GradientBackgroundDataProvider.gradientViewStateList[it]
            binding.root.background = item.getGradientDrawable(this@OverlayValidationActivity)
        }
        viewModel.isDrawnCorrect.observe(this) { isDrawnCorrect ->
            when (isDrawnCorrect) {
                true -> {
                    setResult(RESULT_OK)
                    finish()
                }
                false -> {
                    YoYo.with(Techniques.Shake)
                        .duration(700)
                        .playOn(binding.textViewPrompt)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getFingerPrintMode()
        viewModel.getHiddenDrawingMode()
        viewModel.getSelectedBackgroundId()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent("android.intent.action.MAIN")
        intent.addCategory("android.intent.category.HOME")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun updateLaunchingAppIcon(appPackageName: String) {
        try {
            val icon = packageManager.getApplicationIcon(appPackageName)
            binding.avatarLock.setImageDrawable(icon)
        } catch (e: PackageManager.NameNotFoundException) {
            binding.avatarLock.setImageDrawable(
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
            val intent = Intent(context, OverlayValidationActivity::class.java)
            intent.putExtra(KEY_PACKAGE_NAME, packageName)
            return intent
        }
    }
}