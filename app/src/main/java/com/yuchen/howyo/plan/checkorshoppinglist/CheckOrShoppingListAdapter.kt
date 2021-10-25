package com.yuchen.howyo.plan.checkorshoppinglist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.CheckListItem
import com.yuchen.howyo.data.CheckShoppingItem
import com.yuchen.howyo.data.CheckShoppingItemResult
import com.yuchen.howyo.databinding.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CheckOrShoppingListAdapter(
    private val viewModelOrShopping: CheckOrShoppingListViewModel
) : ListAdapter<CheckListItem, RecyclerView.ViewHolder>(DiffCallback) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class CheckItemViewHolder(
        private var binding: ItemCheckOrShoppingListItemBinding,
        private val viewModelOrShopping: CheckOrShoppingListViewModel
        ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(checkShoppingItem: CheckShoppingItem) {

            binding.viewModel = viewModelOrShopping
            checkShoppingItem.let {
                binding.checkShoppingItem = it
                binding.executePendingBindings()
            }
        }
    }

    class TitleViewHolder(private var binding: ItemCheckOrShoppingListTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.title = title
            binding.executePendingBindings()
        }
    }

    class AddViewHolder(
        private var binding: ItemCheckOrShoppingListAddBinding,
        private val viewModelOrShopping: CheckOrShoppingListViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.viewModel = viewModelOrShopping
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CheckListItem>() {
        override fun areItemsTheSame(oldItem: CheckListItem, newItem: CheckListItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: CheckListItem, newItem: CheckListItem): Boolean {
            return oldItem.id == newItem.id
        }

        private const val ITEM_VIEW_TITLE = 0x00
        private const val ITEM_VIEW_CHECK_ITEM = 0x01
        private const val ITEM_VIEW_ADD_BTN = 0x02
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TITLE -> TitleViewHolder(
                ItemCheckOrShoppingListTitleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            ITEM_VIEW_CHECK_ITEM -> CheckItemViewHolder(
                ItemCheckOrShoppingListItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                viewModelOrShopping
            )
            ITEM_VIEW_ADD_BTN -> AddViewHolder(
                ItemCheckOrShoppingListAddBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                viewModelOrShopping
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is TitleViewHolder -> {
                holder.bind((getItem(position) as CheckListItem.Title).title)
            }
            is CheckItemViewHolder -> {
                holder.bind((getItem(position) as CheckListItem.CheckItem).item)
            }
            is AddViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CheckListItem.Title -> ITEM_VIEW_TITLE
            is CheckListItem.CheckItem -> ITEM_VIEW_CHECK_ITEM
            is CheckListItem.AddBtn -> ITEM_VIEW_ADD_BTN
        }
    }

    fun addTitleAndItem(list: List<CheckShoppingItemResult>) {

        adapterScope.launch {

            val checkListItems: MutableList<CheckListItem> = mutableListOf()

            list.forEach {
                //Only check list has subType
                when {
                    it.type != "" ->  checkListItems.add(CheckListItem.Title(it.type))
                }

                it.itemList.forEach { checkItem ->
                    checkListItems.add(CheckListItem.CheckItem(checkItem))
                }

                checkListItems.add(CheckListItem.AddBtn)
            }

            withContext(Dispatchers.Main) {
                submitList(checkListItems)
            }
        }
    }
}