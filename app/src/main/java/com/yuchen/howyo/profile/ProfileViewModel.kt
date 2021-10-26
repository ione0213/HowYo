package com.yuchen.howyo.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.source.HowYoRepository

class ProfileViewModel(private val howYoRepository: HowYoRepository) : ViewModel() {

    //Plan data
    private val _plans = MutableLiveData<List<Plan>>()

    val plans: LiveData<List<Plan>>
        get() = _plans

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

    init {

        _plans.value = listOf(
            Plan(
                "1",
                "traveller",
                listOf(),
                "Go to Osaka",
                null,
                1634601600000,
                1634688000000,
                "Japan",
                listOf("Jack", "Mary"),
                listOf()
            ),
            Plan(
                "2",
                "traveller",
                listOf(),
                "Go to Nagoya",
                null,
                1634601600000,
                1634688000000,
                "Japan",
                listOf("Jack", "Mary", "Mark"),
                listOf()
            ),
            Plan(
                "3",
                "traveller",
                listOf(),
                "Go to Tokyo",
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/%E4%B8%8B%E8%BC%89%20(1).jpeg?alt=media&token=f5731312-ac2c-4a38-8e5e-4774ad32057a",
                1634601600000,
                1634688000000,
                "Japan",
                listOf("Jack", "Mary"),
                listOf()
            ),
        )
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
}