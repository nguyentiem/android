package khoa.nv.applocker.ui.newpattern

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.andrognito.patternlockview.PatternLockView
import dagger.hilt.android.AndroidEntryPoint
import khoa.nv.applocker.R
import khoa.nv.applocker.databinding.ActivityCreateNewPatternBinding
import khoa.nv.applocker.ui.main.MainActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateNewPatternActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNewPatternBinding
    private val viewModel: CreateNewPatternViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUi()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_new_pattern)

        binding.patternLockView.addPatternLockListener(object : SimplePatternListener() {
            override fun onComplete(pattern: List<PatternLockView.Dot>) {
                val patternString = pattern.map { it.id }.joinToString()
                if (viewModel.isFirstPattern()) {
                    viewModel.setFirstPattern(patternString)
                } else {
                    viewModel.setSecondPattern(patternString)
                }
                binding.patternLockView.clearPattern()
            }
        })
        lifecycleScope.launch {
            viewModel.patternEventState.flowWithLifecycle(lifecycle).collect { viewState ->
                binding.viewState = viewState
                binding.executePendingBindings()

                if (viewState.isCreatedNewPattern()) {
                    onPatternCreateCompleted()
                }
            }
        }
    }

    private fun onPatternCreateCompleted() {
        if (intent?.action == ACTION_CREATE_NEW_PATTERN) {
            startActivity(MainActivity.newIntent(this))
        }
        finish()
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
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        }
    }

    companion object {
        private const val ACTION_CREATE_NEW_PATTERN = "ACTION_CREATE_NEW_PATTERN"

        fun newCreatePatternIntent(context: Context): Intent {
            return Intent(context, CreateNewPatternActivity::class.java)
                .setAction(ACTION_CREATE_NEW_PATTERN)
        }

        fun newReCreatePatternIntent(context: Context): Intent {
            return Intent(context, CreateNewPatternActivity::class.java)
        }
    }
}