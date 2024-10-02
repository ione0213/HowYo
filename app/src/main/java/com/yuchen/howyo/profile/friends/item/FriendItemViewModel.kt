package com.yuchen.howyo.profile.friends.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.profile.friends.FriendFilter
import kotlinx.coroutines.*

class FriendItemViewModel(
    private val howYoRepository: HowYoRepository,
    private val friendType: FriendFilter,
    private val argumentUserId: String
) : ViewModel() {
    private var _currentUser = MutableLiveData<User>()

    val currentUser: LiveData<User>
        get() = _currentUser

    private val _currentUserId = MutableLiveData<String>().apply {
        value = argumentUserId
    }

    private val currentUserId: LiveData<String>
        get() = _currentUserId

    private var _userIdList = MutableLiveData<List<String>>()

    val userIdList: LiveData<List<String>>
        get() = _userIdList

    private val _userList = MutableLiveData<List<User>>()

    val userList: LiveData<List<User>>
        get() = _userList

    private val _status = MutableLiveData<LoadApiStatus>()

    // Handle navigation to user profile
    private val _navigateToUserProfile = MutableLiveData<String?>()

    val navigateToUserProfile: LiveData<String?>
        get() = _navigateToUserProfile

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
        fetchUserIdList()
    }

    private fun fetchUserIdList() {
        var userIdList = listOf<String>()

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {
                when (val result = howYoRepository.getUser(argumentUserId)) {
                    is Result.Success -> {
                        userIdList = when (friendType) {
                            FriendFilter.FANS -> result.data.fansList?.toList() ?: listOf()
                            FriendFilter.FOLLOWING -> result.data.followingList?.toList()
                                ?: listOf()
                        }
                    }
                    else -> {
                        // TODO error handling
                    }
                }
            }

            _userIdList.value = userIdList
        }
    }

    fun fetchUserDataList() {
        val userDataList = mutableSetOf<User>()

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                userIdList.value?.forEach { userId ->
                    when (val result = howYoRepository.getUser(userId)) {
                        is Result.Success -> {
                            userDataList.add(result.data)
                        }
                        else -> {
                            // TODO error handling
                        }
                    }
                }
            }

            _userList.value = userDataList.toList()

            _status.value = LoadApiStatus.DONE
        }
    }

    private fun fetchLiveCurrentUserResult() {
        _currentUser = howYoRepository.getLiveUser(argumentUserId)
    }

    fun unFollow(user: User) {
        val fansList = user.fansList?.toMutableList()
        val newCurrentUser = currentUser.value
        val followingList = newCurrentUser?.followingList?.toMutableList()
        val currentUserId = currentUserId.value ?: ""

        when {
            user.fansList?.contains(currentUserId) == true -> {
                fansList?.removeAt(fansList.indexOf(currentUserId))
            }
        }

        when {
            newCurrentUser?.followingList?.contains(user.id) == true -> {
                followingList?.removeAt(followingList.indexOf(user.id))
            }
        }

        user.fansList = fansList
        newCurrentUser?.followingList = followingList

        newCurrentUser?.let { _currentUser.value = it }

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                howYoRepository.updateUser(user)
                _currentUser.value?.let { howYoRepository.updateUser(it) }
                howYoRepository.deleteFollowNotification(user.id, currentUserId)
            }

            fetchUserIdList()
        }
    }

    fun removeFans(user: User) {
        val followingList = user.followingList?.toMutableList()
        val newCurrentUser = currentUser.value
        val fansList = newCurrentUser?.fansList?.toMutableList()
        val currentUserId = currentUserId.value ?: ""

        when {
            user.followingList?.contains(currentUserId) == true -> {
                followingList?.removeAt(followingList.indexOf(currentUserId))
            }
        }

        when {
            newCurrentUser?.fansList?.contains(user.id) == true -> {
                fansList?.removeAt(fansList.indexOf(user.id))
            }
        }

        user.followingList = followingList
        newCurrentUser?.fansList = fansList

        newCurrentUser.let { _currentUser.value = it }

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                howYoRepository.updateUser(user)
                _currentUser.value?.let { howYoRepository.updateUser(it) }
                howYoRepository.deleteFollowNotification(currentUserId, user.id)
            }

            fetchUserIdList()
        }
    }

    fun navigateToUserProfile(userId: String) {
        _navigateToUserProfile.value = userId
    }

    fun onUserProfileNavigated() {
        _navigateToUserProfile.value = null
    }
}
