package com.yuchen.howyo.plan.companion.locate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.signin.UserManager
import kotlinx.coroutines.*

class LocateViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPlan: Plan?
) : ViewModel() {

    // Plan data from arguments, get companion list here
    private val _plan = MutableLiveData<Plan>().apply {
        value = argumentPlan
    }

    val plan: LiveData<Plan>
        get() = _plan

    // User lists with companion id
    private var _companions = MutableLiveData<List<User>>()

    val companions: LiveData<List<User>>
        get() = _companions

    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    init {
        _status.value = LoadApiStatus.LOADING
    }

    fun fetchCompanionsData() {

        val companionList = plan.value?.companionList?.toMutableSet()

        plan.value?.authorId?.let { companionList?.add(it) }

        companionList?.removeIf { it == UserManager.userId }

        coroutineScope.launch {

            val result = howYoRepository.getUsers(companionList?.toList() ?: listOf())
            _companions.value = when (result) {
                is Result.Success -> result.data
                else -> null
            }
        }
    }

    fun onLocateDone() {
        _status.value = LoadApiStatus.DONE
    }
}
