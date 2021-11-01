package com.yuchen.howyo.plan.cover

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.ext.toDate
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.util.Logger
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class PlanCoverViewModel(private val howYoRepository: HowYoRepository) : ViewModel() {

    private val _plan = MutableLiveData<Plan>().apply {

        val today = Calendar.getInstance()
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_YEAR, 1)

        value = Plan(
            authorId = "userIdFromSharePreference",
            coverFileName = "",
            startDate = today.timeInMillis,
            endDate = tomorrow.timeInMillis
        )
    }

    val plan: LiveData<Plan>
        get() = _plan

    private val _planId = MutableLiveData<String>()

    val planId: LiveData<String>
        get() = _planId

    val startDateFromUser = MutableLiveData<Long>()

    val endDateFromUser = MutableLiveData<Long>()

    //Cover photo bitmap
    private val _photoUri = MutableLiveData<Uri>()

    private val photoUri: LiveData<Uri>
        get() = _photoUri

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

    // Handle the main data of the check and shopping list are ready or not
    private val _isAllDataReady = MutableLiveData<Boolean>()

    val isAllDataReady: LiveData<Boolean>
        get() = _isAllDataReady

    // Handle leave plan cover
    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    // Handle open date range picker
    private val _selectDate = MutableLiveData<Boolean>()

    val selectDate: LiveData<Boolean>
        get() = _selectDate

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

    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {

        //set the default value for duration of the plan
        val calendar = Calendar.getInstance()
        startDateFromUser.value = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        endDateFromUser.value = calendar.timeInMillis

        _photoUri.value = Uri.parse("android.resource://com.yuchen.howyo/drawable/sample_cover")
    }

    fun uploadCoverImg() {

        val uri = photoUri.value!!
        val formatter = SimpleDateFormat("yyyy_mm_dd_HH_mm_ss", Locale.getDefault())
        val fileName = "userIdFromSharePreference_${formatter.format(Date())}"

        Logger.i("Plan old:${plan.value}")
        _plan.value?.coverFileName = fileName
        Logger.i("Plan new:${plan.value}")


        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = howYoRepository.uploadPhoto(uri, fileName)) {
                is Result.Success -> {
                    _plan.value?.coverPhotoUrl = result.data
                    _isCoverPhotoReady.value = true
                }
                is Result.Fail -> {
                    _isCoverPhotoReady.value = false
                }
                is Result.Error -> {
                    _isCoverPhotoReady.value = false
                }
                else -> {
                    _isCoverPhotoReady.value = false
                }
            }
        }

        _photoUri.value = null
    }

    fun createPlan() {

        val plan = plan.value!!

        coroutineScope.launch {

            val result = howYoRepository.createPlan(plan)

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

    fun setRelatedCollection() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {

                _isDaysReady.postValue(createDays()!!)
                _isChkListReady.postValue(createMainCheckList()!!)
            }

            when {
                isDaysReady.value == true && isChkListReady.value == true -> {
                    _isAllDataReady.value = true
                    _status.value = LoadApiStatus.DONE
                }
            }
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

    private suspend fun createMainCheckList(): Boolean {

        val planId = planId.value
        val mainTypeList = HowYoApplication.instance.resources.getStringArray(R.array.main_list)
        val subTypeList = HowYoApplication.instance.resources.getStringArray(R.array.check_list)

        val mainCheckListResults = mutableListOf<Boolean>()

        mainTypeList.forEach { mainType ->
            when (mainType) {
                HowYoApplication.instance.getString(R.string.check) -> {
                    subTypeList.forEach { subType ->
                        val result = howYoRepository.createMainCheckList(
                            planId!!,
                            mainType,
                            subType
                        )

                        if (result is Result.Success) {
                            mainCheckListResults.add(result.data)
                        } else {
                            mainCheckListResults.add(false)
                        }
                    }
                }
                HowYoApplication.instance.getString(R.string.shopping) -> {
                    val result = howYoRepository.createMainCheckList(
                        planId!!,
                        mainType,
                        ""
                    )

                    if (result is Result.Success) {
                        mainCheckListResults.add(result.data)
                    } else {
                        mainCheckListResults.add(false)
                    }
                }
            }
        }
        return !mainCheckListResults.contains(false)
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
        _plan.value?.apply {
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
        _photoUri.value = photoUri!!
    }
}