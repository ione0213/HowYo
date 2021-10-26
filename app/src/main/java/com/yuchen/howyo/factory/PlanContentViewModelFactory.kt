package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.plan.companion.locate.LocateViewModel
import com.yuchen.howyo.plan.groupmessage.GroupMessageViewModel
import com.yuchen.howyo.plan.payment.PaymentViewModel

class PlanContentViewModelFactory(
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
                isAssignableFrom(GroupMessageViewModel::class.java) ->
                    GroupMessageViewModel(howYoRepository, plan)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}