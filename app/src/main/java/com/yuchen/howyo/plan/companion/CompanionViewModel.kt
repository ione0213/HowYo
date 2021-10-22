package com.yuchen.howyo.plan.companion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.util.Logger

class CompanionViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentUser: User
//    private val argumentPlan: Plan
    ): ViewModel() {

    //Current user data
    private val _user = MutableLiveData<User>().apply {
        value = argumentUser
    }

    val user: LiveData<User>
        get() = _user

//    //Plan data
//    private val _plan = MutableLiveData<Plan>().apply {
//        value = argumentPlan
//    }
//
//    val plan: LiveData<Plan>
//        get() = _plan

    // Handle leave companion
    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    fun leave() {
        Logger.i("Leaveeeee")
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }
}