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
import com.yuchen.howyo.signin.UserManager
import com.yuchen.howyo.util.Util.getString
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.*

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

    var plan = MutableLiveData<Plan>().apply {
        value = argumentPlan
    }

    val day = MutableLiveData<Day>().apply {
        value = argumentDay
    }

    private val _photoDataList = MutableLiveData<MutableList<PhotoData>>().apply {
        val photoData = mutableListOf<PhotoData>()
        argumentSchedule?.let {
            it.photoUrlList?.forEachIndexed { index, url ->
                photoData.add(PhotoData(url = url, fileName = it.photoFileNameList?.get(index)))
            }
        }
        value = photoData
    }

    val photoDataDataList: LiveData<MutableList<PhotoData>>
        get() = _photoDataList

    val notification = MutableLiveData<Boolean>()

    var title = MutableLiveData<String>()

    val address = MutableLiveData<String>()

    val startTime = MutableLiveData<Long>()

    val endTime = MutableLiveData<Long>()

    val remark = MutableLiveData<String>()

    val budget = MutableLiveData<String>()

    val refUrl = MutableLiveData<String>()

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
    private val _navigateToEditImage = MutableLiveData<PhotoData>()

    val navigateToEditImage: LiveData<PhotoData>
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

        setData()
    }

    private fun setData() {
        when {
            schedule.value != null -> {
                schedule.value.apply {
                    notification.value = this?.notification ?: false
                    title.value = this?.title ?: ""
                    address.value = this?.address ?: ""
                    startTime.value = this?.startTime ?: 0L
                    endTime.value = this?.endTime ?: 0L
                    remark.value = this?.remark ?: ""
                    budget.value = this?.budget?.toString()
                    refUrl.value = this?.refUrl ?: ""

                    val spinnerList =
                        HowYoApplication.instance.resources.getStringArray(R.array.schedule_type_list)
                    selectedScheduleTypePosition.value = spinnerList.indexOf(this?.scheduleType)
                }
            }
        }
    }

    fun setBitmap(uri: Uri) {
        val photoData = photoDataDataList.value?.toMutableList()
        photoData?.add(PhotoData(uri = uri))
        _photoDataList.value = photoData
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
            notification = this@DetailEditViewModel.notification.value
            scheduleType =
                HowYoApplication
                    .instance
                    .resources
                    .getStringArray(R.array.schedule_type_list)[
                        selectedScheduleTypePosition.value
                            ?: 0
                ]
            title = this@DetailEditViewModel.title.value
            when {
                location?.first != 0.0 && location?.second != 0.0 -> {
                    latitude = location?.first
                    longitude = location?.second
                }
                else -> {
                }
            }
            startTime = this@DetailEditViewModel.startTime.value
            endTime = this@DetailEditViewModel.endTime.value
            when {
                this@DetailEditViewModel.budget.value?.isNullOrEmpty() == false -> {
                    budget = this@DetailEditViewModel.budget.value?.toInt()
                }
            }
            refUrl = this@DetailEditViewModel.refUrl.value
            address = this@DetailEditViewModel.address.value
            remark = this@DetailEditViewModel.remark.value
        }

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {
                photoDataDataList.value?.forEach {

                    when (it.uri) {
                        null -> {
                            when (it.isDeleted) {
                                true -> {
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
                            when (it.isDeleted) {
                                false -> {
                                    val uri = it.uri
                                    val formatter =
                                        SimpleDateFormat("yyyy_mm_dd_HH_mm_ss", Locale.getDefault())

                                    val fileName =
                                        "${UserManager.currentUserEmail}_${formatter.format(Date())}"

                                    fileNameList.add(fileName)

                                    when (
                                        val result =
                                            uri?.let { imgUri ->
                                                howYoRepository.uploadPhoto(imgUri, fileName)
                                            }
                                    ) {
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
        return when (list.isEmpty()) {
            true -> {
                Pair(0.0, 0.0)
            }
            else -> {
                Pair(list.first().latitude, list.first().longitude)
            }
        }
    }

    fun leaveEditDetail() {
        _leaveEditDetail.value = true
    }

    fun onLeaveEditDetail() {
        _leaveEditDetail.value = null
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

    fun onBackToPreviousPage() {
        _status.value = LoadApiStatus.DONE
        _scheduleResult.value = null
    }

    fun navigateToEditImage(photoData: PhotoData) {
        _navigateToEditImage.value = photoData
    }

    fun onEditImageNavigated() {
        _navigateToEditImage.value = null
    }
}
