package com.yuchen.howyo.data

import com.google.firebase.firestore.PropertyName


data class CheckShoppingList(
    val id: String? = null,
    @get:PropertyName("plan_id") val planId: String? = null,
    @get:PropertyName("main_type")val mainType: String? = null,
    @get:PropertyName("sub_type")val subType: String? = null
)