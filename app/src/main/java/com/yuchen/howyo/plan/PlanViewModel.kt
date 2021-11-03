package com.yuchen.howyo.plan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.*
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

    var tempDays = mutableListOf<Day>()

    //All Schedules of plan
    var allSchedules = MutableLiveData<List<Schedule>>()

    //Schedule list of days
    private val _schedules = MutableLiveData<List<Schedule>>()

    val schedules: LiveData<List<Schedule>>
        get() = _schedules

    var tempSchedules = mutableListOf<Schedule>()

    //Current user data
    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    var selectedDayPosition = MutableLiveData<Int>()

    private val _deletingPlan = MutableLiveData<Plan>()

    val deletingPlan: LiveData<Plan>
        get() = _deletingPlan

    private val _deletingDay = MutableLiveData<Day>()

    val deletingDay: LiveData<Day>
        get() = _deletingDay

    private val _deletingSchedule = MutableLiveData<Schedule>()

    val deletingSchedule: LiveData<Schedule>
        get() = _deletingSchedule

    private val _planResult = MutableLiveData<Boolean>()

    private val planResult: LiveData<Boolean>
        get() = _planResult

    private val _mainCheckListResult = MutableLiveData<Boolean>()

    private val mainCheckListResult: LiveData<Boolean>
        get() = _mainCheckListResult

    private val _checkListResult = MutableLiveData<Boolean>()

    private val checkListResult: LiveData<Boolean>
        get() = _checkListResult

    private val _dayResult = MutableLiveData<Boolean>()

    private val dayResult: LiveData<Boolean>
        get() = _dayResult

    private val _photoResult = MutableLiveData<Boolean>()

    private val photoResult: LiveData<Boolean>
        get() = _photoResult

    private val _updatePlanResult = MutableLiveData<Boolean>()

    private val updatePlanResult: LiveData<Boolean>
        get() = _updatePlanResult

    private val _navigateToHomeAfterDeletingPlan = MutableLiveData<Boolean>()

    val navigateToHomeAfterDeletingPlan: LiveData<Boolean>
        get() = _navigateToHomeAfterDeletingPlan

    private val _handleDaySuccess = MutableLiveData<Boolean>()

    val handleDaySuccess: LiveData<Boolean>
        get() = _handleDaySuccess

    private val _handleScheduleSuccess = MutableLiveData<Boolean>()

    val handleScheduleSuccess: LiveData<Boolean>
        get() = _handleScheduleSuccess


    // Handle navigation to detail
    private val _navigateToDetail = MutableLiveData<Schedule>()

    val navigateToDetail: LiveData<Schedule>
        get() = _navigateToDetail

    // Handle navigation to add schedule
    private val _navigateToAddSchedule = MutableLiveData<Day>()

    val navigateToAddSchedule: LiveData<Day>
        get() = _navigateToAddSchedule

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

    // Handle leave plan
    private val _leavePlan = MutableLiveData<Boolean>()

    val leavePlan: LiveData<Boolean>
        get() = _leavePlan

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

        getLiveDaysResult()
        setDefaultSelectedDay()
        getLiveSchedulesResult()
