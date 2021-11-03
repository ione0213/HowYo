package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.SchedulePhoto
import com.yuchen.howyo.data.SchedulePhotos
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.plan.AccessPlanType
import com.yuchen.howyo.plan.PlanViewModel
import com.yuchen.howyo.plan.companion.locate.LocateViewModel
import com.yuchen.howyo.plan.detail.edit.image.DetailEditImageViewModel
import com.yuchen.howyo.plan.groupmessage.GroupMessageViewModel
import com.yuchen.howyo.plan.payment.PaymentViewModel

class DetailEditImageViewModelFactory(
    private val howYoRepository: HowYoRepository,
    private val schedulePhoto: SchedulePhoto,
    private val schedulePhotos: SchedulePhotos
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DetailEditImageViewModel::class.java) ->
                    DetailEditImageViewModel(howYoRepository, schedulePhoto, schedulePhotos)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}