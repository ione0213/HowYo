package com.yuchen.howyo.plan.detail.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.source.HowYoRepository

class DetailEditViewModel(
    private val howYoRepository: HowYoRepository,
    private val arguments: Schedule?,
) : ViewModel() {

    // Detail data from arguments
    private val _schedule = MutableLiveData<Schedule>().apply {
        value = arguments
    }

    val schedule: LiveData<Schedule>
        get() = _schedule

    val type = MutableLiveData<String>()

    val notification = MutableLiveData<Boolean>()

    val title = MutableLiveData<String>()

    val address = MutableLiveData<String>()

    val startTime = MutableLiveData<Long>()

    val endTime = MutableLiveData<Long>()

    val remark = MutableLiveData<String>()

    val budge = MutableLiveData<Int>()

    val refUrl = MutableLiveData<String>()

    val photoList = MutableLiveData<List<String>>()

    val selectedScheduleTypePosition = MutableLiveData<Int>()

    init {
        schedule.value?.let {
            type.value = it.scheduleType ?: ""
            notification.value = it.notification ?: false
            title.value = it.title ?: ""
            address.value = it.address ?: ""
            startTime.value = it.startTime ?: 0L
            endTime.value = it.endTime ?: 0L
            remark.value = it.remark ?: ""
            budge.value = it.budget ?: 0
            refUrl.value = it.refUrl ?: ""
            photoList.value = it.photoUrlList ?: listOf("")
        }
    }
}