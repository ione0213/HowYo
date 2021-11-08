package com.yuchen.howyo.plan.groupmessage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.GroupMessage
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.source.HowYoRepository

class GroupMessageViewModel(
    private val howYoRepository: HowYoRepository,
    private val plan: Plan?
) : ViewModel() {

    private val _groupMessages = MutableLiveData<List<GroupMessage>>()

    val groupMessages: LiveData<List<GroupMessage>>
        get() = _groupMessages

    init {
        _groupMessages.value = listOf(
            GroupMessage(
                "A123",
                "Mark",
                "This is Mark",
                1635138065
                ),
            GroupMessage(
                "A123",
                "Traveller",
                "Hi Mark",
                1635138300
            ),
            GroupMessage(
                "A123",
                "Mark",
                "I'm not here",
                1635139000
            ),
            GroupMessage(
                "A123",
                "Mary",
                "Where are you?",
                1635139000
            )
        )
    }
}