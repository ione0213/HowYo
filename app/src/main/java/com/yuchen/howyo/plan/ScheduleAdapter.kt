package com.yuchen.howyo.plan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.databinding.ItemPlanScheduleBinding
import androidx.recyclerview.widget.ListAdapter
import com.yuchen.howyo.data.DayItem
import com.yuchen.howyo.data.ScheduleDataItem
import com.yuchen.howyo.databinding.ItemEmptyScheduleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleAdapter(
    val viewModel: PlanViewModel,
    private val onClickListener: OnClickListener
) :
    ListAdapter<ScheduleDataItem, RecyclerView.ViewHolder>(DiffCallback) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

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

    class EmptyViewHolder(private var binding: ItemEmptyScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ScheduleDataItem>() {
        override fun areItemsTheSame(
            oldItem: ScheduleDataItem,
            newItem: ScheduleDataItem
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: ScheduleDataItem,
            newItem: ScheduleDataItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        private const val ITEM_VIEW_EMPTY = 0x00
        private const val ITEM_VIEW_SCHEDULE = 0x01
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_EMPTY -> {
                EmptyViewHolder(
                    ItemEmptyScheduleBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ).apply {
                        viewModel = this@ScheduleAdapter.viewModel
                    }
                )
            }
            ITEM_VIEW_SCHEDULE -> {
                ScheduleViewHolder(
                    ItemPlanScheduleBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ).apply {
                        viewModel = this@ScheduleAdapter.viewModel
                    }
                )
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ScheduleViewHolder -> {
                holder.bind(
                    (getItem(position) as ScheduleDataItem.ScheduleItem).schedule,
                    onClickListener
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ScheduleDataItem.EmptySchedule -> ITEM_VIEW_EMPTY
            is ScheduleDataItem.ScheduleItem -> ITEM_VIEW_SCHEDULE
        }
    }

    fun addEmptyAndSchedule(list: List<Schedule>) {
        adapterScope.launch {
            val items = when (list.size) {
                0 -> {
                    listOf(ScheduleDataItem.EmptySchedule)
                }
                else -> {
                    list.map { ScheduleDataItem.ScheduleItem(it) }
                }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }
}