package com.yuchen.howyo.plan.findlocation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.Day
import com.yuchen.howyo.databinding.ItemFindLocationDayBinding

class FindLocationDaysAdapter(val viewModel: FindLocationViewModel) :
    ListAdapter<Day, FindLocationDaysAdapter.DayViewHolder>(DiffCallback) {
    class DayViewHolder(
        private var binding: ItemFindLocationDayBinding,
        private val viewModel: FindLocationViewModel
    ) : RecyclerView.ViewHolder(binding.root), LifecycleOwner {
        val isSelected: LiveData<Boolean> = Transformations.map(viewModel.selectedDayPosition) {
            it == adapterPosition
        }

        private val lifecycleRegistry = LifecycleRegistry(this)

        fun bind(day: Day) {
            binding.viewModel = viewModel
            binding.viewHolder = this
            day.let {
                binding.day = it
                binding.executePendingBindings()
            }
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Day>() {
        override fun areItemsTheSame(oldItem: Day, newItem: Day): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Day, newItem: Day): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        return DayViewHolder(
            ItemFindLocationDayBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
