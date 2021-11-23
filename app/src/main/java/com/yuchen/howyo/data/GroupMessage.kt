package com.yuchen.howyo.data

import com.google.firebase.firestore.PropertyName
import java.util.*

data class GroupMessage(
    @get:PropertyName("plan_id")
    @set:PropertyName("plan_id")
    var planId: String? = null,
    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String? = null,
    val message: String? = null,
    @get:PropertyName("created_time")
    @set:PropertyName("created_time")
    var createdTime: Long? = Calendar.getInstance().timeInMillis
)
