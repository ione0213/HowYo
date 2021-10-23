package com.yuchen.howyo.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Plan(
    val id: String? = "",
    @get:PropertyName("author_id") val authorId: String? = "",
    @get:PropertyName("companion_list") val companionList: List<String>? = listOf(),
    val title: String? = "",
    @get:PropertyName("cover_photo_url") val coverPhotoUrl: String? = "",
    @get:PropertyName("start_date") val startDate: Long? = 0L,
    @get:PropertyName("end_date") val endDate: Long? = 0L,
    val destination: String? = "",
    @get:PropertyName("like_list") val likeList: List<String>? = listOf(),
    @get:PropertyName("plan_collected_list") val planCollectedList: List<String>? = listOf()
) : Parcelable