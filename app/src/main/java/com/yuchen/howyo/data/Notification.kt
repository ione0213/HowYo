package com.yuchen.howyo.data

import com.google.firebase.firestore.PropertyName
import java.util.*

data class Notification(
    var id: String? = null,
    @get:PropertyName("to_user_id")
    @set:PropertyName("to_user_id")
    var toUserId: String? = null,
    @get:PropertyName("from_user_id")
    @set:PropertyName("from_user_id")
    var fromUserId: String? = null,
    @get:PropertyName("notification_type")
    @set:PropertyName("notification_type")
    var notificationType: String? = null,
    @get:PropertyName("plan_id")
    @set:PropertyName("plan_id")
    var planId: String? = null,
    val read: Boolean? = false,
    @get:PropertyName("created_time")
    @set:PropertyName("created_time")
    var createdTime: Long? = Calendar.getInstance().timeInMillis,
    @get:PropertyName("plan_cover_url")
    @set:PropertyName("plan_cover_url")
    var planCoverUrl: String? = ""
)