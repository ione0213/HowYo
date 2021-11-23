package com.yuchen.howyo.plan.payment.paymentdetail

import android.widget.RadioGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.R
import com.yuchen.howyo.data.Payment
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.plan.payment.PaymentType
import kotlinx.coroutines.*

class PaymentDetailViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPayment: Payment?,
    private val argumentPlan: Plan
) : ViewModel() {

    private val _plan = MutableLiveData<Plan>().apply {
        value = argumentPlan
    }

    val plan: LiveData<Plan>
        get() = _plan

    private val _payment = MutableLiveData<Payment>().apply {
        value = when (argumentPayment) {
            null -> {
                Payment(
                    planId = argumentPlan.id
                )
            }
            else -> {
                argumentPayment
            }
        }
    }

    val payment: LiveData<Payment>
        get() = _payment

    val item = MutableLiveData<String>()

    val type = MutableLiveData<String>()

    val amount = MutableLiveData<String>()

    val selectedPaymentTypePosition = MutableLiveData<Int>()

    private val _invalidPayment = MutableLiveData<Int>()

    val invalidPayment: LiveData<Int>
        get() = _invalidPayment

    private val _isSavePayment = MutableLiveData<Boolean>()

    val isSavePayment: LiveData<Boolean>
        get() = _isSavePayment

    private val _paymentResult = MutableLiveData<Boolean>()

    val paymentResult: LiveData<Boolean>
        get() = _paymentResult

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

        setData()
    }

    private fun setData() {
        when {
            payment.value != null -> {
                payment.value.apply {
                    item.value = this?.item ?: ""
                    type.value = this?.type ?: ""
                    amount.value = this?.amount?.toString()

                    val planMembers = mutableListOf(plan.value?.authorId)
                    plan.value?.companionList?.let { planMembers.addAll(it) }

                    selectedPaymentTypePosition.value = planMembers.indexOf(this?.payer)
                }
            }
        }
    }

    fun prepareSubmitPayment() {

        when {
            item.value.isNullOrEmpty() -> {
                _invalidPayment.value = INVALID_FORMAT_ITEM_EMPTY
            }
            type.value.isNullOrEmpty() -> {
                _invalidPayment.value = INVALID_FORMAT_ITEM_TYPE_EMPTY
            }
            amount.value.isNullOrEmpty() || amount.value!!.toInt() == 0 -> {
                _invalidPayment.value = INVALID_FORMAT_AMOUNT_EMPTY
            }
            else -> {
                savePayment()
            }
        }
    }

    private fun savePayment() {
        _isSavePayment.value = true
    }

    fun onSavePayment() {
        _isSavePayment.value = null
    }

    fun submitPayment() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {

                val planMembers = mutableListOf(plan.value?.authorId)
                plan.value?.companionList?.let { planMembers.addAll(it) }

                val newPayment = payment.value?.copy(
                    item = item.value,
                    type = type.value,
                    amount = amount.value?.toInt() ?: 0,
                    payer = selectedPaymentTypePosition.value?.let { planMembers[it] }
                )

                _paymentResult.postValue(
                    when (payment.value?.id.isNullOrEmpty()) {
                        true -> {

                            when (
                                val result =
                                    newPayment?.let { howYoRepository.createPayment(it) }
                            ) {
                                is Result.Success -> result.data
                                else -> false
                            }
                        }
                        false -> {
                            when (
                                val result =
                                    newPayment?.let { howYoRepository.updatePayment(it) }
                            ) {
                                is Result.Success -> result.data
                                else -> false
                            }
                        }
                    }
                )
            }
        }
    }

    fun onSubmitPayment() {
        _paymentResult.value = null
        _status.value = LoadApiStatus.DONE
    }

    fun onTypeChanged(radioGroup: RadioGroup, id: Int) {
        type.value = when (id) {
            R.id.radio_self_payment -> PaymentType.SELF.type
            else -> PaymentType.SHARE.type
        }
    }

    fun deletePayment() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            withContext(Dispatchers.IO) {
                _paymentResult.postValue(
                    when (
                        val result =
                            payment.value?.let { howYoRepository.deletePayment(it) }
                    ) {
                        is Result.Success -> result.data
                        else -> false
                    }
                )
            }

            _status.value = LoadApiStatus.DONE
        }
    }

    companion object {

        const val INVALID_FORMAT_ITEM_EMPTY = 0x11
        const val INVALID_FORMAT_AMOUNT_EMPTY = 0x12
        const val INVALID_FORMAT_ITEM_TYPE_EMPTY = 0x13
    }
}
