package com.yuchen.howyo.profile.author

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Notification
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.home.notification.NotificationType
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.profile.friends.FriendFilter
import com.yuchen.howyo.signin.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AuthorProfileViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentUserId: String
) : ViewModel() {
    private var _author = MutableLiveData<User>()

    val author: LiveData<User>
        get() = _author

    private var _currentUser = MutableLiveData<User>()

    val currentUser: LiveData<User>
        get() = _currentUser

    // Plan data
    var plans = MutableLiveData<List<Plan>>()

    // Handle navigation to plan
    private val _navigateToPlan = MutableLiveData<Plan>()

    val navigateToPlan: LiveData<Plan>
        get() = _navigateToPlan

    // Handle navigation to friends
    private val _navigateToFriends = MutableLiveData<FriendFilter>()

    val navigateToFriends: LiveData<FriendFilter>
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
        fetchLiveUserResult()
        fetchLivePlansResult()
    }

    private fun fetchLiveUserResult() {
        _author = howYoRepository.getLiveUser(argumentUserId ?: "")

        _currentUser = howYoRepository.getLiveUser(UserManager.userId ?: "")
    }

    private fun fetchLivePlansResult() {
        plans = when (argumentUserId) {
            UserManager.userId -> howYoRepository.getLivePlans(listOf(argumentUserId))
            else -> howYoRepository.getLivePublicPlans(listOf(argumentUserId))
        }

        setStatusDone()
    }

    fun navigateToPlan(plan: Plan) {
        _navigateToPlan.value = plan
    }

    fun onPlanNavigated() {
        _navigateToPlan.value = null
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

    fun setFollow(type: FollowType) {
        val newAuthor = author.value
        val fansList = newAuthor?.fansList?.toMutableList()

        val newCurrentUser = currentUser.value
        val followingList = newCurrentUser?.followingList?.toMutableList()

        val currentUserId = UserManager.userId

        val notification = Notification(
            toUserId = author.value?.id,
            fromUserId = currentUserId,
            notificationType = NotificationType.FOLLOW.type
        )

        when (type) {
            FollowType.FOLLOW -> {
                when {
                    newAuthor?.fansList?.contains(currentUserId) != true -> {
                        if (currentUserId != null) {
                            fansList?.add(currentUserId)
                        }
                    }
                }

                when {
                    newCurrentUser?.followingList?.contains(newAuthor?.id) != true -> {
                        if (newAuthor?.id != null) {
                            followingList?.add(newAuthor.id)
                        }
                    }
                }
            }
            FollowType.UNFOLLOW -> {
                when {
                    newAuthor?.fansList?.contains(currentUserId) == true -> {
                        if (currentUserId != null) {
                            fansList?.removeAt(fansList.indexOf(currentUserId))
                        }
                    }
                }

                when {
                    newCurrentUser?.followingList?.contains(newAuthor?.id) == true -> {
                        if (newAuthor?.id != null) {
                            followingList?.removeAt(followingList.indexOf(newAuthor.id))
                        }
                    }
                }
            }
        }

        newAuthor?.fansList = fansList
        newCurrentUser?.followingList = followingList

        _author.value = newAuthor!!
        _currentUser.value = newCurrentUser!!

        coroutineScope.launch {
            _author.value?.let { howYoRepository.updateUser(it) }
            _currentUser.value?.let { howYoRepository.updateUser(it) }

            if (type == FollowType.FOLLOW) {
                howYoRepository.createNotification(notification)
            } else {
                author.value?.let {
                    howYoRepository.deleteFollowNotification(it.id, currentUserId!!)
                }
            }
        }
    }
}
