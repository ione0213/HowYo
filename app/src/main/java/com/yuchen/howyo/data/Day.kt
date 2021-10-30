package com.yuchen.howyo.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Day(
    var id: String = "",
    @get:PropertyName("plan_id")
    @set:PropertyName("plan_id")
    var planId: String? = "",
    var position: Int? = -1
) : Parcelable