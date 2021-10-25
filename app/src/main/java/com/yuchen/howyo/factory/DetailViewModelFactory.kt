package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.MapView
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.plan.detail.edit.DetailEditViewModel
import com.yuchen.howyo.plan.detail.view.DetailViewModel
import com.yuchen.howyo.plan.detail.view.map.MapViewModel

class DetailViewModelFactory(
    private val howYoRepository: HowYoRepository,
    private val schedule: Schedule?,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DetailViewModel::class.java) ->
                    DetailViewModel(howYoRepository, schedule)
                isAssignableFrom(DetailEditViewModel::class.java) ->
                    DetailEditViewModel(howYoRepository, schedule)
                isAssignableFrom(MapViewModel::class.java) ->
                    MapViewModel(howYoRepository, schedule)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}