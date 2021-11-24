package com.yuchen.howyo.plan.detail.edit.image

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.PhotoData
import com.yuchen.howyo.data.Photos
import com.yuchen.howyo.data.source.HowYoRepository

class DetailEditImageViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPhotoData: PhotoData?,
    private val argumentPhotos: Photos?
) : ViewModel() {
    // Image data from arguments
    private val _schedulePhoto = MutableLiveData<PhotoData>().apply {
        value = argumentPhotoData
    }

    val schedulePhoto: LiveData<PhotoData>
        get() = _schedulePhoto

    // Images data from arguments
    private val _schedulePhotos = MutableLiveData<Photos>().apply {
        value = argumentPhotos
    }

    private val schedulePhotos: LiveData<Photos>
        get() = _schedulePhotos

    // Handle leave edit image
    private val _leaveEditImage = MutableLiveData<Boolean>()

    val leaveEditImage: LiveData<Boolean>
        get() = _leaveEditImage

    fun deleteImage() {
        schedulePhotos.value?.forEach {
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
