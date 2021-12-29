package com.yuchen.howyo.navinterface

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yuchen.howyo.data.Plan

interface NavToPlanInterface {

    companion object {
        private val _navigateToPlan = MutableLiveData<Plan?>()
    }

    val navigateToPlan: LiveData<Plan?>
        get() = _navigateToPlan

    fun navigateToPlan(plan: Plan) {
        _navigateToPlan.value = plan
    }

    fun onPlanNavigated() {
        _navigateToPlan.value = null
    }
}