package khoa.nv.applocker.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import khoa.nv.applocker.R
import khoa.nv.applocker.databinding.ActivitySettingsBinding
import khoa.nv.applocker.ui.background.BackgroundActivity
import khoa.nv.applocker.ui.fakeapps.FakeAppActivity
import khoa.nv.applocker.ui.newpattern.CreateNewPatternActivity

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val settingViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUi()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.lifecycleOwner = this
        binding.viewModel = settingViewModel
        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    fun startBackgroundActivity(view: android.view.View) {
        startActivity(Intent(this, BackgroundActivity::class.java))
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
        }
    }

    fun startCreatePatternActivity(view: android.view.View) {
        startActivity(CreateNewPatternActivity.newReCreatePatternIntent(this))
    }

    fun startFakeAppActivity(view: android.view.View) {
        startActivity(Intent(this, FakeAppActivity::class.java))
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}