package tools.certification.latency.auto.ui.quickcommand.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import tools.certification.latency.auto.R
import tools.certification.latency.auto.databinding.ActivityLatencyTestBinding
import tools.certification.latency.auto.databinding.ActivityQuickcommandUttTestBinding
import tools.certification.latency.auto.ui.main.viewmodel.LatencyTestViewModel
import tools.certification.latency.auto.ui.quickcommand.data.QuickcommandResult
import tools.certification.latency.auto.ui.quickcommand.viewmodel.QuickcommandViewModel

class QuickcommandUttTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuickcommandUttTestBinding
    private val viewModel: QuickcommandViewModel by viewModels()
    public lateinit var quickCommand:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quickCommand = intent.getStringExtra("quickcommand").toString()
        Log.d("TAG", "onCreate: "+quickCommand)
        Log.d("TAG", "onCreate: "+intent.getStringExtra("utt1").toString())
        Log.d("TAG", "onCreate: "+intent.getStringExtra("utt2").toString())
        Log.d("TAG", "onCreate: "+intent.getStringExtra("utt3").toString())
        setupUI()
    }

    private fun setupUI() {
        binding = DataBindingUtil.setContentView<ActivityQuickcommandUttTestBinding>(this, R.layout.activity_quickcommand_utt_test).also {
            it.toolbar.setNavigationOnClickListener {
                finish()
            }
        }
        viewModel.curTest.observe(this) {
            it?.let { it1 -> showTest(it1) }
        }

        binding.qcStart.setOnClickListener(View.OnClickListener {
//            viewModel.cleared()
            viewModel.runTest(quickCommand)
        })
    }

    private fun showTest(result: QuickcommandResult){
       if (result.wakeUp.isCompleted){
           binding.hiBixby.visibility = View.VISIBLE
           binding.hiBixby.text = result.wakeUp.endText+": "+result.wakeUp.command
       }else{
           binding.hiBixby.visibility = View.GONE
       }

        if (result.ting.isCompleted){
            binding.ting.visibility = View.VISIBLE
            binding.ting.text = result.ting.endText+": Time Bixby Wakeup sound"
        }else{
            binding.ting.visibility = View.GONE
        }

        if (result.firstRespone.isCompleted){
            binding.firstResponse.visibility = View.VISIBLE
            binding.firstResponse.text = result.firstRespone.endText+": "+result.firstRespone.command
        }else{
            binding.firstResponse.visibility = View.GONE
        }

        if (result.secondRespone.isCompleted){
            binding.secondResponse.visibility = View.VISIBLE
            binding.secondResponse.text = result.secondRespone.endText+": "+result.secondRespone.command
        }else{
            binding.secondResponse.visibility = View.GONE
        }
        if (result.thirdRespone.isCompleted){
            binding.thirdResponse.visibility = View.VISIBLE
            binding.thirdResponse.text = result.thirdRespone.endText+": "+result.thirdRespone.command
        }else{
            binding.thirdResponse.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}