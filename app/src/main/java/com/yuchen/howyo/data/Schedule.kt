package com.yuchen.howyo.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Schedule(
    var id: String? = "",
    @get:PropertyName("plan_id")
    @set:PropertyName("plan_id")
    var planId: String? = "",
    @get:PropertyName("day_id")
    @set:PropertyName("day_id")
    var dayId: String? = "",
    @get:PropertyName("schedule_type")
    @set:PropertyName("schedule_type")
    var scheduleType: String? = "",
    var title: String? = "",
    @get:PropertyName("photo_url_list")
    @set:PropertyName("photo_url_list")
    var photoUrlList: List<String>? = listOf(),
    @get:PropertyName("photo_file_name_list")
    @set:PropertyName("photo_file_name_list")
    var photoFileNameList: List<String>? = listOf(),
    var latitude: Double? = null,
    var longitude: Double? = null,
    @get:PropertyName("start_time")
    @set:PropertyName("start_time")
    var startTime: Long? = 0L,
    @get:PropertyName("end_time")
    @set:PropertyName("end_time")
    var endTime: Long? = 0L,
    var budget: Int? = 0,
    @get:PropertyName("ref_rul")
    @set:PropertyName("ref_rul")
    var refUrl: String? = "",
    var notification: Boolean? = null,
    var position: Int? = -1,
    var address: String? = "",
    var remark: String? = ""
) : Parcelable