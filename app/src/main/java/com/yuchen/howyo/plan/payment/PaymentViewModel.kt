package com.yuchen.howyo.plan.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Payment
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.signin.UserManager
import kotlinx.coroutines.*

class PaymentViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPlan: Plan?
) : ViewModel() {

    private var _currentUser = MutableLiveData<User>()

    val currentUser: LiveData<User>
        get() = _currentUser

    private val _plan = MutableLiveData<Plan>().apply {
        value = argumentPlan
    }

    val plan: LiveData<Plan>
        get() = _plan

    var payments = MutableLiveData<List<Payment>>()

    private val _shouldPay = MutableLiveData<Int>()

    val shouldPay: LiveData<Int>
        get() = _shouldPay

    private val _planMembersData = MutableLiveData<Set<User>>()

    val planMembersData: LiveData<Set<User>>
        get() = _planMembersData

    // Handle navigation to payment detail
    private val _navigateToPaymentDetail = MutableLiveData<Boolean>()

    val navigateToPaymentDetail: LiveData<Boolean>
        get() = _navigateToPaymentDetail

    // Handle navigation to edit exist payment detail
    private val _navigateToEditExistPaymentDetail = MutableLiveData<Payment>()

    val navigateToEditExistPaymentDetail: LiveData<Payment>
        get() = _navigateToEditExistPaymentDetail

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {

        _status.value = LoadApiStatus.LOADING
        fetchLiveUserResult()
        fetchLivePayments()
        fetchPlanMemberData()
    }

    private fun fetchLiveUserResult() {

        _currentUser = howYoRepository.getLiveUser(UserManager.userId ?: "")
    }

    private fun fetchLivePayments() {

        payments = howYoRepository.getLivePayments(plan.value?.id ?: "")
    }

    private fun fetchPlanMemberData() {

        val userDataList = mutableSetOf<User>()
        val planMembers = mutableListOf(plan.value?.authorId)
        plan.value?.companionList?.let { planMembers.addAll(it) }

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                planMembers.forEach { userId ->
                    when (val result = howYoRepository.getUser(userId!!)) {
                        is Result.Success -> {
                            userDataList.add(result.data)
                        }
                    }
                }
            }

            _planMembersData.value = userDataList.toSet()
        }

        _status.value = LoadApiStatus.DONE
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
