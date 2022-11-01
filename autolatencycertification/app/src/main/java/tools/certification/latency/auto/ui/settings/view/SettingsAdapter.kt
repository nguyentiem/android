package tools.certification.latency.auto.ui.settings.view

import tools.certification.latency.auto.R
import tools.certification.latency.auto.ui.common.BaseBindingAdapter

class SettingsAdapter(private val onItemClicked: (utterance: String) -> Unit = {}) :
    BaseBindingAdapter<String>(R.layout.settings_utterance_item) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.binding.root.setOnClickListener {
            onItemClicked(data[position])
        }
    }
}
