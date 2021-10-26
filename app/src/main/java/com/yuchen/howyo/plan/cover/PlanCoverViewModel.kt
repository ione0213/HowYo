package com.yuchen.howyo.plan.cover

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class PlanCoverViewModel(private val howYoRepository: HowYoRepository) : ViewModel() {

    private val _plan = MutableLiveData<Plan>().apply {

        val today = Calendar.getInstance()
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_YEAR, 1)

        value = Plan(
            authorId = "userIdFromSharePreference",
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

    val photoUri: LiveData<Uri>
        get() = _photoUri

    // Handle the plan data is ready or not
    private val _isPlanReady = MutableLiveData<Boolean>()

    val isPlanReady: LiveData<Boolean>
        get() = _isPlanReady

    // Handle leave plan cover
    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    // Handle open date range picker
    private val _selectDate = MutableLiveData<Boolean>()

    val selectDate: LiveData<Boolean>
        get() = _selectDate

    // Handle save plan cover
    private val _navToDetail = MutableLiveData<Plan>()

    val navToDetail: LiveData<Plan>
        get() = _navToDetail

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
    }

    fun createPlan() {

        val plan = plan.value!!

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = howYoRepository.createPlan(plan)

            _planId.value = when (result) {
                is Result.Success -> {
                    _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _isPlanReady.value = null
        }
    }

    fun getPlanData() {

        val planId = planId.value

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = howYoRepository.getPlan(planId!!)

            _navToDetail.value = when (result) {
                is Result.Success -> {
                    _status.value = LoadApiStatus.DONE
                    Logger.i("Plan======: ${result.data}")
                    result.data
                }
                is Result.Fail -> {
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }
    }

    fun onNavToDetailCompleted() {
        _navToDetail.value = null
    }

    fun leave() {
        _leave.value = true
    }

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

    fun uploadCoverImg() {

        val uri = photoUri.value!!

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = howYoRepository.uploadPhoto(uri)) {
                is Result.Success -> {
                    _plan.value?.coverPhotoUrl = result.data
                    _isPlanReady.value = true
                    _status.value = LoadApiStatus.DONE
                }
                is Result.Fail -> {
                    _status.value = LoadApiStatus.ERROR
                    _isPlanReady.value = false
                }
                is Result.Error -> {
                    _status.value = LoadApiStatus.ERROR
                    _isPlanReady.value = false
                }
                else -> {
                    _status.value = LoadApiStatus.ERROR
                    _isPlanReady.value = false
                }
            }
        }

        _photoUri.value = null
    }
}