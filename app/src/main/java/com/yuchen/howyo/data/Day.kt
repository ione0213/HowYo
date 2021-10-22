package com.yuchen.howyo.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Day(
    val id: String? = "",
    @get:PropertyName("plan_id") val planId: String? = "",
    val position: Int? = -1
): Parcelable