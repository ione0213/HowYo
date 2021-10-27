package com.yuchen.howyo.data

import com.google.firebase.firestore.PropertyName

data class CheckShoppingItem(
    var id: String? = null,
    @get:PropertyName("check_shopping_list_id")
    @set:PropertyName("check_shopping_list_id")
    var checkShoppingListId: String? = null,
    var item: String? = null,
    var checked: Boolean? = false
)