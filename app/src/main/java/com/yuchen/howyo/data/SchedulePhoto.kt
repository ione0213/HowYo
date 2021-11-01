package com.yuchen.howyo.data

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SchedulePhoto(
    val uri: Uri? = null,
    val url: String? = null,
    val fileName: String? = null
) : Parcelable