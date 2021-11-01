package com.yuchen.howyo.data

import com.google.firebase.firestore.PropertyName


data class CheckShoppingList(
    var id: String? = null,
    @get:PropertyName("plan_id")
    @set:PropertyName("plan_id")
    var planId: String? = null,
    @get:PropertyName("main_type")
    @set:PropertyName("main_type")
    var mainType: String? = null,
    @get:PropertyName("sub_type")
    @set:PropertyName("sub_type")
    var subType: String? = null
)