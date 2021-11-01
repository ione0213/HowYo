package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.MapView
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.plan.detail.edit.DetailEditViewModel
import com.yuchen.howyo.plan.detail.view.DetailViewModel
import com.yuchen.howyo.plan.detail.view.map.MapViewModel

class EditDetailViewModelFactory(
    private val howYoRepository: HowYoRepository,
    private val schedule: Schedule? = null,
    private val planId: String?,
    private val dayId: String?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DetailEditViewModel::class.java) ->
                    DetailEditViewModel(howYoRepository, schedule, planId, dayId)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}