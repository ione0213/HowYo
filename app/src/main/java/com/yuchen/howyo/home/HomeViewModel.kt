package com.yuchen.howyo.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.navinterface.NavToPlanInterface
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.signin.UserManager
import kotlinx.coroutines.*

class HomeViewModel(private val howYoRepository: HowYoRepository) : ViewModel(),
    NavToPlanInterface {
    private var _followingListOfCurrentUser = MutableLiveData<List<String>>()

    val followingListOfCurrentUser: LiveData<List<String>>
        get() = _followingListOfCurrentUser

    // Plan data
    private val _plans = MutableLiveData<List<Plan>>()

    val plans: LiveData<List<Plan>>
        get() = _plans

    // User id set
    private val _authorIds = MutableLiveData<Set<String>>()

    val authorIds: LiveData<Set<String>>
        get() = _authorIds

    // User data set
    private val _authorDataSet = MutableLiveData<Set<User>>()

    val authorDataSet: LiveData<Set<User>>
        get() = _authorDataSet

    // Handle navigation to notification
    private val _navigateToNotification = MutableLiveData<Boolean?>()

    val navigateToNotification: LiveData<Boolean?>
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
                when (val result = UserManager.userId?.let { howYoRepository.getUser(it) }) {
                    is Result.Success -> {
                        followingList = result.data.followingList?.toList() ?: listOf()
                    }
                    else -> {
                        howYoRepository.signOut()
                        UserManager.clear()
                    }
                }
            }

            _followingListOfCurrentUser.value = followingList
        }
    }

    fun fetchPlansResult() {
        _status.value = LoadApiStatus.LOADING

        coroutineScope.launch {
            val result =
                howYoRepository.getPlans(followingListOfCurrentUser.value ?: listOf())

            _plans.value = when (result) {
                is Result.Success -> result.data
                else -> null
            }
        }
    }

    fun setAuthorIdSet() {
        val authorIdSet = mutableSetOf<String>()

        plans.value?.forEach {
            it.authorId?.let { authorId -> authorIdSet.add(authorId) }
        }

        _authorIds.value = authorIdSet
    }

    fun getAuthorData() {
        val authorDataList = mutableSetOf<User>()

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                authorIds.value?.forEach { authorId ->
                    when (val result = howYoRepository.getUser(authorId)) {
                        is Result.Success -> {
                            authorDataList.add(result.data)
                        }
                        else -> {
                            // TODO error handling
                        }
                    }
                }
            }

            _authorDataSet.value = authorDataList.toSet()
            _refreshStatus.value = false
        }
    }

    fun setStatusDone() {
        _status.value = LoadApiStatus.DONE
    }

    fun navigateToNotification() {
        _navigateToNotification.value = true
    }

    fun onNotificationNavigated() {
        _navigateToNotification.value = null
    }
}
