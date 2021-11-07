package com.yuchen.howyo.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.source.HowYoRepository

class HomeViewModel(private val howYoRepository: HowYoRepository) : ViewModel() {

    //Plan data
    private val _plans = MutableLiveData<List<Plan>>()

    val plans: LiveData<List<Plan>>
        get() = _plans

    // Handle navigation to plan
    private val _navigateToPlan = MutableLiveData<Plan>()

    val navigateToPlan: LiveData<Plan>
        get() = _navigateToPlan

    // Handle navigation to notification
    private val _navigateToNotification = MutableLiveData<Boolean>()

    val navigateToNotification: LiveData<Boolean>
        get() = _navigateToNotification

    init {

        _plans.value = listOf(
            Plan(
                "VWN61M99rDHJnWcmguvE",
                "traveller",
                listOf(),
                "Go to Osaka",
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/sample_cover.jpg?alt=media&token=29567cb7-b77b-41e9-b713-7498e7329c81",
                "",
                1634601600000,
                1634688000000,
                "Japan",
                listOf("Jack", "Mary", "dd", "afadfas", "ddd"),
                listOf()
            ),
            Plan(
                "2",
                "Jack",
                listOf(),
                "Go to Nagoya",
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/Tokyo-Subway-Ticket.jpeg?alt=media&token=a2ea35b5-0e24-4f12-a35c-fd8f740b35ac",
                "",
                1634601600000,
                1634688000000,
                "Japan",
                listOf("Jack", "Mary", "Mark"),
                listOf()
            ),
            Plan(
                "3",
                "Mark",
                listOf(),
                "Go to Tokyo",
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/%E4%B8%8B%E8%BC%89%20(1).jpeg?alt=media&token=f5731312-ac2c-4a38-8e5e-4774ad32057a",
                "",
                1634601600000,
                1634688000000,
                "Japan",
                listOf("Jack", "Mary"),
                listOf()
            ),
            Plan(
                "4",
                "Mark",
                listOf(),
                "Go to Tokyo Cole",
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/%E4%B8%8B%E8%BC%89%20(1).jpeg?alt=media&token=f5731312-ac2c-4a38-8e5e-4774ad32057a",
                "",
                1634601600000,
                1634688000000,
                "Japan",
                listOf("Jack", "Mary"),
                listOf()
            )
        )
    }

    fun navigateToPlan(plan: Plan) {
        _navigateToPlan.value = plan
    }

    fun onPlanNavigated() {
        _navigateToPlan.value = null
    }

    fun navigateToNotification() {
        _navigateToNotification.value = true
    }

    fun onNotificationNavigated() {
        _navigateToNotification.value = null
    }
}