package com.yuchen.howyo.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import com.yuchen.howyo.plan.PlanPrivacy
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Plan(
    var id: String = "",
    @get:PropertyName("author_id")
    @set:PropertyName("author_id")
    var authorId: String? = "",
    @get:PropertyName("companion_list")
    @set:PropertyName("companion_list")
    var companionList: List<String>? = listOf(),
    var title: String? = "",
    @get:PropertyName("cover_photo_url")
    @set:PropertyName("cover_photo_url")
    var coverPhotoUrl: String? = "",
    @get:PropertyName("cover_file_name")
    @set:PropertyName("cover_file_name")
    var coverFileName: String = "",
    @get:PropertyName("start_date")
    @set:PropertyName("start_date")
    var startDate: Long? = 0L,
    @get:PropertyName("end_date")
    @set:PropertyName("end_date")
    var endDate: Long? = 0L,
    var destination: String? = "",
    @get:PropertyName("like_list")
    @set:PropertyName("like_list")
    var likeList: List<String>? = listOf(),
    @get:PropertyName("plan_collected_list")
    @set:PropertyName("plan_collected_list")
    var planCollectedList: List<String>? = listOf(),
    var privacy: String? = PlanPrivacy.PRIVATE.value
) : Parcelable