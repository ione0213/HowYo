package com.yuchen.howyo.plan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.databinding.ItemPlanScheduleBinding
import androidx.recyclerview.widget.ListAdapter

class ScheduleAdapter(
    val viewModel: PlanViewModel,
    private val onClickListener: OnClickListener
) :
    ListAdapter<Schedule, ScheduleAdapter.ScheduleViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (schedule: Schedule) -> Unit) {
        fun onClick(schedule: Schedule) = clickListener(schedule)
    }

    class ScheduleViewHolder(private var binding: ItemPlanScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: Schedule, onClickListener: OnClickListener) {
            binding.root.setOnClickListener { onClickListener.onClick(schedule) }
            schedule.let {
                binding.schedule = it
                binding.executePendingBindings()
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Schedule>() {
        override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        return ScheduleViewHolder(
            ItemPlanScheduleBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ).apply {
                viewModel = this@ScheduleAdapter.viewModel
            }
        )
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(getItem(position), onClickListener)
    }
}