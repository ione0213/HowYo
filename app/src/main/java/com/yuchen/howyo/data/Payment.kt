package com.yuchen.howyo.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Payment(
    @get:PropertyName("plan_id") val planId: String? = null,
    val item: String? = null,
    @get:PropertyName("item_type") val itemType: String? = null,
    val amount: Int? = null,
    val payer: String? = null
) : Parcelable