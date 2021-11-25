package com.yuchen.howyo.plan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.*
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.data.source.remote.DeleteDataType
import com.yuchen.howyo.home.notification.NotificationType
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.plan.checkorshoppinglist.MainItemType
import com.yuchen.howyo.signin.UserManager
import kotlinx.coroutines.*

class PlanViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPlan: Plan?,
    private val argumentAccessPlanType: AccessPlanType
) : ViewModel() {
    // Plan data
    private var _plan = MutableLiveData<Plan>().apply {
        value = argumentPlan
    }

    val plan: LiveData<Plan>
        get() = _plan

    val accessType: AccessPlanType
        get() = argumentAccessPlanType

    // live days list of plans
    var days = MutableLiveData<List<Day>>()

    var movedDays = mutableListOf<Day>()

    // All Schedules of plan
    var allSchedules = MutableLiveData<List<Schedule>>()

    // Schedule list of days
    private val _schedules = MutableLiveData<List<Schedule>>()

    val schedules: LiveData<List<Schedule>>
        get() = _schedules

    // Comments list for deleting plan
    var comments = MutableLiveData<List<Comment>>()

    var movedSchedules = mutableListOf<Schedule>()

    // Author data
    private val _author = MutableLiveData<User>()

    val author: LiveData<User>
        get() = _author

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

    private val _updatePrivacy = MutableLiveData<PlanPrivacy>()

    val updatePrivacy: LiveData<PlanPrivacy>
        get() = _updatePrivacy

    private val _updatePrivacyResult = MutableLiveData<Boolean>()

    val updatePrivacyResult: LiveData<Boolean>
        get() = _updatePrivacyResult

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

    private val _handleDayResult = MutableLiveData<Boolean>()

    val handleDayResult: LiveData<Boolean>
        get() = _handleDayResult

    private val _handleScheduleResult = MutableLiveData<Boolean>()

    val handleScheduleResult: LiveData<Boolean>
        get() = _handleScheduleResult

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
    private val _navigateToCompanion = MutableLiveData<Boolean>()

    val navigateToCompanion: LiveData<Boolean>
        get() = _navigateToCompanion

    // Handle navigation to group message
    private val _navigateToGroupMessage = MutableLiveData<Plan>()

    val navigateToGroupMessage: LiveData<Plan>
        get() = _navigateToGroupMessage

    // Handle navigation to locating companion
    private val _navigateToLocateCompanion = MutableLiveData<Plan>()

    val navigateToLocateCompanion: LiveData<Plan>
        get() = _navigateToLocateCompanion

    // Handle navigation to comment
    private val _navigateToComment = MutableLiveData<Plan>()

    val navigateToComment: LiveData<Plan>
        get() = _navigateToComment

    // Handle navigation to check or shopping list
    private val _navigateToCheckOrShoppingList = MutableLiveData<MainItemType>()

    val navigateToCheckOrShoppingList: LiveData<MainItemType>
        get() = _navigateToCheckOrShoppingList

    // Handle navigation to payment
    private val _navigateToPayment = MutableLiveData<Plan>()

    val navigateToPayment: LiveData<Plan>
        get() = _navigateToPayment

    // Handle navigation to group msg
    private val _navigateToGroupMsg = MutableLiveData<Plan>()

    val navigateToGroupMsg: LiveData<Plan>
        get() = _navigateToGroupMsg

    // Handle navigation to edit mode
    private val _navigateToEditPlan = MutableLiveData<Plan>()

    val navigateToEditPlan: LiveData<Plan>
        get() = _navigateToEditPlan

    // Handle navigation to edit cover
    private val _navigateToEditCover = MutableLiveData<Plan>()

    val navigateToEditCover: LiveData<Plan>
        get() = _navigateToEditCover

    // Handle navigation to copy plan
    private val _navigateToCopyPlan = MutableLiveData<Plan>()

    val navigateToCopyPlan: LiveData<Plan>
        get() = _navigateToCopyPlan

    // Handle navigation to author profile
    private val _navigateToAuthorProfile = MutableLiveData<String>()

    val navigateToAuthorProfile: LiveData<String>
        get() = _navigateToAuthorProfile

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
        fetchLivePlanResult()
        getLiveDaysResult()
        setDefaultSelectedDay()
        getLiveSchedulesResult()
        fetchAuthorResult()
        getLiveCommentsResult()
    }

    fun selectDay(position: Int) {
        selectedDayPosition.value = position
    }

    private fun fetchAuthorResult() {
        val authorId = when (argumentPlan?.id?.isNotEmpty()) {
            true -> argumentPlan.authorId
            else -> null
        }

        when {
            authorId?.isNotEmpty() == true -> {
                coroutineScope.launch {
                    _author.value =
                        when (val result = authorId.let { howYoRepository.getUser(it) }) {
                            is Result.Success -> result.data
                            else -> null
                        }
                }
            }
        }
    }

    private fun fetchLivePlanResult() {
        when (argumentPlan?.id?.isNotEmpty()) {
            true -> _plan = howYoRepository.getLivePlan(argumentPlan.id)
        }
    }

    fun delExistPlan(plan: Plan) {
        val planResult = mutableListOf<Boolean>()
        val daysResult = mutableListOf<Boolean>()
        val scheduleResult = mutableListOf<Boolean>()
        val scheduleImgResult = mutableListOf<Boolean>()
        val commentResult = mutableListOf<Boolean>()
        val checkShopListResult = mutableListOf<Boolean>()
        var groupMsgResult = false
        var notificationResult = false
        var paymentResult = false
        val photoResult = mutableListOf<Boolean>()

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {
                days.value?.let { deleteDaysWithBatch(it) }?.let { daysResult.add(it) }

                allSchedules.value?.let { deleteScheduleWithBatch(it) }
                    ?.let { scheduleResult.add(it) }

                allSchedules.value?.forEach { schedule ->
                    schedule.photoFileNameList?.forEach {
                        deletePhoto(it)?.let { result -> scheduleImgResult.add(result) }
                    }
                }

                comments.value?.let { deleteCommentWithBatch(it) }?.let { commentResult.add(it) }

                checkShopListResult.add(
                    deleteDataListsWithBatch(plan.id, DeleteDataType.CHECK_SHOP_LIST)
                )

                deletePlan(plan)?.let { planResult.add(it) }
                deletePhoto(plan.coverFileName)?.let { photoResult.add(it) }

                groupMsgResult = deleteDataListsWithBatch(plan.id, DeleteDataType.GROUP_MSG)

                notificationResult = deleteDataListsWithBatch(plan.id, DeleteDataType.NOTIFICATION)

                paymentResult = deleteDataListsWithBatch(plan.id, DeleteDataType.PAYMENT)
            }

            when {
                !daysResult.contains(false) &&
                        !scheduleResult.contains(false) &&
                        !commentResult.contains(false) &&
                        !planResult.contains(false) &&
                        !checkShopListResult.contains(false) &&
                        !photoResult.contains(false) &&
                        groupMsgResult &&
                        notificationResult &&
                        paymentResult -> {
                    onDeletedPlan()
                    _navigateToHomeAfterDeletingPlan.value = true
                }
            }
        }
    }

    private suspend fun deletePlan(plan: Plan): Boolean? =
        when (val result = howYoRepository.deletePlan(plan)) {
            is Result.Success -> result.data
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
            is Result.Success -> result.data
            else -> false
        }
    }

    fun updatePrivacy(type: PlanPrivacy) {
        _updatePrivacy.value = type
    }

    fun onUpdatedPrivacy() {
        _updatePrivacy.value = null
    }

    fun setPrivacy(type: PlanPrivacy) {
        var setPrivacyResult: Boolean

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val newPlan = plan.value?.copy(privacy = type.value)

                setPrivacyResult = when (
                    val result = newPlan?.let { howYoRepository.updatePlan(it) }
                ) {
                    is Result.Success -> result.data
                    else -> false
                }
            }

            _updatePrivacyResult.postValue(setPrivacyResult)
        }
    }

    fun setDefaultSelectedDay() {
        // Select first item of days by default
        selectedDayPosition.value = 0
    }

    private fun getLiveDaysResult() {
        days = howYoRepository.getLiveDays(argumentPlan?.id ?: "")
    }

    fun addNewDay() {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {
                _dayResult.postValue(addDay())
                _updatePlanResult.postValue(updatePlan(HandleDayType.NEW))
            }

            when {
                dayResult.value == true && updatePlanResult.value == true -> {
                    _handleDayResult.value = true
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
                        it.position > day.position -> {
                            val newDay = Day(it.id, it.planId, it.position - 1)
                            daysResult.add(updateDay(newDay))
                        }
                        it.position == day.position -> daysResult.add(deleteDay(it))
                    }
                }

                allSchedules.value?.filter { it.dayId == day.id }?.forEach { schedule ->
                    scheduleResult.add(deleteSchedule(schedule))
                }

                _updatePlanResult.postValue(updatePlan(HandleDayType.DELETE))
            }

            when {
                !daysResult.contains(false) &&
                        !scheduleResult.contains(false) &&
                        updatePlanResult.value == true -> {
                    _handleDayResult.value = true
                }
            }
        }
    }

    private suspend fun addDay(): Boolean {
        val newPosition = days.value?.maxByOrNull { it.position }?.position?.plus(1)
        val planId = plan.value?.id

        return when (newPosition?.let { howYoRepository.createDay(it, planId ?: "") }) {
            is Result.Success -> true
            else -> false
        }
    }

    private suspend fun updateDay(day: Day): Boolean {
        return when (val result = howYoRepository.updateDay(day)) {
            is Result.Success -> result.data
            else -> false
        }
    }

    private suspend fun deleteDay(day: Day): Boolean {
        return when (val result = howYoRepository.deleteDay(day)) {
            is Result.Success -> result.data
            else -> false
        }
    }

    private suspend fun deleteDaysWithBatch(dayList: List<Day>): Boolean =
        when (val result = howYoRepository.deleteDaysWithBatch(dayList)) {
            is Result.Success -> result.data
            else -> false
        }

    private suspend fun updateSchedule(schedule: Schedule): Boolean =
        when (val result = howYoRepository.updateSchedule(schedule)) {
            is Result.Success -> result.data
            else -> false
        }

    private suspend fun deleteSchedule(schedule: Schedule): Boolean =
        when (val result = howYoRepository.deleteSchedule(schedule)) {
            is Result.Success -> result.data
            else -> false
        }

    private suspend fun deleteScheduleWithBatch(scheduleList: List<Schedule>): Boolean =
        when (val result = howYoRepository.deleteScheduleWithBatch(scheduleList)) {
            is Result.Success -> result.data
            else -> false
        }

    fun delExistSchedule(schedule: Schedule) {
        val schedulesResult = mutableListOf<Boolean>()
        val scheduleList = schedules.value?.toList()

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {
                scheduleList?.forEach {
                    when {
                        it.position > schedule.position -> {
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
                                it.position - 1,
                                it.address,
                                it.remark
                            )

                            schedulesResult.add(updateSchedule(newSchedule))
                        }
                        it.position == schedule.position -> {
                            schedulesResult.add(deleteSchedule(it))
                        }
                    }
                }
            }

            when {
                !schedulesResult.contains(false) -> _handleScheduleResult.value = true
            }
        }
    }

    private fun getLiveSchedulesResult() {
        allSchedules = howYoRepository.getLiveSchedules(argumentPlan?.id ?: "")

        setStatusDone()
    }

    fun filterSchedule() {
        val currentDayId = selectedDayPosition.value?.let {
            when (it < days.value?.size ?: 0) {
                true -> days.value?.get(it)?.id ?: ""
                false -> days.value?.get(it.minus(1))?.id ?: ""
            }
        }

        _schedules.value = allSchedules.value
            ?.filter { it.dayId == currentDayId }
            ?.sortedBy { it.position }
            ?: listOf()
    }

    private suspend fun deletePhoto(fileName: String): Boolean? =
        when (val result = howYoRepository.deletePhoto(fileName)) {
            is Result.Success -> result.data
            else -> null
        }

    private suspend fun deleteCommentWithBatch(commentList: List<Comment>): Boolean? =
        when (val result = howYoRepository.deleteCommentWithBatch(commentList)) {
            is Result.Success -> result.data
            else -> null
        }

    fun moveDay(from: Int, to: Int) {
        val newDays = when {
            movedDays.isNotEmpty() -> movedDays.toMutableList()
            else -> days.value?.toMutableList()
        }

        val fullSchedules = when {
            movedSchedules.isNotEmpty() -> movedSchedules.toMutableList()
            else -> allSchedules.value?.toMutableList()
        }

        when (from > to) {
            true -> {
                newDays?.forEachIndexed { index, day ->
                    when {
                        day.position < to || day.position > from -> {

                        }
                        day.position >= to && day.position != from -> {
                            val newDay = Day(day.id, day.planId, day.position + 1)

                            newDays[index] = newDay

                            fullSchedules?.forEachIndexed { scheduleIndex, schedule ->
                                when (schedule.dayId) {
                                    day.id -> {
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
                                            schedule.position,
                                            schedule.address,
                                            schedule.remark
                                        )

                                        when {
                                            schedule.startTime != 0L -> {
                                                newSchedule.startTime =
                                                    schedule.startTime?.plus(1000 * 60 * 60 * 24)
                                            }
                                        }

                                        when {
                                            schedule.endTime != 0L -> {
                                                newSchedule.endTime =
                                                    schedule.endTime?.plus(1000 * 60 * 60 * 24)
                                            }
                                        }

                                        fullSchedules[scheduleIndex] = newSchedule
                                    }
                                }
                            }
                        }
                        day.position == from -> {
                            val newDay = Day(day.id, day.planId, to)

                            newDays[index] = newDay

                            fullSchedules?.forEachIndexed { scheduleIndex, schedule ->
                                when (schedule.dayId) {
                                    day.id -> {
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
                                            schedule.position,
                                            schedule.address,
                                            schedule.remark
                                        )

                                        when {
                                            schedule.startTime != 0L -> {
                                                newSchedule.startTime = schedule.startTime?.plus(
                                                    (1000 * 60 * 60 * 24) * to.minus(from)
                                                )
                                            }
                                        }

                                        when {
                                            schedule.endTime != 0L -> {
                                                newSchedule.endTime = schedule.endTime?.plus(
                                                    (1000 * 60 * 60 * 24) * to.minus(from)
                                                )
                                            }
                                        }

                                        fullSchedules[scheduleIndex] = newSchedule
                                    }
                                }
                            }
                        }
                    }
                }
            }
            false -> {
                newDays?.forEachIndexed { index, day ->
                    when {
                        day.position < from || day.position > to -> {

                        }
                        day.position > from -> {
                            val newDay = Day(day.id, day.planId, day.position - 1)

                            newDays[index] = newDay

                            fullSchedules?.forEachIndexed { scheduleIndex, schedule ->
                                when (schedule.dayId) {
                                    day.id -> {
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
                                            schedule.position,
                                            schedule.address,
                                            schedule.remark
                                        )

                                        when {
                                            schedule.startTime != 0L -> {
                                                newSchedule.startTime = schedule.startTime?.minus(
                                                    1000 * 60 * 60 * 24
                                                )
                                            }
                                        }

                                        when {
                                            schedule.endTime != 0L -> {
                                                newSchedule.endTime = schedule.endTime?.minus(
                                                    1000 * 60 * 60 * 24
                                                )
                                            }
                                        }

                                        fullSchedules[scheduleIndex] = newSchedule
                                    }
                                }
                            }
                        }
                        day.position == from -> {
                            val newDay = Day(day.id, day.planId, to)

                            newDays[index] = newDay

                            fullSchedules?.forEachIndexed { scheduleIndex, schedule ->
                                when (schedule.dayId) {
                                    day.id -> {
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
                                            schedule.position,
                                            schedule.address,
                                            schedule.remark
                                        )

                                        when {
                                            schedule.startTime != 0L -> {
                                                newSchedule.startTime = schedule.startTime?.plus(
                                                    (1000 * 60 * 60 * 24) * to.minus(from)
                                                )
                                            }
                                        }

                                        when {
                                            schedule.endTime != 0L -> {
                                                newSchedule.endTime = schedule.endTime?.plus(
                                                    (1000 * 60 * 60 * 24) * to.minus(from)
                                                )
                                            }
                                        }

                                        fullSchedules[scheduleIndex] = newSchedule
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (newDays != null) {
            movedDays = newDays.toMutableList()
        }

        if (fullSchedules != null) {
            movedSchedules = fullSchedules.toMutableList()
        }
    }

    suspend fun submitMovedDay() {
        val daysResult = mutableListOf<Boolean>()

        _status.value = LoadApiStatus.LOADING

        withContext(Dispatchers.IO) {
            movedDays.forEach { tempDay ->
                days.value?.forEach { oldDay ->
                    if (tempDay.id == oldDay.id && tempDay.position != oldDay.position) {
                        daysResult.add(updateDay(tempDay))
                    }
                }
            }
        }

        _handleDayResult.value = !daysResult.contains(false)
    }

    fun onSubmitMoveDay() {
        movedDays = mutableListOf()
    }

    fun moveSchedule(from: Int, to: Int) {
        val newSchedules = when {
            movedSchedules.isNotEmpty() -> movedSchedules.toMutableList()
            else -> schedules.value?.toMutableList()
        }

        when (from > to) {
            true -> {
                newSchedules?.forEachIndexed { index, schedule ->
                    when {
                        schedule.position < to || schedule.position > from -> {

                        }
                        schedule.position >= to && schedule.position != from -> {
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
                                schedule.position + 1,
                                schedule.address,
                                schedule.remark
                            )

                            newSchedules[index] = newSchedule
                        }
                        schedule.position == from -> {
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
                        schedule.position < from || schedule.position > to -> {

                        }
                        schedule.position > from -> {
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
                                schedule.position - 1,
                                schedule.address,
                                schedule.remark
                            )

                            newSchedules[index] = newSchedule
                        }
                        schedule.position == from -> {
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
            movedSchedules = newSchedules.toMutableList()
        }
    }

    suspend fun submitMoveSchedule(type: HandleScheduleType) {
        val schedulesResult = mutableListOf<Boolean>()

        _status.value = LoadApiStatus.LOADING

        withContext(Dispatchers.IO) {
            when (type) {
                HandleScheduleType.POSITION -> {
                    movedSchedules.forEach { tempSchedule ->
                        schedules.value?.forEach { schedule ->
                            when {
                                tempSchedule.position != schedule.position -> {
                                    schedulesResult.add(updateSchedule(tempSchedule))
                                }
                            }
                        }
                    }
                }
                HandleScheduleType.TIME -> {
                    movedSchedules.forEach { tempSchedule ->
                        allSchedules.value?.forEach { oldSchedule ->
                            if (tempSchedule.id == oldSchedule.id &&
                                (tempSchedule.startTime != oldSchedule.startTime ||
                                        tempSchedule.endTime != oldSchedule.endTime)
                            ) {
                                schedulesResult.add(updateSchedule(tempSchedule))
                            }
                        }
                    }
                }
            }
        }

        _handleScheduleResult.value = !schedulesResult.contains(false)
    }

    fun onSubmitMoveSchedule() {
        movedSchedules = mutableListOf()
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

    fun navigateToEditPlan() {
        _navigateToEditPlan.value = plan.value
    }

    fun onEditPlanNavigated() {
        _navigateToEditPlan.value = null
    }

    fun navigateToEditCover() {
        when (accessType) {
            AccessPlanType.EDIT -> _navigateToEditCover.value = _plan.value
            else -> {
            }
        }
    }

    fun onEditCoverNavigated() {
        _navigateToEditCover.value = null
    }

    fun navigateToCopyPlan() {
        _navigateToCopyPlan.value = plan.value
    }

    fun onCopyPlanNavigated() {
        _navigateToCopyPlan.value = null
    }

    fun navigateToAuthorProfile() {
        if (accessType == AccessPlanType.VIEW) {
            _navigateToAuthorProfile.value = plan.value?.authorId ?: ""
        }
    }

    fun onAuthorProfileNavigated() {
        _navigateToAuthorProfile.value = null
    }

    fun navigateToMapMode() {
        _navigateToMapMode.value = days.value
    }

    fun onMapModeNavigated() {
        _navigateToMapMode.value = null
    }

    fun navigateToCompanion() {
        _navigateToCompanion.value = true
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

    fun navigateToComment() {
        _navigateToComment.value = plan.value
    }

    fun onCommentNavigated() {
        _navigateToComment.value = null
    }

    fun navigateToCheckList(mainType: MainItemType) {
        _navigateToCheckOrShoppingList.value = mainType
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

    fun setLike(type: LikeType) {

        val newPlan = plan.value
        val likeList = newPlan?.likeList?.toMutableList()
        val currentUserId = UserManager.userId

        val notification = Notification(
            toUserId = plan.value?.authorId,
            fromUserId = currentUserId,
            notificationType = NotificationType.LIKE.type,
            planId = plan.value?.id,
            planCoverUrl = plan.value?.coverPhotoUrl
        )

        when (type) {
            LikeType.LIKE -> {
                when {
                    newPlan?.likeList?.contains(currentUserId) != true -> {
                        if (currentUserId != null) {
                            likeList?.add(currentUserId)
                        }
                    }
                }
            }
            LikeType.UNLIKE -> {
                when {
                    newPlan?.likeList?.contains(currentUserId) == true -> {
                        if (currentUserId != null) {
                            likeList?.removeAt(likeList.indexOf(currentUserId))
                        }
                    }
                }
            }
        }

        newPlan?.likeList = likeList

        _plan.value = newPlan

        coroutineScope.launch {
            _plan.value?.let { howYoRepository.updatePlan(it) }
            if (type == LikeType.LIKE) howYoRepository.createNotification(notification)
        }
    }

    fun setFavorite(type: FavoriteType) {
        val newPlan = plan.value
        val planCollectedList = newPlan?.planCollectedList?.toMutableList()
        val currentUserId = UserManager.userId

        when (type) {
            FavoriteType.COLLECT -> {
                when {
                    newPlan?.planCollectedList?.contains(currentUserId) != true -> {
                        if (currentUserId != null) {
                            planCollectedList?.add(currentUserId)
                        }
                    }
                }
            }
            FavoriteType.REMOVE -> {
                when {
                    newPlan?.planCollectedList?.contains(currentUserId) == true -> {
                        if (currentUserId != null) {
                            planCollectedList?.removeAt(planCollectedList.indexOf(currentUserId))
                        }
                    }
                }
            }
        }

        newPlan?.planCollectedList = planCollectedList

        _plan.value = newPlan

        coroutineScope.launch {
            _plan.value?.let { howYoRepository.updatePlan(it) }
        }
    }

    private fun getLiveCommentsResult() {
        comments = argumentPlan?.id?.let { howYoRepository.getLiveComments(it) }
            ?: MutableLiveData<List<Comment>>()
    }

    private suspend fun deleteDataListsWithBatch(planId: String, type: DeleteDataType): Boolean =
        when (val result = howYoRepository.deleteDataListsWithPlanID(planId, type)) {
            is Result.Success -> result.data
            else -> false
        }
}
