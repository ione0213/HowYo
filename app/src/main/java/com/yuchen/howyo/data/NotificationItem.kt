package com.yuchen.howyo.data

sealed class NotificationItem {
    abstract val id: String

    data class FollowItem(val notification: Notification) : NotificationItem() {
        override val id: String
            get() = notification.id
    }

    data class LikeItem(val notification: Notification) : NotificationItem() {
        override val id: String
            get() = notification.id
    }
}
