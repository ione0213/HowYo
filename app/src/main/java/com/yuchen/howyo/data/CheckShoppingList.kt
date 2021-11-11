package com.yuchen.howyo.data

import com.google.firebase.firestore.PropertyName
import java.util.*


data class CheckShoppingList(
    var id: String = "",
    @get:PropertyName("plan_id")
    @set:PropertyName("plan_id")
    var planId: String? = null,
    @get:PropertyName("main_type")
    @set:PropertyName("main_type")
    var mainType: String? = null,
    @get:PropertyName("sub_type")
    @set:PropertyName("sub_type")
    var subType: String? = null,
    var item: String? = "",
    var check: Boolean = false,
    @get:PropertyName("created_time")
    @set:PropertyName("created_time")
    var createdTime: Long? = Calendar.getInstance().timeInMillis
)