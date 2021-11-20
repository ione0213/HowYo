package com.yuchen.howyo.plan.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Payment
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.signin.UserManager
import com.yuchen.howyo.util.Logger

class PaymentViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPlan: Plan?
) : ViewModel() {

    private var _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    private val _plan = MutableLiveData<Plan>().apply {
        value = argumentPlan
    }

    val plan: LiveData<Plan>
        get() = _plan

    var payments = MutableLiveData<List<Payment>>()

    private val _shouldPay = MutableLiveData<Int>()

    val shouldPay: LiveData<Int>
        get() = _shouldPay

    // Handle navigation to payment detail
    private val _navigateToPaymentDetail = MutableLiveData<Boolean>()

    val navigateToPaymentDetail: LiveData<Boolean>
        get() = _navigateToPaymentDetail

    // Handle navigation to edit exist payment detail
    private val _navigateToEditExistPaymentDetail = MutableLiveData<Payment>()

    val navigateToEditExistPaymentDetail: LiveData<Payment>
        get() = _navigateToEditExistPaymentDetail

    init {

        getLiveUserResult()
        getLivePayments()
    }

    private fun getLiveUserResult() {

        _user = howYoRepository.getLiveUser(UserManager.userId ?: "")
    }

    private fun getLivePayments() {

        payments = howYoRepository.getLivePayments(plan.value?.id ?: "")
    }

    fun calculateShouldPay() {

        val sharePayments = payments.value?.filter { it.type == PaymentType.SHARE.type }
        val planMembersCount = plan.value?.companionList?.size?.plus(1)
        val sharedAmount = sharePayments?.map { it.amount }?.sum()?.div(planMembersCount ?: 1)
        val currentUserPayAmount =
            sharePayments?.filter { it.payer == UserManager.userId }?.map { it.amount }?.sum()

        _shouldPay.value = sharedAmount?.minus(currentUserPayAmount ?: 0)
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