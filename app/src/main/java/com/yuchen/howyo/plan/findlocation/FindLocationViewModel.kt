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

    init {
        _days.value = listOf(
            Day("001", "987", 0),
            Day("002", "987", 1),
            Day("003", "987", 2),
            Day("004", "987", 3),
            Day("005", "987", 4),
            Day("006", "987", 5),
            Day("007", "987", 6),
            Day("008", "987", 7),
        )
    }
}