package com.yuchen.howyo.data

import com.google.firebase.firestore.PropertyName

data class CheckShoppingItem(
    val id: String? = null,
    @get:PropertyName("check_shopping_list_id")
    val checkShoppingListId: String? = null,
    val item: String? = null,
    val checked: Boolean? = false
)