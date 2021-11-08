package com.yuchen.howyo.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.signin.UserManager

class ProfileViewModel(private val howYoRepository: HowYoRepository) : ViewModel() {

    private var _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    //Plan data
    var plans = MutableLiveData<List<Plan>>()

    // Handle navigation to plan
    private val _navigateToPlan = MutableLiveData<Plan>()

    val navigateToPlan: LiveData<Plan>
        get() = _navigateToPlan

    // Handle navigation to setting
    private val _navigateToSetting = MutableLiveData<Boolean>()

    val navigateToSetting: LiveData<Boolean>
        get() = _navigateToSetting

    // Handle navigation to friends
    private val _navigateToFriends = MutableLiveData<Boolean>()

    val navigateToFriends: LiveData<Boolean>
        get() = _navigateToFriends

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    init {

        getLiveUserResult()
        getLiveDaysResult()
    }

    private fun getLiveUserResult() {

        _user = howYoRepository.getLiveUser(UserManager.currentUserEmail ?: "")
    }

    private fun getLiveDaysResult() {
        plans = howYoRepository.getLivePlans(listOf("userIdFromSharePreference"))
        setStatusDone()
    }

    fun navigateToPlan(plan: Plan) {
        _navigateToPlan.value = plan
    }

    fun onPlanNavigated() {
        _navigateToPlan.value = null
    }

    fun navigateToSetting() {
        _navigateToSetting.value = true
    }

    fun onSettingNavigated() {
        _navigateToSetting.value = null
    }

    fun navigateToFriend() {
        _navigateToFriends.value = true
    }

    fun onFriendNavigated() {
        _navigateToFriends.value = null
    }

    fun setStatusDone() {
        _status.value = LoadApiStatus.DONE
    }
}