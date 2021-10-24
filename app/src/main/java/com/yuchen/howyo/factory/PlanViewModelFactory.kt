package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.plan.companion.locate.LocateViewModel
import com.yuchen.howyo.plan.detail.edit.DetailEditViewModel
import com.yuchen.howyo.plan.detail.view.DetailViewModel
import com.yuchen.howyo.plan.payment.PaymentViewModel

class PlanViewModelFactory(
    private val howYoRepository: HowYoRepository,
    private val plan: Plan,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(LocateViewModel::class.java) ->
                    LocateViewModel(howYoRepository, plan)
                isAssignableFrom(PaymentViewModel::class.java) ->
                    PaymentViewModel(howYoRepository, plan)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}