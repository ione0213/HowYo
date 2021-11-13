package com.yuchen.howyo.plan.detail.view.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.source.HowYoRepository

class MapViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentSchedule: Schedule?
) : ViewModel() {

    // Schedule data from arguments, get companion list here
    private val _schedule = MutableLiveData<Schedule>().apply {
        value = argumentSchedule
    }

    val schedule: LiveData<Schedule>
        get() = _schedule

    // Handle leave map
    private val _leaveMap = MutableLiveData<Boolean>()

    val leaveMap: LiveData<Boolean>
        get() = _leaveMap

    fun leaveMap() {
        _leaveMap.value = true
    }

    fun onLeaveMap() {
        _leaveMap.value = null
    }
}