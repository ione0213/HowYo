package com.yuchen.howyo.copyplan

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.data.*
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.plan.CheckItemType
import com.yuchen.howyo.signin.UserManager
import com.yuchen.howyo.util.Util.getString
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class CopyPlanViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPlan: Plan?
) : ViewModel() {

    private val _isNewPlan = MutableLiveData<Boolean>()

    val isNewPlan: LiveData<Boolean>
        get() = _isNewPlan

    val _plan = MutableLiveData<Plan>().apply {
        when (argumentPlan) {
            null -> {
                val today = Calendar.getInstance()
                val tomorrow = Calendar.getInstance()
                tomorrow.add(Calendar.DAY_OF_YEAR, 1)

                value = Plan(
                    coverFileName = "",
                    startDate = today.timeInMillis,
                    endDate = tomorrow.timeInMillis
                )
            }
            else -> {
                value = argumentPlan
            }
        }
    }

    val plan: LiveData<Plan>
        get() = _plan

    private val _planId = MutableLiveData<String>()

    val planId: LiveData<String>
        get() = _planId

    private val _planPhoto = MutableLiveData<SchedulePhoto>()

    val planPhoto: LiveData<SchedulePhoto>
        get() = _planPhoto

    val startDateFromUser = MutableLiveData<Long>()

    private val previousStartDate = MutableLiveData<Long>()

    val endDateFromUser = MutableLiveData<Long>()

    //Days list for copying reference
    private val _days = MutableLiveData<List<Day>>()

    val days: LiveData<List<Day>>
        get() = _days

    //New days list
    private val _newDays = MutableLiveData<List<Day>>()

    val newDays: LiveData<List<Day>>
        get() = _newDays

    //Schedules list for copying
    private val _schedules = MutableLiveData<List<Schedule>>()

    val schedules: LiveData<List<Schedule>>
        get() = _schedules

    // Handle the submit plan
    private val _isSavePlan = MutableLiveData<Boolean>()

    val isSavePlan: LiveData<Boolean>
        get() = _isSavePlan

    // Handle the plan data is ready or not
    private val _isCoverPhotoReady = MutableLiveData<Boolean>()

    val isCoverPhotoReady: LiveData<Boolean>
        get() = _isCoverPhotoReady

    // Handle the days data is ready or not
    private val _isDaysReady = MutableLiveData<Boolean>()

    private val isDaysReady: LiveData<Boolean>
        get() = _isDaysReady

    // Handle the main data of the check and shopping list are ready or not
    private val _isChkListReady = MutableLiveData<Boolean>()

    private val isChkListReady: LiveData<Boolean>
        get() = _isChkListReady

    // Handle the related data is ready or not
    private val _isRelatedDataReady = MutableLiveData<Boolean>()

    val isRelatedDataReady: LiveData<Boolean>
        get() = _isRelatedDataReady

    // Handle the copying is ready or not
    private val _isCopyFinished = MutableLiveData<Boolean>()

    val isCopyFinished: LiveData<Boolean>
        get() = _isCopyFinished

    // Handle leave plan cover
    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    // Handle add the cover photo by camera
    private val _takePhoto = MutableLiveData<Boolean>()

    val takePhoto: LiveData<Boolean>
        get() = _takePhoto

    // Handle add the cover photo by selecting
    private val _selectPhoto = MutableLiveData<Boolean>()

    val selectPhoto: LiveData<Boolean>
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
            getFromDaysResult()
            getSchedulesResult()
        }
    }

    private fun setInitData() {

        //set the default value for duration of the plan
        val calendar = Calendar.getInstance()

        startDateFromUser.value = plan.value?.startDate ?: calendar.timeInMillis
        previousStartDate.value = startDateFromUser.value!!.toLong()

        calendar.add(Calendar.DAY_OF_YEAR, 1)

        endDateFromUser.value = plan.value?.endDate ?: calendar.timeInMillis

        _planPhoto.value = SchedulePhoto(
            Uri.parse(getString(R.string.default_cover))
        )

        _isNewPlan.value = when (argumentPlan) {
            null -> {
                true
            }
            else -> {
                false
            }
        }
    }

    fun prepareSubmitPlan() {

        when {
            plan.value?.title.isNullOrEmpty() -> _invalidPlan.value = INVALID_FORMAT_TITLE_EMPTY
            else -> {
                savePlan()
            }
        }
    }

    private fun savePlan() {
        _isSavePlan.value = true
    }

    fun onSavedPlan() {
        _isSavePlan.value = null
    }

    fun handlePlanCover() {

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            _isCoverPhotoReady.value = uploadCoverImg()
        }
    }

    private suspend fun uploadCoverImg(): Boolean {

        val uri = planPhoto.value?.uri
        val formatter = SimpleDateFormat("yyyy_mm_dd_HH_mm_ss", Locale.getDefault())
        val fileName = "${UserManager.currentUserEmail}_${formatter.format(Date())}"
        var uploadResult = false

        _plan.value?.coverFileName = fileName

        when (val result = uri?.let { howYoRepository.uploadPhoto(it, fileName) }) {
            is Result.Success -> {
                _plan.value?.coverPhotoUrl = result.data
                uploadResult = true
            }
            else -> {
                uploadResult = true
            }
        }
        return uploadResult
    }

    fun createPlan() {

        val plan = plan.value
        val newPlan = plan?.copy(
            id = "",
            authorId = UserManager.userId,
            companionList = listOf(),
            likeList = listOf(),
            planCollectedList = listOf(),
            privacy = getString(R.string.plan_private)
        )

        coroutineScope.launch {

            val result = newPlan?.let { howYoRepository.createPlan(it) }

            _planId.value = when (result) {
                is Result.Success -> {
                    result.data
                }
                is Result.Fail -> {
                    null
                }
                is Result.Error -> {
                    null
                }
                else -> {
                    null
                }
            }
            _isCoverPhotoReady.value = null
        }
    }

    private suspend fun createDays(): Boolean {

        val planId = planId.value
        val days =
            (endDateFromUser.value?.minus(startDateFromUser.value!!)
                ?.div((60 * 60 * 24 * 1000)))?.toInt()
        val dayResults = mutableListOf<Boolean>()

        for (position in 0..days!!) {

            val result = howYoRepository.createDay(position, planId!!)

            if (result is Result.Success) {
                dayResults.add(result.data)
            } else {
                dayResults.add(false)
            }
        }
        return !dayResults.contains(false)
    }

    fun createRelatedCollection() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {

                _isDaysReady.postValue(createDays()!!)
                _isChkListReady.postValue(createDefaultCheckList()!!)
            }

            when {
                isDaysReady.value == true && isChkListReady.value == true -> {
                    _isRelatedDataReady.value = true
                }
            }
        }
    }

    private suspend fun createDefaultCheckList(): Boolean {

        val planId = planId.value
        val subTypeList = HowYoApplication.instance.resources.getStringArray(R.array.check_list)
        val checkListResults = mutableListOf<Boolean>()

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

                        val result = howYoRepository.createCheckShopList(newItem)

                        if (result is Result.Success) {
                            checkListResults.add(result.data)
                        } else {
                            checkListResults.add(false)
                        }
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

                        val result = howYoRepository.createCheckShopList(newItem)

                        if (result is Result.Success) {
                            checkListResults.add(result.data)
                        } else {
                            checkListResults.add(false)
                        }
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

                        val result = howYoRepository.createCheckShopList(newItem)

                        if (result is Result.Success) {
                            checkListResults.add(result.data)
                        } else {
                            checkListResults.add(false)
                        }
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

                        val result = howYoRepository.createCheckShopList(newItem)

                        if (result is Result.Success) {
                            checkListResults.add(result.data)
                        } else {
                            checkListResults.add(false)
                        }
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

                        val result = howYoRepository.createCheckShopList(newItem)

                        if (result is Result.Success) {
                            checkListResults.add(result.data)
                        } else {
                            checkListResults.add(false)
                        }
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

                        val result = howYoRepository.createCheckShopList(newItem)

                        if (result is Result.Success) {
                            checkListResults.add(result.data)
                        } else {
                            checkListResults.add(false)
                        }
                    }
                }
            }
        }

        return !checkListResults.contains(false)
    }

    fun copySchedules() {

        val schedulesResult = mutableListOf<Boolean>()



        coroutineScope.launch {

            withContext(Dispatchers.IO) {
                days.value?.forEachIndexed { index, day ->
                    schedules.value?.filter { it.dayId == day.id }?.forEach { schedule ->
                        val newSchedule = schedule.copy(
                            planId = planId.value,
                            dayId = newDays.value?.get(index)?.id,
                            id = "",
                            photoUrlList = listOf(),
                            photoFileNameList = listOf()
                        )

                        schedulesResult.add(createSchedule(newSchedule))
                    }
                }
            }

            when {
                !schedulesResult.contains(false) -> _isCopyFinished.value = true
            }
        }
    }

    private suspend fun createSchedule(schedule: Schedule): Boolean =
        when (val result = howYoRepository.createSchedule(schedule)) {
            is Result.Success -> result.data
            else -> false
        }

    private fun getFromDaysResult() {

        coroutineScope.launch {

            val result = howYoRepository.getDays(plan.value?.id!!)
            _days.value = when (result) {
                is Result.Success -> result.data
                else -> null
            }
        }
    }

    fun getNewDaysResult() {

        coroutineScope.launch {

            val result = planId.value?.let { howYoRepository.getDays(it) }
            _newDays.value = when (result) {
                is Result.Success -> result.data
                else -> null
            }
        }
    }

    private fun getSchedulesResult() {

        coroutineScope.launch {

            val result = howYoRepository.getSchedules(plan.value?.id!!)
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

        val newPlanPhoto = SchedulePhoto(
            photoUri,
            null,
            planPhoto.value?.fileName,
            true
        )

        _planPhoto.value = newPlanPhoto
    }

    fun resetCoverImg() {
        val newPlanPhoto = SchedulePhoto(
            Uri.parse(getString(R.string.default_cover))
        )

        _planPhoto.value = newPlanPhoto
    }

    companion object {

        const val INVALID_FORMAT_TITLE_EMPTY = 0x11
    }
}