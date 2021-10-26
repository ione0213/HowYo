package com.yuchen.howyo.home.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Notification
import com.yuchen.howyo.data.source.HowYoRepository

class NotificationViewModel(private val howYoRepository: HowYoRepository) : ViewModel() {

    //Notification data
    private val _notifications = MutableLiveData<List<Notification>>()

    val notifications: LiveData<List<Notification>>
        get() = _notifications

    init {

        _notifications.value = listOf(
            Notification(
                "1",
                "Traveller",
                "Jack",
                NotificationType.LIKE.type,
                "id123",
                false,
                1635078600,
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/sample_cover.jpg?alt=media&token=29567cb7-b77b-41e9-b713-7498e7329c81"
            ),
            Notification(
                "2",
                "Traveller",
                "Jack",
                NotificationType.FOLLOW.type,
                "",
                false,
                1635078600
            ),
            Notification(
                "3",
                "Traveller",
                "Mary",
                NotificationType.FOLLOW.type,
                "",
                false,
                1635081300
            ),
            Notification(
                "4",
                "Traveller",
                "Katia",
                NotificationType.LIKE.type,
                "id123",
                false,
                1635075941,
                "https://firebasestorage.googleapis.com/v0/b/howyo-ione.appspot.com/o/Tokyo-Subway-Ticket.jpeg?alt=media&token=a2ea35b5-0e24-4f12-a35c-fd8f740b35ac"
            )
        )
    }
}