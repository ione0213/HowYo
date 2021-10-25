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

    private val _users = MutableLiveData<List<User>>()

    val users: LiveData<List<User>>
        get() = _users

    init {
        _users.value = listOf(
            User(
                "HIHI",
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/man%20(1).png?alt=media&token=ac4dc055-3cfe-44d7-8bfe-3e0a2d3de6c1"
            ),
            User(
                "Jack",
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/kid.png?alt=media&token=cde1ac58-3a08-4419-b0bc-6c7f6be079bc"
            ),
            User(
                "Mary",
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/profile.png?alt=media&token=8d89552f-a15e-4a1c-a2c8-b417500bb63e"
            )
        )
    }
}