//        _schedules.value = listOf()
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

    fun delExistPlan(plan: Plan) {

        val daysResult = mutableListOf<Boolean>()
        val scheduleResult = mutableListOf<Boolean>()
        val scheduleImgResult = mutableListOf<Boolean>()

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {
                days.value?.forEach {
                    daysResult.add(deleteDay(it))
                }
                allSchedules.value?.forEach { schedule ->
                    scheduleResult.add(deleteSchedule(schedule))
                    schedule.photoFileNameList?.forEach {
                        deletePhoto(it)?.let { result -> scheduleImgResult.add(result) }
                    }
                }
                _mainCheckListResult.postValue(deleteMainCheckList(plan.id))
                _checkListResult.postValue(deleteCheckList(plan.id))
                _planResult.postValue(deletePlan(plan)!!)
                _photoResult.postValue(deletePhoto(plan.coverFileName))
            }

            when {
                !daysResult.contains(false)
                        && !scheduleResult.contains(false)
                        && planResult.value == true
                        && mainCheckListResult.value == true
                        && checkListResult.value == true
                        && photoResult.value == true -> {
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

    fun setDefaultSelectedDay() {
        //Select first item of days by default
        selectedDayPosition.value = 0
        Logger.i("setDefaultSelectedDay")

        filterSchedule()
    }

    fun getLiveDaysResult() {
        days = howYoRepository.getLiveDays(plan.value?.id!!)
        setStatusDone()
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
        val scheduleResult = mutableListOf<Boolean>()

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

                allSchedules.value?.filter { it.dayId == day.id }?.forEach { schedule ->
                    scheduleResult.add(deleteSchedule(schedule))
                }
                _updatePlanResult.postValue(updatePlan(HandleDayType.DELETE)!!)
            }

            when {
                !daysResult.contains(false)
                        && !scheduleResult.contains(false)
                        && updatePlanResult.value == true -> {
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

    private suspend fun updateSchedule(schedule: Schedule): Boolean {

        return when (val result = howYoRepository.updateSchedule(schedule)) {
            is Result.Success -> {
                result.data
            }
            else -> {
                false
            }
        }
    }

    private suspend fun deleteSchedule(schedule: Schedule): Boolean {

        return when (val result = howYoRepository.deleteSchedule(schedule)) {
            is Result.Success -> {
                result.data
            }
            else -> {
                false
            }
        }
    }

    fun delExistSchedule(schedule: Schedule) {

        val schedulesResult = mutableListOf<Boolean>()
        val scheduleList = schedules.value?.toList()
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {
                scheduleList?.forEach {

                    when {
                        it.position!! > schedule.position!! -> {
                            Logger.i("schedule.position-1:${schedule.position}")
                            val newSchedule = Schedule(
                                it.id,
                                it.planId,
                                it.dayId,
                                it.scheduleType,
                                it.title,
                                it.photoUrlList,
                                it.photoFileNameList,
                                it.latitude,
                                it.longitude,
                                it.startTime,
                                it.endTime,
                                it.budget,
                                it.refUrl,
                                it.notification,
                                it.position!! - 1,
                                it.address,
                                it.remark
                            )
                            schedulesResult.add(updateSchedule(newSchedule))
                            Logger.i("schedulesResult:$schedulesResult")
                        }
                        it.position!! == schedule.position!! -> {
                            Logger.i("schedule.position-del:${schedule.position}")
                            schedulesResult.add(deleteSchedule(it))
                            Logger.i("schedulesResult:$schedulesResult")
                        }
                    }
                }
            }

            when {
                !schedulesResult.contains(false) -> {
                    _handleScheduleSuccess.value = true
                }
            }
        }
    }

    fun getLiveSchedulesResult() {
        allSchedules = howYoRepository.getLiveSchedules(plan.value?.id!!)
        Logger.i("allSchedules:${allSchedules.value}")
        setStatusDone()
    }

    fun filterSchedule() {
        val currentDayId = selectedDayPosition.value?.let { days.value?.get(it)?.id ?: "" }
        _schedules.value =
            allSchedules.value
                ?.filter { it.dayId == currentDayId }?.sortedBy { it.position } ?: listOf()
        Logger.i("schedules:${schedules.value}")
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

    private suspend fun deletePhoto(fileName: String): Boolean? =
        when (val result = howYoRepository.deletePhoto(fileName)) {
            is Result.Success -> {
                result.data
            }
            else -> null
        }

    fun moveDay(from: Int, to: Int) {

        val newDays = when {
            tempDays.isNotEmpty() -> tempDays.toMutableList()
            else -> days.value?.toMutableList()
        }

        when (from > to) {
            true -> {
                newDays?.forEachIndexed { index, day ->
                    when {
                        day.position!! < to || day.position!! > from -> {
                        }
                        day.position!! >= to && day.position!! != from -> {
                            val newDay = Day(
                                day.id,
                                day.planId,
                                day.position!! + 1
                            )
                            newDays[index] = newDay
                        }
                        day.position!! == from -> {
                            val newDay = Day(
                                day.id,
                                day.planId,
                                to
                            )
                            newDays[index] = newDay
                        }
                    }
                }
            }
            false -> {
                newDays?.forEachIndexed { index, day ->
                    when {
                        day.position!! < from || day.position!! > to -> {
                        }
                        day.position!! > from -> {
                            val newDay = Day(
                                day.id,
                                day.planId,
                                day.position!! - 1
                            )

                            newDays[index] = newDay
                        }
                        day.position!! == from -> {

                            val newDay = Day(
                                day.id,
                                day.planId,
                                to
                            )

                            newDays[index] = newDay
                        }
                    }
                }
            }
        }
        if (newDays != null) {
            tempDays = newDays.toMutableList()
        }
    }

    suspend fun submitMovedDay() {

        val daysResult = mutableListOf<Boolean>()

        _status.value = LoadApiStatus.LOADING

        withContext(Dispatchers.IO) {

            tempDays.forEach { tempDay ->
                days.value?.forEach { day ->
                    when {
                        tempDay.position != day.position -> {
                            daysResult.add(updateDay(tempDay))
                        }
                    }
                }
            }
        }

        _handleDaySuccess.value = !daysResult.contains(false)
    }

    fun moveSchedule(from: Int, to: Int) {

        val newSchedules = when {
            tempSchedules.isNotEmpty() -> tempSchedules.toMutableList()
            else -> schedules.value?.toMutableList()
        }

        when (from > to) {
            true -> {
                newSchedules?.forEachIndexed { index, schedule ->
                    when {
                        schedule.position!! < to || schedule.position!! > from -> {
                        }
                        schedule.position!! >= to && schedule.position!! != from -> {
                            val newSchedule = Schedule(
                                schedule.id,
                                schedule.planId,
                                schedule.dayId,
                                schedule.scheduleType,
                                schedule.title,
                                schedule.photoUrlList,
                                schedule.photoFileNameList,
                                schedule.latitude,
                                schedule.longitude,
                                schedule.startTime,
                                schedule.endTime,
                                schedule.budget,
                                schedule.refUrl,
                                schedule.notification,
                                schedule.position!! + 1,
                                schedule.address,
                                schedule.remark
                            )
                            newSchedules[index] = newSchedule
                        }
                        schedule.position!! == from -> {
                            val newSchedule = Schedule(
                                schedule.id,
                                schedule.planId,
                                schedule.dayId,
                                schedule.scheduleType,
                                schedule.title,
                                schedule.photoUrlList,
                                schedule.photoFileNameList,
                                schedule.latitude,
                                schedule.longitude,
                                schedule.startTime,
                                schedule.endTime,
                                schedule.budget,
                                schedule.refUrl,
                                schedule.notification,
                                to,
                                schedule.address,
                                schedule.remark
                            )
                            newSchedules[index] = newSchedule
                        }
                    }
                }
            }
            false -> {
                newSchedules?.forEachIndexed { index, schedule ->
                    when {
                        schedule.position!! < from || schedule.position!! > to -> {
                        }
                        schedule.position!! > from -> {
                            val newSchedule = Schedule(
                                schedule.id,
                                schedule.planId,
                                schedule.dayId,
                                schedule.scheduleType,
                                schedule.title,
                                schedule.photoUrlList,
                                schedule.photoFileNameList,
                                schedule.latitude,
                                schedule.longitude,
                                schedule.startTime,
                                schedule.endTime,
                                schedule.budget,
                                schedule.refUrl,
                                schedule.notification,
                                schedule.position!! - 1,
                                schedule.address,
                                schedule.remark
                            )

                            newSchedules[index] = newSchedule
                        }
                        schedule.position!! == from -> {

                            val newSchedule = Schedule(
                                schedule.id,
                                schedule.planId,
                                schedule.dayId,
                                schedule.scheduleType,
                                schedule.title,
                                schedule.photoUrlList,
                                schedule.photoFileNameList,
                                schedule.latitude,
                                schedule.longitude,
                                schedule.startTime,
                                schedule.endTime,
                                schedule.budget,
                                schedule.refUrl,
                                schedule.notification,
                                to,
                                schedule.address,
                                schedule.remark
                            )

                            newSchedules[index] = newSchedule
                        }
                    }
                }
            }
        }
        if (newSchedules != null) {
            tempSchedules = newSchedules.toMutableList()
        }
    }

    suspend fun submitMoveSchedule() {

        val schedulesResult = mutableListOf<Boolean>()

        _status.value = LoadApiStatus.LOADING

        withContext(Dispatchers.IO) {

            tempSchedules.forEach { tempSchedule ->
                schedules.value?.forEach { schedule ->
                    when {
                        tempSchedule.position != schedule.position -> {
                            schedulesResult.add(updateSchedule(tempSchedule))
                        }
                    }
                }
            }
        }

        _handleScheduleSuccess.value = !schedulesResult.contains(false)
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

    fun checkDeleteSchedule(schedule: Schedule) {
        _deletingSchedule.value = schedule
    }

    fun onDeletedSchedule() {
        _deletingSchedule.value = null
    }

    fun navigateToDetail(schedule: Schedule) {
        _navigateToDetail.value = schedule
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }

    fun navigateToAddSchedule() {
        _navigateToAddSchedule.value = selectedDayPosition.value?.let { days.value?.get(it) }
    }

    fun onAddScheduleNavigated() {
        _navigateToAddSchedule.value = null
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
        setStatusDone()
        _navigateToHomeAfterDeletingPlan.value = null
    }

    fun setStatusDone() {
        _status.value = LoadApiStatus.DONE
    }

    fun leavePlan() {
        _leavePlan.value = true
    }
}