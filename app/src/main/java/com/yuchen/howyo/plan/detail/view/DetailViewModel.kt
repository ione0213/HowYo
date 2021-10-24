package com.yuchen.howyo.plan.detail.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.source.HowYoRepository

class DetailViewModel(
    private val howYoRepository: HowYoRepository,
    private val arguments: Schedule?,
): ViewModel() {

    // Detail data from arguments
    private val _schedule = MutableLiveData<Schedule>().apply {
        value = arguments
    }

    val schedule: LiveData<Schedule>
        get() = _schedule

    // Handle navigation to edit schedule
    private val _navigateToEditSchedule = MutableLiveData<Schedule>()

    val navigateToEditSchedule: LiveData<Schedule>
        get() = _navigateToEditSchedule

    // Handle navigation to view map
    private val _navigateToViewMap = MutableLiveData<Schedule>()

    val navigateToViewMap: LiveData<Schedule>
        get() = _navigateToViewMap

    fun navigateToEditSchedule() {
        _navigateToEditSchedule.value = schedule.value
    }

    fun onEditScheduleNavigated() {
        _navigateToEditSchedule.value = null
    }

    fun navigateToViewMap() {
        _navigateToViewMap.value = schedule.value
    }

    fun onViewMapNavigated() {
        _navigateToViewMap.value = null
    }
}