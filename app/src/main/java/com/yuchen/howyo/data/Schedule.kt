package com.yuchen.howyo.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Schedule(
    @get:PropertyName("day_id") val dayId: String? = "",
    @get:PropertyName("schedule_type") val scheduleType: String? = "",
    val title: String? = "",
    @get:PropertyName("photo_url_list") val photoUrlList: List<String>? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @get:PropertyName("start_time") val startTime: Long? = 0L,
    @get:PropertyName("end_time") val endTime: Long? = 0L,
    val from: String? = "",
    val to: String? = "",
    val budget: Int? = 0,
    @get:PropertyName("ref_rul") val refUrl: String? = "",
    val notification: Boolean? = null,
    val position: Int? = -1,
    val address: String? = "",
    val remark: String? = ""
): Parcelable