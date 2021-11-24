package com.yuchen.howyo.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhotoData(
    var uri: Uri? = null,
    val url: String? = null,
    val fileName: String? = null,
    var isDeleted: Boolean? = false
) : Parcelable

@Parcelize
class Photos : ArrayList<PhotoData>(), Parcelable
