package com.yuchen.howyo.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommentData(
    val userId: String? = null,
    val avatar: String? = null,
    val comment: String? = null,
    var createdTime: Long? = 0L
) : Parcelable