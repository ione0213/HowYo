package com.yuchen.howyo.plan.detail.edit.image

import android.location.Address
import android.location.Geocoder
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
import com.yuchen.howyo.data.SchedulePhotos
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.util.Logger
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class DetailEditImageViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentSchedulePhoto: SchedulePhoto?,
    private val argumentSchedulePhotos: SchedulePhotos?
) : ViewModel() {

    //Image data from arguments
    private val _schedulePhoto = MutableLiveData<SchedulePhoto>().apply {
        value = argumentSchedulePhoto
    }

    val schedulePhoto: LiveData<SchedulePhoto>
        get() = _schedulePhoto

    //Images data from arguments
    private val _schedulePhotos = MutableLiveData<SchedulePhotos>().apply {
        value = argumentSchedulePhotos
    }

    val schedulePhotos: LiveData<SchedulePhotos>
        get() = _schedulePhotos

    // Handle leave edit image
    private val _leaveEditImage = MutableLiveData<Boolean>()

    val leaveEditImage: LiveData<Boolean>
        get() = _leaveEditImage

    fun deleteImage() {
        _schedulePhotos.value?.forEach {
            when {
                it === schedulePhoto.value -> {
                    it.isDeleted = true
                    leaveEditImage()
                }
            }
        }
    }

    fun leaveEditImage() {
        _leaveEditImage.value = true
    }
}