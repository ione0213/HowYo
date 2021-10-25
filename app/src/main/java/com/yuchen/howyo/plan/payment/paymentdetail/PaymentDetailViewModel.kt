package com.yuchen.howyo.plan.payment.paymentdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Payment
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.source.HowYoRepository

class PaymentDetailViewModel (
    private val howYoRepository: HowYoRepository,
    private val argumentPayment: Payment?,
    private val argumentPlan: Plan
) : ViewModel() {

    private val _plan = MutableLiveData<Plan>().apply {
        value = argumentPlan
    }

    val plan: LiveData<Plan>
        get() =_plan

    val item = MutableLiveData<String>()

    val amount = MutableLiveData<String>()

    val selectedScheduleTypePosition = MutableLiveData<Int>()

    init {

        item.value = argumentPayment?.item ?: ""
        amount.value = "${argumentPayment?.amount ?: 0}"
    }
}