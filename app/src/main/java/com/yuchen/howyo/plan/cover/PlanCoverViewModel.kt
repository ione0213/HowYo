package com.yuchen.howyo.plan.cover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.util.Logger
import java.util.*

class PlanCoverViewModel(private val howYoRepository: HowYoRepository) : ViewModel() {

    // Handle leave plan cover
    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    // Handle open date range picker
    private val _selectDate = MutableLiveData<Boolean>()

    val selectDate: LiveData<Boolean>
        get() = _selectDate

    // Handle save plan cover
    private val _save = MutableLiveData<Boolean>()

    val save: LiveData<Boolean>
        get() = _save

    val startDate = MutableLiveData<Long>()

    val endDate = MutableLiveData<Long>()

    init {

        val calendar = Calendar.getInstance()

        startDate.value = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_YEAR, 1)

        endDate.value = calendar.timeInMillis
    }

    fun save() {
        _save.value = true
    }

    fun onSaveCompleted() {
        _save.value = null
    }

    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }

    fun selectDate() {
        _selectDate.value = true
    }

    fun onSelectedDate() {
        _selectDate.value = null
    }
}