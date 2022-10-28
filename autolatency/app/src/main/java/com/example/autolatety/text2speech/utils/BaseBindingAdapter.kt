package com.example.autolatety.text2speech.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.autolatety.BR

open class BaseBindingAdapter<T>(@LayoutRes val resId: Int) :
    RecyclerView.Adapter<BaseBindingAdapter.ViewHolder>() {
    var data: List<T>? = null
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
        val item = data?.get(position)
        holder.binding.setVariable(BR.item, item)
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    class ViewHolder(@NonNull var binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}