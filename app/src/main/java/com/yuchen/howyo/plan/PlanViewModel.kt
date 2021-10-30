package com.yuchen.howyo.plan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Day
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.util.Logger
import kotlinx.coroutines.*

class PlanViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPlan: Plan,
    private val argumentAccessPlanType: AccessPlanType
) : ViewModel() {

    //Plan data
    private val _plan = MutableLiveData<Plan>().apply {
        value = argumentPlan
    }

    val plan: LiveData<Plan>
        get() = _plan

    val accessType: AccessPlanType
        get() = argumentAccessPlanType

    //live days list of plans
    var days = MutableLiveData<List<Day>>()

//    val days: LiveData<List<Day>>
//        get() = _days

    //Schedule list of days
    private val _schedules = MutableLiveData<List<Schedule>>()

    val schedules: LiveData<List<Schedule>>
        get() = _schedules

    //Current user data
    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    var selectedDayPosition = MutableLiveData<Int>()

    private val _deletingDay = MutableLiveData<Day>()

    val deletingDay: LiveData<Day>
        get() = _deletingDay

    private val _deletingPlan = MutableLiveData<Plan>()

    val deletingPlan: LiveData<Plan>
        get() = _deletingPlan

    private val _planResult = MutableLiveData<Boolean>()

    val planResult: LiveData<Boolean>
        get() = _planResult

    private val _mainCheckListResult = MutableLiveData<Boolean>()

    val mainCheckListResult: LiveData<Boolean>
        get() = _mainCheckListResult

    private val _checkListResult = MutableLiveData<Boolean>()

    val checkListResult: LiveData<Boolean>
        get() = _checkListResult

    private val _dayResult = MutableLiveData<Boolean>()

    private val dayResult: LiveData<Boolean>
        get() = _dayResult

    private val _updatePlanResult = MutableLiveData<Boolean>()

    private val updatePlanResult: LiveData<Boolean>
        get() = _updatePlanResult

    private val _navigateToHomeAfterDeletingPlan = MutableLiveData<Boolean>()

    val navigateToHomeAfterDeletingPlan: LiveData<Boolean>
        get() = _navigateToHomeAfterDeletingPlan

    private val _handleDaySuccess = MutableLiveData<Boolean>()

    val handleDaySuccess: LiveData<Boolean>
        get() = _handleDaySuccess

    // Handle navigation to detail
    private val _navigateToDetail = MutableLiveData<Schedule>()

    val navigateToDetail: LiveData<Schedule>
        get() = _navigateToDetail

    // Handle navigation to map mode
    private val _navigateToMapMode = MutableLiveData<List<Day>>()

    val navigateToMapMode: LiveData<List<Day>>
        get() = _navigateToMapMode

    // Handle navigation to companion
    private val _navigateToCompanion = MutableLiveData<User>()

    val navigateToCompanion: LiveData<User>
        get() = _navigateToCompanion

    // Handle navigation to group message
    private val _navigateToGroupMessage = MutableLiveData<Plan>()

    val navigateToGroupMessage: LiveData<Plan>
        get() = _navigateToGroupMessage

    // Handle navigation to locating companion
    private val _navigateToLocateCompanion = MutableLiveData<Plan>()

    val navigateToLocateCompanion: LiveData<Plan>
        get() = _navigateToLocateCompanion

    // Handle navigation to check or shopping list
    private val _navigateToCheckOrShoppingList = MutableLiveData<String>()

    val navigateToCheckOrShoppingList: LiveData<String>
        get() = _navigateToCheckOrShoppingList

    // Handle navigation to payment
    private val _navigateToPayment = MutableLiveData<Plan>()

    val navigateToPayment: LiveData<Plan>
        get() = _navigateToPayment

    // Handle navigation to group msg
    private val _navigateToGroupMsg = MutableLiveData<Plan>()

    val navigateToGroupMsg: LiveData<Plan>
        get() = _navigateToGroupMsg

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
        Logger.i("=========================")
        Logger.i("argumentPlan: $argumentPlan")
        Logger.i("argumentAccessPlanType: $argumentAccessPlanType")
        Logger.i("=========================")

        getLiveDaysResult()

        //Select first item of days by default
        selectedDayPosition.value = 0

        _user.value = User(
            id = "788",
            fansList = listOf("Mark", "Mary"),
            followingList = listOf(
                "Mark",
                "Mary",
                "22",
                "112121",
                "12143ffad",
                "ddfadfdfdf",
                "aaaa",
                "bbbb",
                "ccc"
            )
        )
    }

    fun selectDay(position: Int) {
        selectedDayPosition.value = position
    }

    fun getPlanResult() {

        val planId = plan.value?.id

        coroutineScope.launch {

            _plan.value = when (val result = planId?.let { howYoRepository.getPlan(it) }) {
                is Result.Success -> {
                    result.data
                }
                else -> {
                    _plan.value
                }
            }
        }
    }

    fun getLiveDaysResult() {
        days = howYoRepository.getLiveDays(plan.value?.id!!)
        _status.value = LoadApiStatus.DONE
    }

    fun addNewDay() {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            withContext(Dispatchers.IO) {

                _dayResult.postValue(addDay()!!)
                _updatePlanResult.postValue(
                    updatePlan(HandleDayType.NEW)!!
                )
            }

            when {
                dayResult.value == true && updatePlanResult.value == true -> {
                    _handleDaySuccess.value = true
                }
            }
        }
    }

    fun delExistDay(day: Day) {

        val daysResult = mutableListOf<Boolean>()

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {
                days.value?.forEach {

                    when {
                        it.position!! > day.position!! -> {
                            val newDay = Day(
                                it.id,
                                it.planId,
                                it.position!! - 1
                            )
                            daysResult.add(updateDay(newDay))
                        }
                        it.position!! == day.position!! -> {
                            daysResult.add(deleteDay(it))
                        }
                    }
                }
                _updatePlanResult.postValue(updatePlan(HandleDayType.DELETE)!!)
            }
            _dayResult.postValue(!daysResult.contains(false))

            when {
                dayResult.value == true && updatePlanResult.value == true -> {
                    _handleDaySuccess.value = true
                }
            }
        }
    }

    private suspend fun addDay(): Boolean {

        val newPosition = days.value?.maxByOrNull { it.position!! }!!.position?.plus(1)
        val planId = plan.value?.id

        return when (val result = howYoRepository.createDay(newPosition!!, planId!!)) {
            is Result.Success -> {
                result.data
            }
            else -> {
                false
            }
        }
    }

    private suspend fun updateDay(day: Day): Boolean {

        return when (val result = howYoRepository.updateDay(day)) {
            is Result.Success -> {
                result.data
            }
            else -> {
                false
            }
        }
    }

    private suspend fun deleteDay(day: Day): Boolean {

        return when (val result = howYoRepository.deleteDay(day)) {
            is Result.Success -> {
                result.data
            }
            else -> {
                false
            }
        }
    }

    fun delExistPlan(plan: Plan) {

        val daysResult = mutableListOf<Boolean>()

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {
                days.value?.forEach {

                    daysResult.add(deleteDay(it))
                }
                _mainCheckListResult.postValue(deleteMainCheckList(plan.id))
                _checkListResult.postValue(deleteCheckList(plan.id))
                _planResult.postValue(deletePlan(plan)!!)
            }
            _dayResult.postValue(!daysResult.contains(false))

            Logger.i("daysResult: $daysResult")

            Logger.i("dayResult.value: ${dayResult.value}")
            Logger.i("planResult.value: ${planResult.value}")
            Logger.i("mainCheckListResult.value: ${mainCheckListResult.value}")
            Logger.i("checkListResult.value: ${checkListResult.value}")
            when {
                !daysResult.contains(false)
                        && planResult.value == true
                        && mainCheckListResult.value == true
                        && checkListResult.value == true -> {
                    onDeletedPlan()
                    _navigateToHomeAfterDeletingPlan.value = true
                }
            }
        }
    }

    private suspend fun deletePlan(plan: Plan): Boolean? =
        when (val result = howYoRepository.deletePlan(plan)) {
            is Result.Success -> {
                result.data
            }
            else -> null
        }

