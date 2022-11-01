package tools.certification.latency.auto.ui.quickcommand.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import tools.certification.latency.auto.ui.quickcommand.view.QuickCommandRecyclerViewAdapter

class AddQuickCommandViewModel (application: Application) : AndroidViewModel(application){
    private var utts: List<String> = listOf()
    private lateinit var adapter : QuickCommandRecyclerViewAdapter

    fun setAdapter(adp:QuickCommandRecyclerViewAdapter){
        this.adapter =adp
    }

    fun setUtterances(list:List<String>){
        utts =list
        adapter.setData(utts)
    }

    fun getUtterances():List<String>{ return utts}
}