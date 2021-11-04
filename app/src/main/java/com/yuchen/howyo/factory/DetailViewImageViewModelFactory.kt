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
import com.yuchen.howyo.plan.detail.view.image.DetailViewImageViewModel
import com.yuchen.howyo.plan.groupmessage.GroupMessageViewModel
import com.yuchen.howyo.plan.payment.PaymentViewModel

class DetailViewImageViewModelFactory(
    private val howYoRepository: HowYoRepository,
    private val imageUrl: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DetailViewImageViewModel::class.java) ->
                    DetailViewImageViewModel(howYoRepository, imageUrl)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}