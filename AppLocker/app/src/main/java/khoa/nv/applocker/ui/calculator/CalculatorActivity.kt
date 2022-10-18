package khoa.nv.applocker.ui.calculator

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import khoa.nv.applocker.R
import khoa.nv.applocker.databinding.ActivityCalculatorBinding
import khoa.nv.applocker.ui.main.MainActivity
import khoa.nv.applocker.ui.newpattern.CreateNewPatternActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CalculatorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalculatorBinding
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calculator)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        observePassword()
    }

    private fun observePassword() {
        lifecycleScope.launch {
            viewModel.passwordState.flowWithLifecycle(lifecycle).collect { passwordState ->
                when (passwordState) {
                    CalculatorViewModel.PasswordState.SECOND_COMPLETED ->
                        startActivity(CreateNewPatternActivity.newCreatePatternIntent(this@CalculatorActivity))
                    CalculatorViewModel.PasswordState.MATCHED ->
                        startActivity(MainActivity.newIntent(this@CalculatorActivity))
                }
            }
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
            hide(WindowInsetsCompat.Type.statusBars())
//            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}