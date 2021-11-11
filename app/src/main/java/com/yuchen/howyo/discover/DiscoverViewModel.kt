package com.yuchen.howyo.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.signin.UserManager

class DiscoverViewModel(private val howYoRepository: HowYoRepository) : ViewModel() {

    //Plan data
    var plans = MutableLiveData<List<Plan>>()

    // Handle navigation to plan
    private val _navigateToPlan = MutableLiveData<Plan>()

    val navigateToPlan: LiveData<Plan>
        get() = _navigateToPlan

    init {


    }

    private fun getLivePlansResult() {

        plans = howYoRepository.getLivePlans(listOf())
    }

    fun navigateToPlan(plan: Plan) {
        _navigateToPlan.value = plan
    }

    fun onPlanNavigated() {
        _navigateToPlan.value = null
    }
}