package com.yuchen.howyo.favorite

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

class FavoriteViewModel(private val howYoRepository: HowYoRepository) : ViewModel(),
    NavToPlanInterface {
    // Plan data
    var plans = MutableLiveData<List<Plan>>()

    // User id set
    private val _authorIds = MutableLiveData<Set<String>>()

    val authorIds: LiveData<Set<String>>
        get() = _authorIds

    // User id set
    private val _authorDataList = MutableLiveData<Set<User>>()

    val authorDataList: LiveData<Set<User>>
        get() = _authorDataList

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
        fetchLivePlansResult()
    }

    private fun fetchLivePlansResult() {
        _status.value = LoadApiStatus.LOADING
        plans = howYoRepository.getLiveCollectedPublicPlans(listOf(UserManager.userId ?: ""))
    }

    fun setAuthorIdSet() {
        val authorIdSet = mutableSetOf<String>()

        plans.value?.forEach {
            it.authorId?.let { authorId -> authorIdSet.add(authorId) }
        }

        _authorIds.value = authorIdSet
    }

    fun fetchAuthorData() {
        val authorDataList = mutableSetOf<User>()

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                authorIds.value?.forEach { authorId ->
                    when (val result = howYoRepository.getUser(authorId)) {
                        is Result.Success -> authorDataList.add(result.data)
                    }
                }
            }

            _authorDataList.value = authorDataList.toSet()
        }
    }

    fun setStatusDone() {
        _status.value = LoadApiStatus.DONE
    }
}
