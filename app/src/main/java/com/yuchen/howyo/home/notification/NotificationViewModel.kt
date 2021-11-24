package com.yuchen.howyo.home.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Notification
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.profile.author.FollowType
import com.yuchen.howyo.signin.UserManager
import kotlinx.coroutines.*

class NotificationViewModel(private val howYoRepository: HowYoRepository) : ViewModel() {

    private var _currentUser = MutableLiveData<User>()

    val currentUser: LiveData<User>
        get() = _currentUser

    // Notification data
    var notifications = MutableLiveData<List<Notification>>()

    // Plan data
    private val _plans = MutableLiveData<List<Plan>>()

    val plans: LiveData<List<Plan>>
        get() = _plans

    // User data set
    private val _userDataSet = MutableLiveData<Set<User>>()

    val userDataSet: LiveData<Set<User>>
        get() = _userDataSet

    private val _sendNotificationResult = MutableLiveData<Boolean>()

    val sendNotificationResult: LiveData<Boolean>
        get() = _sendNotificationResult

    // Handle navigation to user profile
    private val _navigateToUserProfile = MutableLiveData<String>()

    val navigateToUserProfile: LiveData<String>
        get() = _navigateToUserProfile

    // Handle navigation to plan
    private val _navigateToPlan = MutableLiveData<Plan>()

    val navigateToPlan: LiveData<Plan>
        get() = _navigateToPlan

    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {

        fetchLiveCurrentUserResult()
        fetchLiveNotificationsResult()
    }

    private fun fetchLiveCurrentUserResult() {

        _currentUser = howYoRepository.getLiveUser(UserManager.userId ?: "")
    }

    private fun fetchLiveNotificationsResult() {

        notifications = howYoRepository.getLiveNotifications()
    }

    fun fetchUserData() {

        val userIds = mutableSetOf<String>()
        val userDataSet = mutableSetOf<User>()

        notifications.value?.forEach {
            it.fromUserId?.let { userId -> userIds.add(userId) }
        }

        coroutineScope.launch {

            withContext(Dispatchers.IO) {
                userIds.forEach { userId ->
                    when (val result = howYoRepository.getUser(userId)) {
                        is Result.Success -> {
                            userDataSet.add(result.data)
                        }
                    }
                }
            }

            _userDataSet.value = userDataSet.toSet()
        }
    }

    fun setFollow(user: User, type: FollowType) {
        val fansList = user.fansList?.toMutableList()

        val newCurrentUser = currentUser.value
        val followingList = newCurrentUser?.followingList?.toMutableList()

        val currentUserId = UserManager.userId

        val notification = Notification(
            toUserId = user.id,
            fromUserId = currentUserId,
            notificationType = NotificationType.FOLLOW.type
        )

        when (type) {
            FollowType.FOLLOW -> {
                when {
                    user.fansList?.contains(currentUserId) != true -> {
                        if (currentUserId != null) {
                            fansList?.add(currentUserId)
                        }
                    }
                }

                when {
                    newCurrentUser?.followingList?.contains(user.id) != true -> {
                        followingList?.add(user.id)
                    }
                }
            }
            FollowType.UNFOLLOW -> {
                when {
                    user.fansList?.contains(currentUserId) == true -> {
                        if (currentUserId != null) {
                            fansList?.removeAt(fansList.indexOf(currentUserId))
                        }
                    }
                }

                when {
                    newCurrentUser?.followingList?.contains(user.id) == true -> {
                        followingList?.removeAt(followingList.indexOf(user.id))
                    }
                }
            }
        }

        user.fansList = fansList
        newCurrentUser?.followingList = followingList

        _currentUser.value = newCurrentUser!!

        coroutineScope.launch {
            howYoRepository.updateUser(user)
            _currentUser.value?.let { howYoRepository.updateUser(it) }
            _sendNotificationResult.postValue(
                if (type == FollowType.FOLLOW) {
                    when (val result = howYoRepository.createNotification(notification)) {
                        is Result.Success -> result.data
                        else -> false
                    }
                } else {
                    when (val result = howYoRepository.deleteFollowNotification(user.id, currentUserId!!)) {
                        is Result.Success -> result.data
                        else -> false
                    }
                }
            )
        }
    }

    fun navigateToUserProfile(userId: String) {
        _navigateToUserProfile.value = userId
    }

    fun onUserProfileNavigated() {
        _navigateToUserProfile.value = null
    }

    fun navigateToPlan(planId: String) {

        coroutineScope.launch {
            withContext(Dispatchers.IO) {

                _navigateToPlan.postValue(
                    when (val result = howYoRepository.getPlan(planId)) {
                        is Result.Success -> result.data
                        else -> null
                    }
                )
            }
        }
    }

    fun onPlanNavigated() {
        _navigateToPlan.value = null
    }

    fun onSentNotification() {
        _sendNotificationResult.value = null
    }
}
