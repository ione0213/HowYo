package com.yuchen.howyo.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.navinterface.NavToPlanInterface
import com.yuchen.howyo.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiscoverViewModel(private val howYoRepository: HowYoRepository) : ViewModel(),
    NavToPlanInterface {
    private val _plansForDisplay = MutableLiveData<List<Plan>>()

    val plansForDisplay: LiveData<List<Plan>>
        get() = _plansForDisplay

    // Plan data
    private val _plans = MutableLiveData<List<Plan>>()

    val plans: LiveData<List<Plan>>
        get() = _plans

    // User id set
    private val _authorIds = MutableLiveData<Set<String>>()

    val authorIds: LiveData<Set<String>>
        get() = _authorIds

    // User id set
    private val _authorDataSet = MutableLiveData<Set<User>>()

    val authorDataSet: LiveData<Set<User>>
        get() = _authorDataSet

    val keywords = MutableLiveData<String>()

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
        fetchPlansResult()
    }

    fun fetchPlansResult() {
        _status.value = LoadApiStatus.LOADING

        coroutineScope.launch {
            val result = howYoRepository.getAllPublicPlans()

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

    fun fetchAuthorData() {
        val authorDataList = mutableSetOf<User>()

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                authorIds.value?.forEach { authorId ->
                    when (val result = howYoRepository.getUser(authorId)) {
                        is Result.Success -> authorDataList.add(result.data)
                        else -> {
                            // TODO Error Handling
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

    fun setPlansForShow() {
        _plansForDisplay.value = plans.value?.toList()
    }

    fun filter() {
        var filteredPlans = listOf<Plan>()

        when (keywords.value?.isEmpty()) {
            true -> filteredPlans = plans.value ?: listOf()
            false -> {
                filteredPlans =
                    plans.value?.filter { it.title?.contains(keywords.value ?: "") ?: false }
                        ?: listOf()
            }

            null -> {}
        }

        _plansForDisplay.value = filteredPlans
    }
}
