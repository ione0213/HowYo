package com.yuchen.howyo.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.PlanDataItem
import com.yuchen.howyo.databinding.ItemEmptyFavoriteBinding
import com.yuchen.howyo.databinding.ItemPlansFavoriteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteAdapter(
    private val onClickListener: OnClickListener,
    private val viewModel: FavoriteViewModel
) :
    ListAdapter<PlanDataItem, RecyclerView.ViewHolder>(DiffCallback) {
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class OnClickListener(val clickListener: (plan: Plan) -> Unit) {
        fun onClick(plan: Plan) = clickListener(plan)
    }

    class PlanViewHolder(private var binding: ItemPlansFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(plan: Plan, onClickListener: OnClickListener) {
            binding.root.setOnClickListener { onClickListener.onClick(plan) }
            binding.plan = plan
            binding.executePendingBindings()
        }
    }

    class EmptyViewHolder(binding: ItemEmptyFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object DiffCallback : DiffUtil.ItemCallback<PlanDataItem>() {
        override fun areItemsTheSame(oldItem: PlanDataItem, newItem: PlanDataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlanDataItem, newItem: PlanDataItem): Boolean {
            return oldItem == newItem
        }

        private const val ITEM_VIEW_EMPTY = 0x00
        private const val ITEM_VIEW_PLAN = 0x01
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_EMPTY -> {
                EmptyViewHolder(
                    ItemEmptyFavoriteBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            ITEM_VIEW_PLAN -> {
                PlanViewHolder(
                    ItemPlansFavoriteBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ).apply { viewModel = this@FavoriteAdapter.viewModel }
                )
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PlanViewHolder -> {
                holder.bind((getItem(position) as PlanDataItem.PlanItem).plan, onClickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PlanDataItem.EmptySchedule -> ITEM_VIEW_EMPTY
            is PlanDataItem.PlanItem -> ITEM_VIEW_PLAN
        }
    }

    fun addPlanOrEmptyPage(list: List<Plan>) {
        adapterScope.launch {
            val items = when (list.size) {
                0 -> listOf(PlanDataItem.EmptySchedule)
                else -> list.map { PlanDataItem.PlanItem(it) }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }
}
