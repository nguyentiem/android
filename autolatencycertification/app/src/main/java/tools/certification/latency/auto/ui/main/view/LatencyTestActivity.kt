package tools.certification.latency.auto.ui.main.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.postDelayed
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import kotlinx.coroutines.launch
import timber.log.Timber
import tools.certification.latency.auto.R
import tools.certification.latency.auto.databinding.ActivityLatencyTestBinding
import tools.certification.latency.auto.ui.main.viewmodel.LatencyTestViewModel
import tools.certification.latency.auto.utils.StorageUtils

class LatencyTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLatencyTestBinding
    private val viewModel: LatencyTestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding = DataBindingUtil.setContentView<ActivityLatencyTestBinding>(
            this,
            R.layout.activity_latency_test
        ).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
            it.recyclerView.addItemDecoration(DividerItemDecoration(this, VERTICAL))
            it.adapter = LatencyTestAdapter(intent.getIntExtra("repeat", 1))
            setSupportActionBar(it.toolbar)

            it.toolbar.setNavigationOnClickListener {
                finish()
            }
            it.toggle.setOnClickListener { _ ->
                if (viewModel.isRunning.value == false) {
                    it.adapter!!.clear()
                }
                viewModel.stopOrRunTest()
            }
        }
        viewModel.isRunning.observe(this) {
            invalidateOptionsMenu()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.curTest.collect { curTest ->
                    curTest?.let {
                        if (binding.adapter!!.itemCount == viewModel.testNumber.value) {
                            binding.adapter!!.updateLastItem(it)
                        } else {
                            binding.adapter!!.addItem(it)
                        }
                        if (binding.adapter!!.itemCount > 0) {
                            binding.recyclerView.postDelayed(10L) {
                                binding.recyclerView.smoothScrollToPosition(binding.adapter!!.itemCount - 1)
                            }
                        }
                    }
                    Timber.d("Current test: $curTest")
                }
            }
        }
        viewModel.results.observe(this) {
            binding.totalDone.text = "Total Completed Tests: ${it.size}"
            binding.totalFails.text = "Total Failed Tests: ${it.count { it.accuracy != 1 }}"
            val notError = it.filter { it.accuracy != -1 }
            binding.accuracyAverage.text =
                "Accuracy: ${(notError.sumOf { it.accuracy } * 100 / notError.size.toFloat()).toInt()}%"
            binding.wakeupLatencyAverage.text =
                "Average Wakeup Latency: ${notError.sumOf { it.ting.start - it.wakeUp.end } / notError.size.toFloat()}ms"
            binding.responseLatencyAverage.text =
                "Average Command Latency: ${notError.sumOf { it.response.start - it.request.end } / notError.size.toFloat()}ms"
        }
        viewModel.load().invokeOnCompletion {
            Timber.d("Results: ${viewModel.results.value}")
            viewModel.results.value?.forEach {
                binding.adapter!!.addItem(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (viewModel.isRunning.value != true && StorageUtils.isExternalStorageAvailable() && !StorageUtils.isExternalStorageReadOnly()) {
            menuInflater.inflate(R.menu.latency_test_menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.export -> {
                viewModel.exportTestResults(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.bindService(this)
    }

    override fun onStop() {
        super.onStop()
        viewModel.unbindService(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onStop()
    }
}
