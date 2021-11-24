package com.yuchen.howyo.plan.groupmessage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.*
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.signin.UserManager
import kotlinx.coroutines.*

class GroupMessageViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPlan: Plan?
) : ViewModel() {
    // Plan data from arguments
    private val _plan = MutableLiveData<Plan>().apply {
        value = argumentPlan
    }

    val plan: LiveData<Plan>
        get() = _plan

    // All messages of plan
    var allGroupMessages = MutableLiveData<List<GroupMessage>>()

    private val _groupMessagesData = MutableLiveData<List<GroupMessageData>>()

    val groupMessages: LiveData<List<GroupMessageData>>
        get() = _groupMessagesData

    private val _groupMsgResult = MutableLiveData<Boolean>()

    val groupMsgResult: LiveData<Boolean>
        get() = _groupMsgResult

    var message = MutableLiveData<String>()

    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        fetchLiveGroupMsgResult()
    }

    private fun fetchLiveGroupMsgResult() {
        allGroupMessages = plan.value?.id?.let { howYoRepository.getLiveGroupMessages(it) }!!
    }

    fun fetchUsersResult() {
        val groupMsgData = mutableListOf<GroupMessageData>()

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                allGroupMessages.value?.forEach { message ->
                    when (val result = message.userId?.let { howYoRepository.getUser(it) }) {
                        is Result.Success -> {
                            groupMsgData.add(
                                GroupMessageData(
                                    message.userId,
                                    result.data.name,
                                    result.data.avatar,
                                    message.message,
                                    message.createdTime
                                )
                            )
                        }
                    }
                }

                _groupMessagesData.postValue(groupMsgData)
            }
        }
    }

    fun submitMessage() {
        val message = GroupMessage(
            planId = plan.value?.id,
            userId = UserManager.userId,
            message = message.value
        )

        coroutineScope.launch {
            val result = howYoRepository.createGroupMessage(message)

            _groupMsgResult.value = when (result) {
                is Result.Success -> result.data
                else -> null
            }
        }
    }

    fun onSubmittedMessage() {
        message.value = null
    }
}
