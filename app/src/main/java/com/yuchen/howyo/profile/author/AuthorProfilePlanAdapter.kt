package com.yuchen.howyo.profile.author

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.databinding.ItemPlansProfileBinding

class AuthorProfilePlanAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Plan, AuthorProfilePlanAdapter.PlanViewHolder>(DiffCallback) {
    class OnClickListener(val clickListener: (plan: Plan) -> Unit) {
        fun onClick(plan: Plan) = clickListener(plan)
    }

    class PlanViewHolder(private var binding: ItemPlansProfileBinding) :
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
            ItemPlansProfileBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bind(getItem(position), onClickListener)
    }
}
