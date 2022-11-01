package tools.certification.latency.auto.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import tools.certification.latency.auto.BR

open class BaseBindingAdapter<T>(@LayoutRes val resId: Int) :
    RecyclerView.Adapter<BaseBindingAdapter.ViewHolder>() {
    var data: List<T> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                resId,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.setVariable(BR.item, data[position])
        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = data.size

    class ViewHolder(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}
