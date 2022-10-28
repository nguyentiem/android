package com.example.autolatety.text2speech.view

import com.example.autolatety.R
import com.example.autolatety.text2speech.data.Utterance
import com.example.autolatety.text2speech.utils.BaseBindingAdapter


class UtteranceListAdapter(private val utteranceItemClick: (utterance: String) -> Unit) :
    BaseBindingAdapter<Utterance>(R.layout.utterance_item) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.binding.root.setOnClickListener {
            data?.get(position)?.let { item -> utteranceItemClick.invoke(item.content) }
        }
    }
}