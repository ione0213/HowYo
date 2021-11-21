package com.yuchen.howyo.plan.companion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.signin.UserManager
import com.yuchen.howyo.util.Logger
import kotlinx.coroutines.*

class CompanionViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPlan: Plan?
) : ViewModel() {

    private var _plan = MutableLiveData<Plan>()

    val plan: LiveData<Plan>
        get() = _plan

    //Current user data
    private var _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    private val _friends = MutableLiveData<List<User>>()

    val friends: LiveData<List<User>>
        get() = _friends

    private val _friendsForShow = MutableLiveData<List<User>>()

    val friendsForShow: LiveData<List<User>>
        get() = _friendsForShow

    val keywords = MutableLiveData<String>()

    // Handle leave companion
    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {

        getLivePlanResult()
    }

    fun getCurrentUser() {

        coroutineScope.launch {

            withContext(Dispatchers.IO) {
                when (val result = UserManager.userId?.let { howYoRepository.getUser(it) }) {
                    is Result.Success -> {
                        _user.postValue(result.data!!)
                    }
                }
            }
        }
    }

    private fun getLivePlanResult() {

        _status.value = LoadApiStatus.LOADING

        when (argumentPlan?.id?.isNotEmpty()) {
            true -> {
                _plan = howYoRepository.getLivePlan(argumentPlan.id)
            }
        }
    }

    fun getFriendsData() {

        val friendDataList = mutableListOf<User>()

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                user.value?.followingList?.forEach { friendId ->

                    when (val result = howYoRepository.getUser(friendId)) {
                        is Result.Success -> {
                            friendDataList.add(result.data)
                        }
                    }
                }
            }

            _friends.value = friendDataList
        }
    }

    fun setCompanion(userId: String, type: CompanionType) {

        val newPlan = plan.value
        val companionList = newPlan?.companionList?.toMutableList()

        when (type) {
            CompanionType.ADD -> {
                when {
                    newPlan?.companionList?.contains(userId) != true -> {
                        companionList?.add(userId)
                    }
                }
            }
            CompanionType.REMOVE -> {
                when {
                    newPlan?.companionList?.contains(userId) == true -> {
                        companionList?.removeAt(companionList.indexOf(userId))
                    }
                }
            }
        }

        newPlan?.companionList = companionList

        _plan.value = newPlan!!

        coroutineScope.launch {
            _plan.value?.let { howYoRepository.updatePlan(it) }
        }
    }

    fun setFriendsForShow() {

        _friendsForShow.value = friends.value?.toList()
    }

    fun filter() {

        var newUsers = listOf<User>()

        when (keywords.value?.isEmpty()) {
            true -> {
                newUsers = friends.value ?: listOf()
            }
            false -> {

                newUsers =
                    friends.value?.filter { it.id.contains(keywords.value ?: "") } ?: listOf()
            }
        }

        _friendsForShow.value = newUsers
    }

    fun setStatusDone() {
        _status.value = LoadApiStatus.DONE
    }

    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }
}