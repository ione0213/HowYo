package com.yuchen.howyo.data

sealed class CheckListItem {
    abstract val id: String

    data class Title(val title: String) : CheckListItem() {
        override val id: String = title
    }

    data class CheckItem(val item: CheckShoppingList) : CheckListItem() {
        override val id: String
            get() = item.id ?: ""
    }
    data class AddBtn(val subType: String) : CheckListItem() {
        override val id: String
            get() = subType
    }
}
