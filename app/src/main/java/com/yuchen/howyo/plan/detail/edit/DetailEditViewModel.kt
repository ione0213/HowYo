package com.yuchen.howyo.plan.detail.edit

import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.data.*
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.util.Logger
import com.yuchen.howyo.util.Util.getString
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class DetailEditViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentSchedule: Schedule?,
    private val argumentPlan: Plan?,
    private val argumentDay: Day?
) : ViewModel() {

    // Detail data from arguments
    private val _schedule = MutableLiveData<Schedule>().apply {
        value = argumentSchedule
    }

    val schedule: LiveData<Schedule>
        get() = _schedule

    val plan = MutableLiveData<Plan>().apply {
        value = argumentPlan
    }

    val day = MutableLiveData<Day>().apply {
        value = argumentDay
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

    var title = MutableLiveData<String>()

    val address = MutableLiveData<String>()

    val startTime = MutableLiveData<Long>()

    val endTime = MutableLiveData<Long>()

    val remark = MutableLiveData<String>()

    val budget = MutableLiveData<String>()

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

    // Handle setting time
    private val _setTime = MutableLiveData<String>()

    val setTime: LiveData<String>
        get() = _setTime

    private val _scheduleResult = MutableLiveData<Boolean>()

    val scheduleResult: LiveData<Boolean>
        get() = _scheduleResult

    // Handle navigation to edit single image
    private val _navigateToEditImage = MutableLiveData<SchedulePhoto>()

    val navigateToEditImage: LiveData<SchedulePhoto>
        get() = _navigateToEditImage

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
        Logger.i("planId:${plan.value}")
        Logger.i("dayId:${day.value}")
        setData()
    }

    private fun setData() {
        when {
            schedule.value != null -> {
                schedule.value.apply {
                    type.value = this?.scheduleType ?: ""
                    notification.value = this?.notification ?: false
                    title.value = this?.title ?: ""
                    address.value = this?.address ?: ""
                    startTime.value = this?.startTime ?: 0L
                    endTime.value = this?.endTime ?: 0L
                    remark.value = this?.remark ?: ""
                    budget.value = this?.budget.toString()
                    refUrl.value = this?.refUrl ?: ""
                }
            }
        }
    }

    fun setBitmap(uri: Uri) {
        val photoData = photoDataList.value?.toMutableList()
        Logger.i("_photoDataList:${photoDataList.value}")
        photoData?.add(SchedulePhoto(uri = uri))
        _photoDataList.value = photoData
        Logger.i("_photoDataList:${photoDataList.value}")
    }

    fun saveSchedule() {

        val imageUrlList = mutableListOf<String>()
        val fileNameList = mutableListOf<String>()
        val location = when (address.value?.isNotEmpty()) {
            true -> {
                getLatitudeAndLongitude()
            }
            else -> null
        }

        val newSchedule = schedule.value ?: Schedule(
            planId = plan.value?.id ?: "",
            dayId = day.value?.id ?: "",
        )

        newSchedule.apply {
            scheduleType =
                HowYoApplication
                    .instance
                    .resources
                    .getStringArray(R.array.schedule_type_list)[selectedScheduleTypePosition.value ?: 0]
            title = this@DetailEditViewModel.title.value
            latitude = location?.first
            longitude = location?.second
            startTime = this@DetailEditViewModel.startTime.value
            endTime = this@DetailEditViewModel.endTime.value
            budget = this@DetailEditViewModel.budget.value?.toInt()
            refUrl = this@DetailEditViewModel.refUrl.value
            address = this@DetailEditViewModel.address.value
            remark = this@DetailEditViewModel.remark.value
        }

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {
                photoDataList.value?.forEach {

                    when (it.uri) {
                        null -> {
                            Logger.i("No uri:$it")
                            when (it.isDeleted) {
                                true -> {
                                    Logger.i("delete image: ${it.fileName}")
                                    it.fileName?.let { fileName ->
                                        howYoRepository.deletePhoto(fileName)
                                    }
                                }
                                false -> {
                                    fileNameList.add(it.fileName ?: "")
                                    imageUrlList.add(it.url ?: "")
                                }
                            }
                        }
                        else -> {
                            Logger.i("Has uri:$it")
                            when (it.isDeleted) {
                                false -> {
                                    val uri = it.uri
                                    val formatter =
                                        SimpleDateFormat("yyyy_mm_dd_HH_mm_ss", Locale.getDefault())
                                    val fileName =
                                        "userIdFromSharePreference_${formatter.format(Date())}"

                                    fileNameList.add(fileName)
                                    Logger.i("upload file :$fileName")

                                    when (val result =
                                        uri.let { imgUri ->
                                            howYoRepository.uploadPhoto(imgUri, fileName)
                                        }) {
                                        is Result.Success -> {
                                            imageUrlList.add(result.data)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            newSchedule.apply {
                Logger.i("imageUrlList:$imageUrlList")
                Logger.i("fileNameList:$fileNameList")
                photoUrlList = imageUrlList
                photoFileNameList = fileNameList
            }

            withContext(Dispatchers.IO) {
                _scheduleResult.postValue(
                    when {
                        newSchedule.id.isNullOrEmpty() -> {
                            when (val result = howYoRepository.createSchedule(newSchedule)) {
                                is Result.Success -> {
                                    result.data
                                }
                                else -> false
                            }
                        }
                        else -> {
                            when (val result = howYoRepository.updateSchedule(newSchedule)) {
                                is Result.Success -> {
                                    result.data
                                }
                                else -> false
                            }
                        }
                    }
                )
            }
        }
    }

    private fun getLatitudeAndLongitude(): Pair<Double, Double> {

        val geocoder = Geocoder(HowYoApplication.instance)

        val list: List<Address> =
            geocoder.getFromLocationName(address.value ?: "", 1)

        return Pair(list.first().latitude, list.first().longitude)
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

    fun setTime(type: String) {
        _setTime.value = type
    }

    fun onSetTime() {
        _setTime.value = null
    }

    fun setTimeValue(type: String, data: Long) {
        when (type) {
            getString(R.string.detail_edit_schedule_start_time) -> {
                startTime.value = data
            }
            else -> {
                endTime.value = data
            }
        }
    }

    fun onBackToPlanPortal() {
        _status.value = LoadApiStatus.DONE
        _scheduleResult.value = null
    }

    fun navigateToEditImage(schedulePhoto: SchedulePhoto) {
        _navigateToEditImage.value = schedulePhoto
    }

    fun onEditImageNavigated() {
        _navigateToEditImage.value = null
    }
}