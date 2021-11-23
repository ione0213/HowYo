package com.yuchen.howyo.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import java.util.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Payment(
    var id: String = "",
    @get:PropertyName("plan_id")
    @set:PropertyName("plan_id")
    var planId: String? = null,
    val item: String? = "",
    @get:PropertyName("item_type")
    @set:PropertyName("item_type")
    var type: String? = "",
    val amount: Int = 0,
    val payer: String? = "",
    @get:PropertyName("created_time")
    @set:PropertyName("created_time")
    var createdTime: Long? = Calendar.getInstance().timeInMillis
) : Parcelable
