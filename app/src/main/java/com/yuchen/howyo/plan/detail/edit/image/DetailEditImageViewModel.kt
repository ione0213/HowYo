package com.yuchen.howyo.plan.detail.edit.image

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.SchedulePhoto
import com.yuchen.howyo.data.SchedulePhotos
import com.yuchen.howyo.data.source.HowYoRepository
import java.util.*
import kotlinx.coroutines.*

class DetailEditImageViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentSchedulePhoto: SchedulePhoto?,
    private val argumentSchedulePhotos: SchedulePhotos?
) : ViewModel() {

    // Image data from arguments
    private val _schedulePhoto = MutableLiveData<SchedulePhoto>().apply {
        value = argumentSchedulePhoto
    }

    val schedulePhoto: LiveData<SchedulePhoto>
        get() = _schedulePhoto

    // Images data from arguments
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
