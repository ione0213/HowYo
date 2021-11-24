package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.data.PhotoData
import com.yuchen.howyo.data.Photos
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.plan.detail.edit.image.DetailEditImageViewModel

class DetailEditImageViewModelFactory(
    private val howYoRepository: HowYoRepository,
    private val photoData: PhotoData,
    private val photos: Photos
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DetailEditImageViewModel::class.java) ->
                    DetailEditImageViewModel(howYoRepository, photoData, photos)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
