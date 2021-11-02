package com.yuchen.howyo.plan.detail.edit

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.SchedulePhoto
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.util.Logger
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class DetailEditViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentSchedule: Schedule?,
    private val argumentPlanId: String?,
    private val argumentDayId: String?
) : ViewModel() {

    // Detail data from arguments
    private val _schedule = MutableLiveData<Schedule>().apply {
        value = argumentSchedule ?: Schedule(dayId = argumentDayId)
    }

    val schedule: LiveData<Schedule>
        get() = _schedule

    private val planId = MutableLiveData<String>().apply {
        value = argumentPlanId ?: ""
    }

    private val dayId = MutableLiveData<String>().apply {
        value = argumentDayId ?: ""
    }

    private val _photoDataList = MutableLiveData<MutableList<SchedulePhoto>>().apply {
        val photoData = mutableListOf<SchedulePhoto>()
        argumentSchedule?.let {
            it.photoUrlList?.forEachIndexed { index, url ->
                photoData.add(SchedulePhoto(url = url, fileName = it.photoFileNameList?.get(index)))
            }
        }
        value = photoData
    }

    val photoDataList: LiveData<MutableList<SchedulePhoto>>
        get() = _photoDataList

    val type = MutableLiveData<String>()

    val notification = MutableLiveData<Boolean>()

    val title = MutableLiveData<String>()

    val address = MutableLiveData<String>()

    val startTime = MutableLiveData<Long>()

    val endTime = MutableLiveData<Long>()

    val remark = MutableLiveData<String>()

    val budge = MutableLiveData<String>()

    val refUrl = MutableLiveData<String>()

    val photoList = MutableLiveData<List<String>>()

    val selectedScheduleTypePosition = MutableLiveData<Int>()

    // Handle leave edit detail
    private val _leaveEditDetail = MutableLiveData<Boolean>()

    val leaveEditDetail: LiveData<Boolean>
        get() = _leaveEditDetail

    // Handle add the photo by selecting
    private val _selectPhoto = MutableLiveData<Boolean>()

    val selectPhoto: LiveData<Boolean>
        get() = _selectPhoto

    private val _scheduleResult = MutableLiveData<Boolean>()

    val scheduleResult: LiveData<Boolean>
        get() = _scheduleResult

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
        Logger.i("schedule:${schedule.value}")
        Logger.i("planId:${planId.value}")
        Logger.i("dayId:${dayId.value}")
    }

    fun setBitmap(uri: Uri) {
        val photoData = photoDataList.value?.toMutableList()
        Logger.i("_photoDataList:${photoDataList.value}")
        photoData?.add(SchedulePhoto(uri = uri))
        _photoDataList.value = photoData
        Logger.i("_photoDataList:${photoDataList.value}")
    }

    fun createSchedule() {

        val imageUrlList = mutableListOf<String>()
        val fileNameList = mutableListOf<String>()
        val schedule = Schedule(
            planId = planId.value,
            dayId = dayId.value,
            scheduleType =
            HowYoApplication
                .instance
                .resources
                .getStringArray(R.array.schedule_type_list)
                    [selectedScheduleTypePosition.value ?: 0],
            title = title.value,
            budget = budge.value?.toInt(),
            refUrl = refUrl.value,
            address = address.value,
            remark = remark.value
        )

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {
                photoDataList.value?.forEach {

                    val uri = it.uri
                    val formatter = SimpleDateFormat("yyyy_mm_dd_HH_mm_ss", Locale.getDefault())
                    val fileName = "userIdFromSharePreference_${formatter.format(Date())}"

                    fileNameList.add(fileName)

                    when (val result =
                        uri?.let { imgUri -> howYoRepository.uploadPhoto(imgUri, fileName) }) {
                        is Result.Success -> {
                            imageUrlList.add(result.data)
                        }
                    }
                }
            }

            schedule.apply {
                photoUrlList = imageUrlList
                photoFileNameList = fileNameList
            }

            withContext(Dispatchers.Main) {
                _scheduleResult.setValue(
                    when (val result = howYoRepository.createSchedule(schedule)) {
                        is Result.Success -> {
                            result.data
                        }
                        else -> false
                    }
                )
            }
        }
    }

    fun leaveEditDetail() {
        _leaveEditDetail.value = true
    }

    fun selectPhoto() {
        _selectPhoto.value = true
    }

    fun onSelectedPhoto() {
        _selectPhoto.value = null
    }

    fun onBackToPlanPortal() {
        _status.value = LoadApiStatus.DONE
        _scheduleResult.value = null
    }
}