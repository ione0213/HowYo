package com.yuchen.howyo.plan.cover

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.data.*
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.ext.toHour
import com.yuchen.howyo.ext.toMinute
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.plan.CheckItemType
import com.yuchen.howyo.signin.UserManager
import com.yuchen.howyo.util.Util.getString
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue
import kotlinx.coroutines.*

class PlanCoverViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPlan: Plan?
) : ViewModel() {
    private var _currentUser = MutableLiveData<User>()

    val currentUser: LiveData<User>
        get() = _currentUser

    private val _isNewPlan = MutableLiveData<Boolean>()

    val isNewPlan: LiveData<Boolean>
        get() = _isNewPlan

    val plan = MutableLiveData<Plan>().apply {
        value = when (argumentPlan) {
            null -> {
                val today = Calendar.getInstance()
                val tomorrow = Calendar.getInstance()
                tomorrow.add(Calendar.DAY_OF_YEAR, 1)

                Plan(
                    coverFileName = "",
                    startDate = today.timeInMillis,
                    endDate = tomorrow.timeInMillis
                )
            }
            else -> argumentPlan
        }
    }

    private val _planId = MutableLiveData<String>()

    val planId: LiveData<String>
        get() = _planId

    private val _planPhoto = MutableLiveData<PhotoData>()

    val planPhotoData: LiveData<PhotoData>
        get() = _planPhoto

    val startDateFromUser = MutableLiveData<Long>()

    private val previousStartDate = MutableLiveData<Long>()

    val endDateFromUser = MutableLiveData<Long>()

    // Days list for updating days and schedules when plan is updated
    private val _days = MutableLiveData<List<Day>>()

    val days: LiveData<List<Day>>
        get() = _days

    // Schedules list for updating when plan is updated
    private val _schedules = MutableLiveData<List<Schedule>>()

    val schedules: LiveData<List<Schedule>>
        get() = _schedules

    // Handle the submit plan
    private val _isSavePlan = MutableLiveData<Boolean?>()

    val isSavePlan: LiveData<Boolean?>
        get() = _isSavePlan

    // Handle the plan cover is ready or not
    private val _isCoverPhotoReady = MutableLiveData<Boolean?>()

    val isCoverPhotoReady: LiveData<Boolean?>
        get() = _isCoverPhotoReady

    private val _isPlanUpdated = MutableLiveData<Boolean>()

    val isPlanUpdated: LiveData<Boolean>
        get() = _isPlanUpdated

    // Handle the days data is ready or not
    private val _isDaysReady = MutableLiveData<Boolean>()

    private val isDaysReady: LiveData<Boolean>
        get() = _isDaysReady

    // Handle the main data of the check and shopping list are ready or not
    private val _isChkListReady = MutableLiveData<Boolean>()

    private val isChkListReady: LiveData<Boolean>
        get() = _isChkListReady

    // Handle the main data of the check and shopping list are ready or not
    private val _isAllDataReady = MutableLiveData<Boolean>()

    val isAllDataReady: LiveData<Boolean>
        get() = _isAllDataReady

    // Handle leave plan cover
    private val _leave = MutableLiveData<Boolean?>()

    val leave: LiveData<Boolean?>
        get() = _leave

    // Handle open date range picker
    private val _selectDate = MutableLiveData<Boolean?>()

    val selectDate: LiveData<Boolean?>
        get() = _selectDate

    // Handle add the cover photo by camera
    private val _takePhoto = MutableLiveData<Boolean?>()

    val takePhoto: LiveData<Boolean?>
        get() = _takePhoto

    // Handle add the cover photo by selecting
    private val _selectPhoto = MutableLiveData<Boolean?>()

    val selectPhoto: LiveData<Boolean?>
        get() = _selectPhoto

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // Handle the error for submit plan
    private val _invalidPlan = MutableLiveData<Int>()

    val invalidPlan: LiveData<Int>
        get() = _invalidPlan

    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        setInitData()

        plan.value?.id?.let {
            fetchDaysResult()
            fetchSchedulesResult()
        }

        fetchLiveCurrentUserResult()
    }

    private fun fetchLiveCurrentUserResult() {
        val userId = UserManager.userId

        _currentUser = howYoRepository.getLiveUser(userId ?: "")
    }

    private fun setInitData() {
        // set the default value for duration of the plan
        val calendar = Calendar.getInstance()

        startDateFromUser.value = plan.value?.startDate ?: calendar.timeInMillis
        previousStartDate.value = startDateFromUser.value?.toLong()

        calendar.add(Calendar.DAY_OF_YEAR, 1)

        endDateFromUser.value = plan.value?.endDate ?: calendar.timeInMillis

        _planPhoto.value = PhotoData(
            Uri.parse(getString(R.string.default_cover)),
            plan.value?.coverPhotoUrl,
            plan.value?.coverFileName
        )

        _isNewPlan.value = when (argumentPlan) {
            null -> true
            else -> false
        }
    }

    fun prepareSubmitPlan() {
        when {
            plan.value?.title.isNullOrEmpty() -> _invalidPlan.value = INVALID_FORMAT_TITLE_EMPTY
            else -> savePlan()
        }
    }

    private fun savePlan() {
        _isSavePlan.value = true
    }

    fun onSavedPlan() {
        _isSavePlan.value = null
    }

    fun handlePlanCover() {
        val coverPhotoResult = mutableListOf<Boolean>()

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {
                when (plan.value?.id.isNullOrEmpty()) {
                    true -> coverPhotoResult.add(uploadCoverPhoto())
                    false -> {
                        when (planPhotoData.value?.isDeleted) {
                            true -> {
                                when {
                                    planPhotoData.value?.fileName?.isNotEmpty() == true -> {
                                        coverPhotoResult.add(
                                            deletePhoto(planPhotoData.value?.fileName ?: "")
                                        )
                                    }
                                    else -> {

                                    }
                                }

                                coverPhotoResult.add(uploadCoverPhoto())
                            }
                            else -> {

                            }
                        }
                    }
                }
            }

            when {
                !coverPhotoResult.contains(false) -> _isCoverPhotoReady.value = true
            }
        }
    }

    private suspend fun uploadCoverPhoto(): Boolean {
        val uri = planPhotoData.value?.uri
        val formatter = SimpleDateFormat("yyyy_mm_dd_HH_mm_ss", Locale.getDefault())
        val fileName = "${UserManager.currentUserEmail}_${formatter.format(Date())}"
        val uploadResult: Boolean

        plan.value?.coverFileName = fileName

        when (val result = uri?.let { howYoRepository.uploadPhoto(it, fileName) }) {
            is Result.Success -> {
                plan.value?.coverPhotoUrl = result.data
                uploadResult = true
            }
            else -> uploadResult = true
        }

        return uploadResult
    }

    private suspend fun deletePhoto(fileName: String): Boolean =
        when (val result = howYoRepository.deletePhoto(fileName)) {
            is Result.Success -> result.data
            else -> false
        }

    fun createPlan() {
        val plan = plan.value

        plan?.authorId = currentUser.value?.id

        coroutineScope.launch {
            val result = plan?.let { howYoRepository.createPlan(it) }

            _planId.value = when (result) {
                is Result.Success -> result.data
                is Result.Fail -> null
                is Result.Error -> null
                else -> null
            }

            _isCoverPhotoReady.value = null
        }
    }

    fun updatePlan() {
        coroutineScope.launch {
            val result = plan.value?.let { howYoRepository.updatePlan(it) }

            _isPlanUpdated.value = when (result) {
                is Result.Success -> result.data
                else -> false
            }
        }
    }

    private suspend fun updateSchedule(schedule: Schedule): Boolean {
        return when (val result = howYoRepository.updateSchedule(schedule)) {
            is Result.Success -> result.data
            else -> false
        }
    }

    fun createRelatedCollection() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                _isDaysReady.postValue(createDays())
                _isChkListReady.postValue(createDefaultCheckList())
            }

            when {
                isDaysReady.value == true && isChkListReady.value == true -> {
                    _isAllDataReady.value = true
                    _status.value = LoadApiStatus.DONE
                }
            }
        }
    }

    fun updateRelatedCollection() {
        val dayList = days.value?.toList()
        val scheduleList = schedules.value?.toList()

        val newDayCount =
            (startDateFromUser.value?.let { startDate ->
                endDateFromUser.value?.minus(startDate)?.div((60 * 60 * 24 * 1000))
            })?.toInt()?.plus(1)

        val differenceInDayCount = newDayCount?.minus(dayList?.size ?: 0)

        val scheduleResults = mutableListOf<Boolean>()
        val dayResults = mutableListOf<Boolean>()

        if (differenceInDayCount != null) {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    when {
                        plan.value?.startDate != previousStartDate.value -> {
                            val days = dayList?.sortedBy { it.position }

                            days?.forEach { day ->
                                scheduleList?.filter { it.dayId == day.id }
                                    ?.forEach { schedule ->
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

                                        val date = Calendar.getInstance()
                                        date.timeInMillis = plan.value?.startDate?.plus(
                                            (1000 * 60 * 60 * 24 * day.position)
                                        ) ?: 0L

                                        when {
                                            schedule.startTime != 0L && schedule.startTime != null -> {

                                                val hour = schedule.startTime?.toHour() ?: 0
                                                val minute = schedule.startTime?.toMinute() ?: 0

                                                date.set(Calendar.HOUR_OF_DAY, hour)
                                                date.set(Calendar.MINUTE, minute)

                                                newSchedule.startTime = date.timeInMillis
                                            }
                                        }

                                        when {
                                            schedule.endTime != 0L && schedule.startTime != null -> {

                                                val hour = schedule.endTime?.toHour() ?: 0
                                                val minute = schedule.endTime?.toMinute() ?: 0

                                                date.set(Calendar.HOUR_OF_DAY, hour)
                                                date.set(Calendar.MINUTE, minute)

                                                newSchedule.endTime = date.timeInMillis
                                            }
                                        }

                                        scheduleResults.add(updateSchedule(newSchedule))
                                    }
                            }
                        }
                        else -> {

                        }
                    }

                    when {
                        differenceInDayCount > 0 -> {
                            val lastPosition =
                                dayList?.maxByOrNull { it.position }?.position

                            for (position in 1..differenceInDayCount) {
                                val result =
                                    plan.value?.id?.let {
                                        lastPosition?.let { lastPosition ->
                                            howYoRepository.createDay(
                                                lastPosition.plus(position), it
                                            )
                                        }
                                    }

                                if (result is Result.Success) {
                                    dayResults.add(true)
                                } else {
                                    dayResults.add(false)
                                }
                            }
                        }
                        differenceInDayCount < 0 -> {
                            val days = dayList?.sortedByDescending { it.position }

                            for (position in 0 until differenceInDayCount.absoluteValue) {
                                days?.get(position)?.let { day ->
                                    deleteDay(day)
                                }?.let { deleteResult ->
                                    dayResults.add(deleteResult)
                                }

                                days?.get(position)?.let { day ->
                                    scheduleList?.filter { it.dayId == day.id }
                                        ?.forEach { schedule ->
                                            scheduleResults.add(deleteSchedule(schedule))
                                        }
                                }
                            }
                        }
                    }
                }

                when {
                    !dayResults.contains(false) && !scheduleResults.contains(false) -> {
                        _isAllDataReady.value = true
                        _status.value = LoadApiStatus.DONE
                    }
                }
            }
        }
    }

    private suspend fun createDays(): Boolean {
        val planId = planId.value
        val days =
            (startDateFromUser.value?.let {
                endDateFromUser.value?.minus(it)?.div((60 * 60 * 24 * 1000))
            })?.toInt() ?: 0

        val dayResults = mutableListOf<Boolean>()

        for (position in 0..days) {
            val result = planId?.let { howYoRepository.createDay(position, it) }

            if (result is Result.Success) {
                dayResults.add(true)
            } else {
                dayResults.add(false)
            }
        }

        return !dayResults.contains(false)
    }

    private suspend fun deleteDay(day: Day): Boolean =
        when (val result = howYoRepository.deleteDay(day)) {
            is Result.Success -> result.data
            else -> false
        }

    private suspend fun deleteSchedule(schedule: Schedule): Boolean =
        when (val result = howYoRepository.deleteSchedule(schedule)) {
            is Result.Success -> result.data
            else -> false
        }


    private suspend fun createDefaultCheckList(): Boolean {
        val planId = planId.value
        val subTypeList = HowYoApplication.instance.resources.getStringArray(R.array.check_list)
        val checkListResults = mutableListOf<Boolean>()
        val itemList = mutableListOf<CheckShoppingList>()

        subTypeList.forEach { subType ->
            when (subType) {
                getString(R.string.necessary) -> {
                    CheckItemType.NECESSARY.list.forEach { item ->
                        val newItem = CheckShoppingList(
                            planId = planId,
                            mainType = getString(R.string.check),
                            subType = subType,
                            item = item
                        )

                        itemList.add(newItem)
                    }
                }
                getString(R.string.clothe) -> {
                    CheckItemType.CLOTHE.list.forEach { item ->
                        val newItem = CheckShoppingList(
                            planId = planId,
                            mainType = getString(R.string.check),
                            subType = subType,
                            item = item
                        )

                        itemList.add(newItem)
                    }
                }
                getString(R.string.wash) -> {
                    CheckItemType.WASH.list.forEach { item ->
                        val newItem = CheckShoppingList(
                            planId = planId,
                            mainType = getString(R.string.check),
                            subType = subType,
                            item = item
                        )

                        itemList.add(newItem)
                    }
                }
                getString(R.string.electronic) -> {
                    CheckItemType.ELECTRONIC.list.forEach { item ->
                        val newItem = CheckShoppingList(
                            planId = planId,
                            mainType = getString(R.string.check),
                            subType = subType,
                            item = item
                        )

                        itemList.add(newItem)
                    }
                }
                getString(R.string.health) -> {
                    CheckItemType.HEALTH.list.forEach { item ->
                        val newItem = CheckShoppingList(
                            planId = planId,
                            mainType = getString(R.string.check),
                            subType = subType,
                            item = item
                        )

                        itemList.add(newItem)
                    }
                }
                getString(R.string.other) -> {
                    CheckItemType.OTHER.list.forEach { item ->
                        val newItem = CheckShoppingList(
                            planId = planId,
                            mainType = getString(R.string.check),
                            subType = subType,
                            item = item
                        )

                        itemList.add(newItem)
                    }
                }
            }
        }

        checkListResults.add(createDefaultCheckListWithBatch(itemList))

        return !checkListResults.contains(false)
    }

    private suspend fun createDefaultCheckListWithBatch(list: List<CheckShoppingList>): Boolean =
        when (val result = howYoRepository.createCheckShopListWithBatch(list)) {
            is Result.Success -> result.data
            else -> false
        }

    private fun fetchDaysResult() {
        coroutineScope.launch {
            val result = howYoRepository.getDays(plan.value?.id ?: "")
            _days.value = when (result) {
                is Result.Success -> result.data
                else -> null
            }
        }
    }

    private fun fetchSchedulesResult() {
        coroutineScope.launch {
            val result = howYoRepository.getSchedules(plan.value?.id ?: "")
            _schedules.value = when (result) {
                is Result.Success -> result.data
                else -> null
            }
        }
    }

    fun leave() {
        _leave.value = true
    }

    fun nothing() {}

    fun onLeaveCompleted() {
        _leave.value = null
    }

    fun selectDate() {
        _selectDate.value = true
    }

    fun onSelectedDate() {
        plan.value?.apply {
            startDate = startDateFromUser.value
            endDate = endDateFromUser.value
        }

        _selectDate.value = null
    }

    fun takePhoto() {
        _takePhoto.value = true
    }

    fun onTookPhoto() {
        _takePhoto.value = null
    }

    fun selectPhoto() {
        _selectPhoto.value = true
    }

    fun onSelectedPhoto() {
        _selectPhoto.value = null
    }

    fun setCoverBitmap(photoUri: Uri?) {
        val newPlanPhoto = PhotoData(
            photoUri,
            null,
            planPhotoData.value?.fileName,
            true
        )

        _planPhoto.value = newPlanPhoto
    }

    fun resetCoverPhoto() {
        val newPlanPhoto = PhotoData(
            Uri.parse(getString(R.string.default_cover)),
            null,
            planPhotoData.value?.fileName,
            true
        )

        _planPhoto.value = newPlanPhoto
    }

    companion object {
        const val INVALID_FORMAT_TITLE_EMPTY = 0x11
    }
}
