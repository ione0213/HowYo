package com.yuchen.howyo.home

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

class HomeViewModel(private val howYoRepository: HowYoRepository) : ViewModel() {

    private var _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    private var _followingList = MutableLiveData<List<String>>()

    val followingList: LiveData<List<String>>
        get() = _followingList

    //Plan data
    private val _plans = MutableLiveData<List<Plan>>()

    val plans: LiveData<List<Plan>>
        get() = _plans

    // Handle navigation to plan
    private val _navigateToPlan = MutableLiveData<Plan>()

    val navigateToPlan: LiveData<Plan>
        get() = _navigateToPlan

    // Handle navigation to notification
    private val _navigateToNotification = MutableLiveData<Boolean>()

    val navigateToNotification: LiveData<Boolean>
        get() = _navigateToNotification

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        getUserResult()
    }

    private fun getUserResult() {

        var followingList = listOf<String>()
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {
//                _user.postValue(
                    when (val result = UserManager.userId?.let { howYoRepository.getUser(it) }) {
                        is Result.Success -> {
                            followingList = result.data.followingList?.toList() ?: listOf()
                        }
                        else -> null
                    }
//                )
            }

            _followingList.value = followingList
        }
    }

    fun getPlansResult() {

        _status.value = LoadApiStatus.LOADING

        coroutineScope.launch {

            val result = howYoRepository.getPlans(followingList.value ?: listOf())
            _plans.value = when (result) {
                is Result.Success -> result.data
                else -> null
            }

            _refreshStatus.value = false
        }
    }

    fun setStatusDone() {
        _status.value = LoadApiStatus.DONE
    }

    fun navigateToPlan(plan: Plan) {
        _navigateToPlan.value = plan
    }

    fun onPlanNavigated() {
        _navigateToPlan.value = null
    }

    fun navigateToNotification() {
        _navigateToNotification.value = true
    }

    fun onNotificationNavigated() {
        _navigateToNotification.value = null
    }
}