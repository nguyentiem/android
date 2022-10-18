package khoa.nv.applocker.ui.background

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import khoa.nv.applocker.R
import khoa.nv.applocker.databinding.ActivityBackgroundBinding

@AndroidEntryPoint
class BackgroundActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBackgroundBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUi()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_background)

        if (savedInstanceState == null) {
            supportFragmentManager.commit(true) {
                add(R.id.containerBackgrounds, BackgroundFragment.newInstance())
            }
        }

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
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