package com.yuchen.howyo.plan.companion.locate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository

class LocateViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPlan: Plan?
) : ViewModel() {

    // Plan data from arguments, get companion list here
    private val _plan = MutableLiveData<Plan>().apply {
        value = argumentPlan
    }

    val plan: LiveData<Plan>
        get() = _plan

    //Get user lists with companion id
    private val _users = MutableLiveData<List<User>>()

    val user: LiveData<List<User>>
        get() = _users

    fun setMockUserData() {
        _users.value = listOf(
            User(
                "1",
                "",
                "",
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/man%20(1).png?alt=media&token=ac4dc055-3cfe-44d7-8bfe-3e0a2d3de6c1",
                "intro",
                121.56634663324172,
                25.043031998511577,
                listOf(),
                listOf()
            ),
            User(
                "2",
                "",
                "",
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/profile.png?alt=media&token=8d89552f-a15e-4a1c-a2c8-b417500bb63e",
                "intro",
                121.56546282814702,
                25.04201919216114,
                listOf(),
                listOf()
            ),
            User(
                "3",
                "",
                "",
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/kid.png?alt=media&token=cde1ac58-3a08-4419-b0bc-6c7f6be079bc",
                "intro",
                121.56424162524388,
                25.042606067477852,
                listOf(),
                listOf()
            )
        )
    }
}