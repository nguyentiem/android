package tools.certification.latency.auto.ui.quickcommand.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import tools.certification.latency.auto.R
import tools.certification.latency.auto.databinding.ActivityAddQuickCommandCertificateBinding
import tools.certification.latency.auto.ui.quickcommand.viewmodel.AddQuickCommandViewModel


class AddQuickCommandCertificateActivity : AppCompatActivity() {
    lateinit var  binding:ActivityAddQuickCommandCertificateBinding
    private val viewModel: AddQuickCommandViewModel by viewModels()
    private lateinit var mAdapter: QuickCommandRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_quick_command_certificate)

        mAdapter = QuickCommandRecyclerViewAdapter()
        binding.qcContaner.adapter = mAdapter
        binding.qcContaner.layoutManager = LinearLayoutManager(this)
        val callback: ItemTouchHelper.Callback = ItemMoveCallback(mAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.qcContaner)
        viewModel.setAdapter(binding.qcContaner.adapter as QuickCommandRecyclerViewAdapter)
        viewModel.setUtterances(listOf("what is weather now ?","what time is it?","what day is today?"))
        binding.qcTestAction.setOnClickListener(View.OnClickListener {
            if(binding.qcContent.text!=null && binding.qcContent.text.toString()!=""){
//                add qc here
                val intent =Intent(this,QuickcommandUttTestActivity::class.java)
                intent.putExtra("quickcommand",binding.qcContent.text.toString())
                intent.putExtra("utt1",viewModel.getUtterances().get(0))
                intent.putExtra("utt2",viewModel.getUtterances().get(1))
                intent.putExtra("utt3",viewModel.getUtterances().get(2))
                startActivity(intent)
            }else{
                Toast.makeText(this,"Please,type quick command!",Toast.LENGTH_SHORT).show()
            }
        })
    }
}


