package com.yuchen.howyo.data

import com.google.firebase.firestore.PropertyName
import java.util.*

data class GroupMessage(
    @get:PropertyName("plan_id") val planId: String? = null,
    val name: String? = null,
    val message: String? = null,
    @get:PropertyName("created_time") val createdTime: Long? = Calendar.getInstance().timeInMillis
)