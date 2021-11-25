package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.data.Payment
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.plan.payment.paymentdetail.PaymentDetailViewModel

class PaymentDetailViewModelFactory(
    private val howYoRepository: HowYoRepository,
    private val payment: Payment?,
    private val plan: Plan?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(PaymentDetailViewModel::class.java) ->
                    PaymentDetailViewModel(howYoRepository, payment, plan)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
