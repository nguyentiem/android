package khoa.nv.applocker.ui.background

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import khoa.nv.applocker.databinding.ItemBackgroundGradientBinding

class BackgroundAdapter(
    private val onItemSelected: ((GradientItemViewState) -> Unit)? = null
) : RecyclerView.Adapter<BackgroundAdapter.ViewHolder>() {
    private val backgrounds = mutableListOf<GradientItemViewState>()
    private var selectedBackgroundId = 0

    @SuppressLint("NotifyDataSetChanged")
    fun setBackgrounds(newBackgrounds: List<GradientItemViewState>) {
        backgrounds.clear()
        backgrounds.addAll(newBackgrounds)
        selectedBackgroundId = backgrounds.indexOfFirst { it.isChecked }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        ItemBackgroundGradientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.viewState = backgrounds[position]
    }

    override fun getItemCount() = backgrounds.size

    inner class ViewHolder(val binding: ItemBackgroundGradientBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val itemClicked = backgrounds[adapterPosition]
                onItemSelected?.invoke(itemClicked)
                backgrounds[selectedBackgroundId].isChecked = itemClicked.id == selectedBackgroundId
                notifyItemChanged(selectedBackgroundId)
                selectedBackgroundId = itemClicked.id
                backgrounds[selectedBackgroundId].isChecked = true
                notifyItemChanged(adapterPosition)
            }
        }
    }
}