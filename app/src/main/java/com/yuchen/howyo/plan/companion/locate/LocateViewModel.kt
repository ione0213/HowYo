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

    //Get user lists with companion id
    var _companions = MutableLiveData<List<User>>()

    val companions:LiveData<List<User>>
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

    fun setMockUserData() {
        _companions.value = listOf(
            User(
                "1",
                "",
                "",
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/man%20(1).png?alt=media&token=ac4dc055-3cfe-44d7-8bfe-3e0a2d3de6c1",
                "intro",
                121.56634663324172,
                25.043031998511577,
                listOf(),
                listOf()
            ),
            User(
                "2",
                "",
                "",
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/profile.png?alt=media&token=8d89552f-a15e-4a1c-a2c8-b417500bb63e",
                "intro",
                121.56546282814702,
                25.04201919216114,
                listOf(),
                listOf()
            ),
            User(
                "3",
                "",
                "",
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/kid.png?alt=media&token=cde1ac58-3a08-4419-b0bc-6c7f6be079bc",
                "intro",
                121.56424162524388,
                25.042606067477852,
                listOf(),
                listOf()
            )
        )
    }

    fun getCompanionsData() {

        val companionList = plan.value?.companionList?.toMutableSet()

        plan.value?.authorId?.let { companionList?.add(it) }

        companionList?.removeIf { it ==  UserManager.userId}

        coroutineScope.launch {
//            _companions = plan.value?.companionList?.let { howYoRepository.getLiveUsers(it) }!!

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