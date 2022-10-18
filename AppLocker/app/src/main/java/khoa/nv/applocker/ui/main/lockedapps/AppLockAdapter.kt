package khoa.nv.applocker.ui.main.lockedapps

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import khoa.nv.applocker.databinding.ItemAppLockListBinding
import java.util.concurrent.Executors

class AppLockAdapter(
    private val lockUnlockApp: (AppLockItemViewState) -> Unit
) : ListAdapter<AppLockItemViewState, AppLockAdapter.ViewHolder>(differConfig), Filterable {
    private val apps: MutableList<AppLockItemViewState> = mutableListOf()

    fun setApps(newApps: List<AppLockItemViewState>) {
        apps.clear()
        apps.addAll(newApps)
        submitList(newApps)
    }

    fun findIndexOf(packageName: String): Int {
        return apps.indexOfFirst { it.getPackageName() == packageName }
    }

    inner class ViewHolder(val binding: ItemAppLockListBinding, context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val item = getItem(adapterPosition)
                lockUnlockApp(item)
                item.isLocked = !item.isLocked
                notifyItemChanged(adapterPosition)
                submitList(currentList.sortedWith(
                    compareBy(
                        AppLockItemViewState::isNotLocked,
                        AppLockItemViewState::getAppName
                    )
                ))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemAppLockListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ), parent.context
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.viewState = getItem(position)
    }

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return FilterResults().apply {
                values = if (constraint.isNullOrEmpty()) {
                    apps
                } else {
                    val query = constraint.toString()
                    apps.filter { it.getAppName().contains(query, true) }
                }
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            submitList(results.values as? List<AppLockItemViewState>)
        }

    }

    companion object {
        private val appLockDiffUtil = object : DiffUtil.ItemCallback<AppLockItemViewState>() {
            override fun areItemsTheSame(
                oldItem: AppLockItemViewState,
                newItem: AppLockItemViewState
            ): Boolean {
                return oldItem.getPackageName() == newItem.getPackageName()
            }

            override fun areContentsTheSame(
                oldItem: AppLockItemViewState,
                newItem: AppLockItemViewState
            ): Boolean {
                return oldItem.getPackageName() == newItem.getPackageName() &&
                        oldItem.isLocked == newItem.isLocked &&
                        oldItem.getAppName() == newItem.getAppName()
            }
        }
        private val differConfig = AsyncDifferConfig.Builder(appLockDiffUtil)
            .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor()).build()
    }
}