package com.yuchen.howyo.plan.detail.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.util.Logger

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

    fun navigateToEditSchedule() {
        Logger.i("navigateToDetail@@@@@@@@@@")
        _navigateToEditSchedule.value = schedule.value
    }

    fun onEditScheduleNavigated() {
        _navigateToEditSchedule.value = null
    }
}