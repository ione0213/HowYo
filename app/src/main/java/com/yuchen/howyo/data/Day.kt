package com.yuchen.howyo.data

import com.google.firebase.firestore.PropertyName

data class Day(
    val id: String? = "",
    @get:PropertyName("plan_id") val planId: String? = "",
    val position: Int? = -1
)