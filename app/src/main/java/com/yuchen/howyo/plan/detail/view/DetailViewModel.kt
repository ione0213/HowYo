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

    // Detail has product data from arguments
    private val _schedule = MutableLiveData<Schedule>().apply {
        value = arguments
    }

    val schedule: LiveData<Schedule>
        get() = _schedule
}