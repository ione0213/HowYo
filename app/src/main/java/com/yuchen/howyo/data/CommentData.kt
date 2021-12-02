package com.yuchen.howyo.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommentData(
    val userName: String? = null,
    val avatar: String? = null,
    val comment: String? = null,
    var createdTime: Long? = 0L
) : Parcelable
