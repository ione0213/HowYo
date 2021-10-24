package com.yuchen.howyo.plan.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Payment
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.source.HowYoRepository

class PaymentViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPlan: Plan
) : ViewModel(){

    private val _plan = MutableLiveData<Plan>().apply {
        value = argumentPlan
    }

    val plan: LiveData<Plan>
        get() =_plan

    private val _payments = MutableLiveData<List<Payment>>()

    val payments: LiveData<List<Payment>>
        get() = _payments

    // Handle navigation to payment detail
    private val _navigateToPaymentDetail = MutableLiveData<Boolean>()

    val navigateToPaymentDetail: LiveData<Boolean>
        get() = _navigateToPaymentDetail

    // Handle navigation to edit exist payment detail
    private val _navigateToEditExistPaymentDetail = MutableLiveData<Payment>()

    val navigateToEditExistPaymentDetail: LiveData<Payment>
        get() = _navigateToEditExistPaymentDetail

    init {

        _payments.value = listOf(
            Payment(
                "A",
                "拉麵",
                "均分費用",
                360,
                "Yu Chen"
            ),
            Payment(
                "A",
                "拉麵2",
                "均分費用",
                490,
                "Yu Chen"
            ),
            Payment(
                "A",
                "拉麵3",
                "均分費用",
                999,
                "Yu Chen"
            )
        )
    }

    fun navigateToPaymentDetail() {
        _navigateToPaymentDetail.value = true
    }

    fun onPaymentDetailNavigated() {
        _navigateToPaymentDetail.value = null
    }

    fun navigateToEditExistPaymentDetail(payment: Payment) {
        _navigateToEditExistPaymentDetail.value = payment
    }

    fun onEditExistPaymentDetailNavigated() {
        _navigateToEditExistPaymentDetail.value = null
    }
}