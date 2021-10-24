package com.yuchen.howyo.profile.friends.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.profile.friends.FriendFilter

class FriendItemViewModel(
    private val howYoRepository: HowYoRepository,
    private val friendType: FriendFilter
): ViewModel() {

//    private val _users = MutableLiveData<PagingData<User>>()
//
//    val users: LiveData<PagingData<User>>
//        get() = _users
//
//    init {
//        _users.value =
//    }
//
//    fun setMockData(): Flow<PagingData<User>> {
//
//    }
}