package com.yuchen.howyo.data

sealed class GroupMessageDataItem {

    abstract val createdTime: Long

    data class MessageSelf(val groupMessageData: GroupMessageData) : GroupMessageDataItem() {
        override val createdTime: Long
            get() = groupMessageData.createdTime ?: 0L
    }

    data class MessageReceive(val groupMessageData: GroupMessageData) : GroupMessageDataItem() {
        override val createdTime: Long
            get() = groupMessageData.createdTime ?: 0L
    }
}