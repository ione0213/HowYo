package com.yuchen.howyo.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Comment(
    var id: String = "",
    @get:PropertyName("plan_id")
    @set:PropertyName("plan_id")
    var planId: String? = null,
    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String? = null,
    val comment: String? = null,
    @get:PropertyName("created_time")
    @set:PropertyName("created_time")
    var createdTime: Long? = Calendar.getInstance().timeInMillis
) : Parcelable