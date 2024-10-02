package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.plan.checkorshoppinglist.CheckOrShoppingListViewModel
import com.yuchen.howyo.plan.checkorshoppinglist.MainItemType

class CheckListViewModelFactory(
    private val howYoRepository: HowYoRepository,
    private val planId: String,
    private val mainType: MainItemType
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        with(modelClass) {
            when {
                isAssignableFrom(CheckOrShoppingListViewModel::class.java) ->
                    CheckOrShoppingListViewModel(howYoRepository, planId, mainType)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
