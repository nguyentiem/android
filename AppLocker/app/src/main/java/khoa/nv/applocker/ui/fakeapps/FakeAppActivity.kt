package khoa.nv.applocker.ui.fakeapps

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
import khoa.nv.applocker.databinding.ActivityFakeAppBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FakeAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFakeAppBinding
    private val viewModel: FakeAppViewModel by viewModels()
    private val fakeAppAdapter = FakeAppAdapter { old, new ->
        viewModel.onSelectedItemChanged(old, new)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fake_app)

        setupToolbar()

        binding.recyclerView.apply {
            setHasFixedSize(true)
            adapter = fakeAppAdapter
        }

        lifecycleScope.launch {
            viewModel.fakeAppState.flowWithLifecycle(lifecycle).collect {
                fakeAppAdapter.setFakeApps(it)
            }
        }
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