package tools.certification.latency.auto.ui.quickcommand.view


import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tools.certification.latency.auto.R
import tools.certification.latency.auto.ui.quickcommand.viewmodel.AddQuickCommandViewModel
import java.util.*


class QuickCommandRecyclerViewAdapter : RecyclerView.Adapter<QuickCommandRecyclerViewAdapter.UttItemViewHolder>(), ItemMoveCallback.ItemTouchHelperContract {
    var list = listOf<String>()


    fun setData(utts: List<String>) {
        list = utts
        notifyDataSetChanged()
//        for (t in list) {
//            Log.d("TAG", "setData: "+t)
//        }
    }

    inner class UttItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var context: TextView = itemView.findViewById(R.id.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UttItemViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.utt_item, parent, false)
        return UttItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UttItemViewHolder, position: Int) {
        holder.context.text = list.get(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(list, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(list, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(myViewHolder: UttItemViewHolder?) {
        myViewHolder?.context?.setBackgroundColor(Color.GRAY);
    }

    override fun onRowClear(myViewHolder: UttItemViewHolder?) {
        myViewHolder?.context?.setBackgroundColor(Color.WHITE);
    }

}