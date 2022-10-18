package khoa.nv.applocker.ui.fakeapps

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import khoa.nv.applocker.databinding.ItemFakeAppBinding

class FakeAppAdapter(
    private val onItemSelected: ((FakeAppItemViewState, FakeAppItemViewState) -> Unit)? = null
) : RecyclerView.Adapter<FakeAppAdapter.ViewHolder>() {
    private val fakeApps = mutableListOf<FakeAppItemViewState>()
    private var selectedFakeAppId = 0

    @SuppressLint("NotifyDataSetChanged")
    fun setFakeApps(newFakeApps: List<FakeAppItemViewState>) {
        fakeApps.clear()
        fakeApps.addAll(newFakeApps)
        selectedFakeAppId = fakeApps.indexOfFirst { it.isChecked }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        ItemFakeAppBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.viewState = fakeApps[position]
    }

    override fun getItemCount() = fakeApps.size

    inner class ViewHolder(val binding: ItemFakeAppBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val itemClicked = fakeApps[adapterPosition]
                onItemSelected?.invoke(fakeApps[selectedFakeAppId], itemClicked)
                fakeApps[selectedFakeAppId].isChecked = itemClicked.id == selectedFakeAppId
                notifyItemChanged(selectedFakeAppId)
                selectedFakeAppId = itemClicked.id
                fakeApps[selectedFakeAppId].isChecked = true
                notifyItemChanged(adapterPosition)
            }
        }
    }
}
