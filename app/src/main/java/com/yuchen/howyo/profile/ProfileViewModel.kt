package com.yuchen.howyo.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.navinterface.NavToPlanInterface
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.profile.friends.FriendFilter
import com.yuchen.howyo.signin.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentUserId: String
) : ViewModel(), NavToPlanInterface {
    private var _currentUser = MutableLiveData<User>()

    val currentUser: LiveData<User>
        get() = _currentUser

    // Plan data
    private val _plans = MutableLiveData<List<Plan>>()

    val plans: LiveData<List<Plan>>
        get() = _plans

    // Handle navigation to setting
    private val _navigateToSetting = MutableLiveData<Boolean?>()

    val navigateToSetting: LiveData<Boolean?>
        get() = _navigateToSetting

    // Handle navigation to friends
    private val _navigateToFriends = MutableLiveData<FriendFilter?>()

    val navigateToFriends: LiveData<FriendFilter?>
        get() = _navigateToFriends

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
        fetchLiveCurrentUserResult()
        fetchPlansResult()
    }

    private fun fetchLiveCurrentUserResult() {
        _currentUser = howYoRepository.getLiveUser(argumentUserId)
    }

    private fun fetchPlansResult() {
        _status.value = LoadApiStatus.LOADING

        val planResults = mutableSetOf<Plan>()

        coroutineScope.launch {
            val result = howYoRepository.getAllPlans()

            _plans.value = when (result) {
                is Result.Success -> {
                    result.data.filter { it.authorId == UserManager.userId }.forEach {
                        planResults.add(it)
                    }

                    result.data.filter { it.companionList?.contains(UserManager.userId) ?: false }
                        .forEach {
                            planResults.add(it)
                        }

                    planResults.toList()
                }
                else -> null
            }
        }
    }

    fun navigateToSetting() {
        _navigateToSetting.value = true
    }

    fun onSettingNavigated() {
        _navigateToSetting.value = null
    }

    fun navigateToFriend(type: FriendFilter) {
        _navigateToFriends.value = type
    }

    fun onFriendNavigated() {
        _navigateToFriends.value = null
    }

    fun setStatusDone() {
        _status.value = LoadApiStatus.DONE
    }
}
