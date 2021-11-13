package com.yuchen.howyo.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.databinding.ItemPlansFavoriteBinding

class FavoriteAdapter(
    private val onClickListener: OnClickListener,
    private val viewModel: FavoriteViewModel
) :
    ListAdapter<Plan, FavoriteAdapter.PlanViewHolder>(DiffCallback) {

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

    companion object DiffCallback : DiffUtil.ItemCallback<Plan>() {
        override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        return PlanViewHolder(
            ItemPlansFavoriteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ).apply { viewModel = this@FavoriteAdapter.viewModel }
        )
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bind(getItem(position), onClickListener)
    }
}