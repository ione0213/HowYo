package com.yuchen.howyo.data

import com.google.firebase.firestore.PropertyName

data class Notification(
    val id: String? = null,
    @get:PropertyName("to_user_id") val toUserId: String? = null,
    @get:PropertyName("from_user_id") val fromUserId: String? = null,
    @get:PropertyName("notification_type") val notificationType: String? = null,
    @get:PropertyName("plan_id") val planId: String? = null,
    val read: Boolean? = null,
    @get:PropertyName("created_time") val createdTime: Long? = 0L,
    @get:PropertyName("plan_cover_url") val planCoverUrl: String? = ""
)