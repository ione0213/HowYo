package com.yuchen.howyo.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    var id: String? = null,
    var name: String? = null,
    var email: String? = null,
    var avatar: String? = null,
    var introduction: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    @get:PropertyName("fans_list")
    @set:PropertyName("fans_list")
    var fansList: List<String>? = listOf(),
    @get:PropertyName("following_list")
    @set:PropertyName("following_list")
    var followingList: List<String>? = listOf()
) : Parcelable