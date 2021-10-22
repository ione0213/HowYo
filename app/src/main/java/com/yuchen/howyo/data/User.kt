package com.yuchen.howyo.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    val id: String? = null,
    val avatar: String? = null,
    val introduction: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @get:PropertyName("fans_list") val fansList: List<String>? = null,
    @get:PropertyName("following_list") val followingList: List<String>? = null
) : Parcelable