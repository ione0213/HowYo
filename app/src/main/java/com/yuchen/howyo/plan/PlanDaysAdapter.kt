package com.yuchen.howyo.plan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.Day
import com.yuchen.howyo.databinding.ItemPlanDayBinding

class PlanDaysAdapter(val viewModel: PlanViewModel) :
    ListAdapter<Day, PlanDaysAdapter.DayViewHolder>(DiffCallback) {

    class DayViewHolder(
        private var binding: ItemPlanDayBinding,
        private val viewModel: PlanViewModel
    ) : RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        val isSelected: LiveData<Boolean> = Transformations.map(viewModel.selectedDayPosition) {
            it == adapterPosition
        }

        fun bind(day: Day) {
            binding.viewModel = viewModel
            binding.viewHolder = this
            day.let {
                binding.day = it
                binding.executePendingBindings()
            }
        }
//
        private val lifecycleRegistry = LifecycleRegistry(this)
//
//        init {
//            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
//        }
//
//        fun onAttach() {
//            lifecycleRegistry.currentState = Lifecycle.State.STARTED
//        }
//
//        fun onDetach() {
//            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
//        }
//
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
            ItemPlanDayBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

//    override fun onViewAttachedToWindow(holder: DayViewHolder) {
//        super.onViewAttachedToWindow(holder)
//        holder.onAttach()
//    }
//
//    override fun onViewDetachedFromWindow(holder: DayViewHolder) {
//        super.onViewDetachedFromWindow(holder)
//        holder.onDetach()
//    }
}