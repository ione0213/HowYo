package com.yuchen.howyo.plan.checkorshoppinglist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.data.CheckListItem
import com.yuchen.howyo.data.CheckShoppingList
import com.yuchen.howyo.databinding.*
import com.yuchen.howyo.util.Logger
import com.yuchen.howyo.util.Util.getString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CheckOrShoppingListAdapter(
    private val checkOrShoppingListViewModel: CheckOrShoppingListViewModel
) : ListAdapter<CheckListItem, RecyclerView.ViewHolder>(DiffCallback) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class CheckItemViewHolder(
        private var binding: ItemCheckOrShoppingListItemBinding,
        private val viewModelOrShopping: CheckOrShoppingListViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(checkShoppingList: CheckShoppingList) {

            binding.checkboxCheckList.setOnCheckedChangeListener { buttonView, isChecked ->
                Logger.i("buttonView:$buttonView")
                Logger.i("isChecked::$isChecked")
                viewModelOrShopping.setCheck(checkShoppingList, isChecked)
            }
            binding.viewModel = viewModelOrShopping
            checkShoppingList.let {
                binding.checkShoppingList = it
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
        fun bind(subType: String) {
            binding.viewModel = viewModelOrShopping
            binding.subType = subType
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CheckListItem>() {
        override fun areItemsTheSame(oldItem: CheckListItem, newItem: CheckListItem): Boolean {
            return oldItem == newItem
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
                checkOrShoppingListViewModel
            )
            ITEM_VIEW_ADD_BTN -> AddViewHolder(
                ItemCheckOrShoppingListAddBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                checkOrShoppingListViewModel
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
                holder.bind((getItem(position) as CheckListItem.AddBtn).subType)
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

    fun addTitleAndItem(list: List<CheckShoppingList>?, mainItemType: MainItemType) {

        adapterScope.launch {

            val checkListItems: MutableList<CheckListItem> = mutableListOf()

            when (mainItemType) {
                MainItemType.CHECK -> {
                    HowYoApplication.instance.resources.getStringArray(R.array.check_list)
                        .forEach { subType ->
                            if (list != null) {
                                when {
                                    list.any { it.subType == subType } -> {

                                        checkListItems.add(
                                            CheckListItem.Title(
                                                when (subType) {
                                                    getString(R.string.necessary) -> getString(R.string.necessary_title)
                                                    getString(R.string.clothe) -> getString(R.string.clothe_title)
                                                    getString(R.string.wash) -> getString(R.string.wash_title)
                                                    getString(R.string.electronic) -> getString(R.string.electronic_title)
                                                    getString(R.string.health) -> getString(R.string.health_title)
                                                    getString(R.string.other) -> getString(R.string.other_title)
                                                    else -> ""
                                                }
                                            )
                                        )

                                        list.filter { it.subType == subType }.forEach { checkItem ->
                                            checkListItems.add(CheckListItem.CheckItem(checkItem))
                                        }

                                        checkListItems.add(CheckListItem.AddBtn(subType))
                                    }
                                }
                            }
                        }
                }
                MainItemType.SHOPPING -> {
                    list?.forEach { checkItem ->
                        checkListItems.add(CheckListItem.CheckItem(checkItem))
                    }

                    checkListItems.add(CheckListItem.AddBtn(""))
                }
            }

            withContext(Dispatchers.Main) {
                submitList(checkListItems)
            }
        }
    }
}