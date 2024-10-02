package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.profile.friends.FriendFilter
import com.yuchen.howyo.profile.friends.item.FriendItemViewModel

class FriendItemViewModelFactory(
    private val howYoRepository: HowYoRepository,
    private val friendType: FriendFilter,
    private val userId: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(FriendItemViewModel::class.java) ->
                    FriendItemViewModel(howYoRepository, friendType, userId)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