//        coroutineScope.launch {

//            _status.value = LoadApiStatus.LOADING
//
//            val result = howYoRepository.deletePlan(plan)
//
//            _planResult.value = when (result) {
//                is Result.Success -> {
//                    result.data
//                }
//                is Result.Fail -> {
//                    null
//                }
//                is Result.Error -> {
//                    null
//                }
//                else -> {
//                    null
//                }
//            }
//        }


    private suspend fun updatePlan(type: HandleDayType): Boolean {

        _plan.value?.endDate = when (type) {
            HandleDayType.NEW -> {
                _plan.value?.endDate?.plus(60 * 60 * 24 * 1000)
            }
            HandleDayType.DELETE -> {
                _plan.value?.endDate?.minus(60 * 60 * 24 * 1000)
            }
        }

        return when (val result = _plan.value?.let { howYoRepository.updatePlan(it) }) {
            is Result.Success -> {
                result.data
            }
            else -> {
                false
            }
        }
    }

    private suspend fun deleteMainCheckList(planId: String): Boolean =
        when (val result = howYoRepository.deleteMainCheckList(planId)) {
            is Result.Success -> {
                result.data
            }
            else -> false
        }

    private suspend fun deleteCheckList(planId: String): Boolean =
        when (val result = howYoRepository.deleteCheckList(planId)) {
            is Result.Success -> {
                result.data
            }
            else -> false
        }


    fun checkDeletePlan() {
        _deletingPlan.value = plan.value
    }

    fun onDeletedPlan() {
        _deletingPlan.value = null
    }

    fun checkDeleteDay(day: Day) {
        _deletingDay.value = day
    }

    fun onDeletedDay() {
        _deletingDay.value = null
    }

    fun navigateToDetail(schedule: Schedule) {
        _navigateToDetail.value = schedule
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }

    fun navigateToMapMode() {
        _navigateToMapMode.value = days.value
    }

    fun onMapModeNavigated() {
        _navigateToMapMode.value = null
    }

    fun navigateToCompanion() {
        _navigateToCompanion.value = user.value
    }

    fun onCompanionNavigated() {
        _navigateToCompanion.value = null
    }

    fun navigateToGroupMessage() {
        _navigateToGroupMessage.value = plan.value
    }

    fun onGroupMessageNavigated() {
        _navigateToGroupMessage.value = null
    }

    fun navigateToLocateCompanion() {
        _navigateToLocateCompanion.value = plan.value
    }

    fun onLocateCompanionNavigated() {
        _navigateToLocateCompanion.value = null
    }

    fun navigateToCheckList(listType: String) {
        _navigateToCheckOrShoppingList.value = listType
    }

    fun onCheckLIstNavigated() {
        _navigateToCheckOrShoppingList.value = null
    }

    fun navigateToPayment() {
        _navigateToPayment.value = plan.value
    }

    fun onPaymentNavigated() {
        _navigateToPayment.value = null
    }

    fun onNavigatedHome() {
        _status.value = LoadApiStatus.DONE
        _navigateToHomeAfterDeletingPlan.value = null
    }
}