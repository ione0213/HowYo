package com.yuchen.howyo.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GroupMessageData(
    val userId: String? = null,
    val userName: String? = null,
    val avatar: String? = null,
    val message: String? = null,
    var createdTime: Long? = 0L
) : Parcelable