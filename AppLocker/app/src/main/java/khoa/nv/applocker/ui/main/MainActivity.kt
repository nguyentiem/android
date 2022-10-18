package khoa.nv.applocker.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import dagger.hilt.android.AndroidEntryPoint
import khoa.nv.applocker.R
import khoa.nv.applocker.databinding.ActivityMainBinding
import khoa.nv.applocker.service.AppLockerService
import khoa.nv.applocker.ui.main.lockedapps.AppLockAdapter
import khoa.nv.applocker.ui.settings.SettingsActivity
import khoa.nv.applocker.util.helper.LOCK_APP_RECEIVER
import khoa.nv.applocker.util.helper.PermissionChecker
import khoa.nv.applocker.util.helper.UNLOCK_APP_RECEIVER
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchView: SearchView

    private var permissionDialog: PermissionDialog? = null
    private val viewModel: MainViewModel by viewModels()
    private val appAdapter = AppLockAdapter { item ->
        val action = if (item.isLocked) UNLOCK_APP_RECEIVER else LOCK_APP_RECEIVER
        Intent(action).apply {
            putExtra(Intent.EXTRA_TEXT, item.getPackageName())
        }.also {
            LocalBroadcastManager.getInstance(this).sendBroadcast(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupSearchView()
        listenState()

        binding.recyclerView.apply {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(this@MainActivity, RecyclerView.VERTICAL))
            adapter = appAdapter
        }

        binding.toolbar.setNavigationOnClickListener {
            startActivity(SettingsActivity.newIntent(this))
        }

        AppLockerService.startForeground(this)
    }

    private fun listenState() {
        lifecycleScope.launch {
            viewModel.appLock.flowWithLifecycle(lifecycle).collect {
                if (it.isNotEmpty()) {
                    appAdapter.setApps(it)
                    checkScrollToNewApp()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (PermissionChecker.isAllPermissionChecked(applicationContext)) {
            getAppLock()
        } else if (permissionDialog == null) {
//            appAdapter.submitList(emptyList())

            displayPermissionDialog()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun displayPermissionDialog() {
        permissionDialog = PermissionDialog().apply {
            isCancelable = false
            show(supportFragmentManager, PermissionDialog.TAG)
        }
    }

    private fun getAppLock() {
        viewModel.getAppsLock()
    }

    private fun checkScrollToNewApp() {
        if (intent?.action != ACTION_NEW_INSTALLED_APP_SCROLL)
            return

        intent.getStringExtra(KEY_PACKAGE_NAME)?.let {
            val index = appAdapter.findIndexOf(it)

            if (index != -1) {
                scrollTo(index)
            }
        }
    }

    private fun scrollTo(index: Int) {
        with(binding.recyclerView) {
            smoothScrollToPosition(appAdapter.itemCount.coerceAtMost(index + 2))
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == SCROLL_STATE_IDLE) {
                        (findViewHolderForAdapterPosition(index) as? AppLockAdapter.ViewHolder)?.binding?.root?.let { item ->
                            item.postOnAnimation {
                                item.isPressed = true
                            }
                            item.postOnAnimationDelayed({
                                item.isPressed = false
                            }, 250)
                        }
                        removeOnScrollListener(this)
                        intent.action = null
                    }
                }
            })
        }
    }

    private fun setupSearchView() {
        searchView = (binding.toolbar.menu.findItem(R.id.search).actionView as SearchView).apply {
            setOnQueryTextFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    hideKeyboard(v)
                    isIconified = true
                }
                binding.appbar.setExpanded(!hasFocus, true)
            }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    appAdapter.filter.filter(query)
                    binding.recyclerView.smoothScrollToPosition(0)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    appAdapter.filter.filter(newText)
                    binding.recyclerView.smoothScrollToPosition(0)
                    return false
                }

            })
        }
    }

    private fun hideKeyboard(v: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.isIconified = true
            binding.recyclerView.smoothScrollToPosition(0)
            return
        }
        super.onBackPressed()
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

    companion object {
        private const val ACTION_NEW_INSTALLED_APP_SCROLL = "ACTION_NEW_INSTALLED_APP_SCROLL"
        private const val KEY_PACKAGE_NAME = "KEY_PACKAGE_NAME"

        fun newIntent(context: Context) =
            Intent(context, MainActivity::class.java)

        fun newIntent(context: Context, packageName: String) =
            Intent(context, MainActivity::class.java)
                .putExtra(KEY_PACKAGE_NAME, packageName)

        fun newInstalledAppIntent(context: Context, packageName: String) =
            newIntent(context, packageName)
                .setAction(ACTION_NEW_INSTALLED_APP_SCROLL)
    }
}