package com.yuchen.howyo.home.notification

import com.yuchen.howyo.R
import com.yuchen.howyo.util.Util.getString

enum class NotificationType(val type: String) {
    LIKE(getString(R.string.notification_like_type)),
    FOLLOW(getString(R.string.notification_follow_type))
}