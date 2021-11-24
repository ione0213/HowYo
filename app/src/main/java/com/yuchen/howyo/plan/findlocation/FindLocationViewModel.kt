package com.yuchen.howyo.plan.findlocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Day
import com.yuchen.howyo.data.source.HowYoRepository

class FindLocationViewModel(
    private val howYoRepository: HowYoRepository,
//    private val argumentDays: List<Day>
) : ViewModel() {

    private val _days = MutableLiveData<List<Day>>()

    val days: LiveData<List<Day>>
        get() = _days

    var selectedDayPosition = MutableLiveData<Int>()
}
