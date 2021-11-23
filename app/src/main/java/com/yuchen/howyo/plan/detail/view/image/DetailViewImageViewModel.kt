package com.yuchen.howyo.plan.detail.view.image

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.source.HowYoRepository

class DetailViewImageViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentImageUrl: String?
) : ViewModel() {

    // Image data from arguments
    private val _imageUrl = MutableLiveData<String>().apply {
        value = argumentImageUrl
    }

    val imageUrl: LiveData<String>
        get() = _imageUrl

    // Handle leave edit image
    private val _leaveViewImage = MutableLiveData<Boolean>()

    val leaveViewImage: LiveData<Boolean>
        get() = _leaveViewImage

    fun leaveViewImage() {
        _leaveViewImage.value = true
    }
}
