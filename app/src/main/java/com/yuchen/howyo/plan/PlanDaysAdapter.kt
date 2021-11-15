package com.yuchen.howyo.plan

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.Day
import com.yuchen.howyo.data.DayItem
import com.yuchen.howyo.databinding.ItemPlanDayAddBinding
import com.yuchen.howyo.databinding.ItemPlanDayBinding
import com.yuchen.howyo.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlanDaysAdapter(val viewModel: PlanViewModel) :
    ListAdapter<DayItem, RecyclerView.ViewHolder>(DiffCallback) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private lateinit var context: Context

    class DayViewHolder(
        private var binding: ItemPlanDayBinding,
        private val viewModel: PlanViewModel
    ) : RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        val isSelected: LiveData<Boolean> = Transformations.map(viewModel.selectedDayPosition) {
            it == adapterPosition
        }


        fun bind(day: Day) {

            val isTrueSelected = (adapterPosition == viewModel.selectedDayPosition.value)
            binding.lifecycleOwner = this
            binding.viewModel = viewModel
            binding.viewHolder = this
            binding.day = day
            binding.isSelectedOnBind = isTrueSelected
            binding.executePendingBindings()

        }

        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun onAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun onDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }

    class AddViewHolder(
        private val binding: ItemPlanDayAddBinding,
        private val viewModel: PlanViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {

            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DayItem>() {
        override fun areItemsTheSame(oldItem: DayItem, newItem: DayItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DayItem, newItem: DayItem): Boolean {
            return oldItem.id == newItem.id
        }

        private const val ITEM_VIEW_DAY = 0x00
        private const val ITEM_VIEW_ADD_BTN = 0x01
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            ITEM_VIEW_DAY -> {
                DayViewHolder(
                    ItemPlanDayBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ),
                    viewModel
                )
            }
            ITEM_VIEW_ADD_BTN -> {
                AddViewHolder(
                    ItemPlanDayAddBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ),
                    viewModel
                )
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DayViewHolder -> {
                holder.bind((getItem(position) as DayItem.FullDayItem).day)
            }
            is AddViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DayItem.FullDayItem -> ITEM_VIEW_DAY
            is DayItem.AddBtn -> ITEM_VIEW_ADD_BTN
        }
    }

    fun submitDays(days: List<Day>) {

        adapterScope.launch {

            val dayItems: MutableList<DayItem> = mutableListOf()

            days.forEach {

                dayItems.add(DayItem.FullDayItem(it))
            }

            dayItems.add(DayItem.AddBtn)

            withContext(Dispatchers.Main) {
                submitList(dayItems)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is DayViewHolder -> holder.onAttach()
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is DayViewHolder -> holder.onDetach()
        }
    }
}