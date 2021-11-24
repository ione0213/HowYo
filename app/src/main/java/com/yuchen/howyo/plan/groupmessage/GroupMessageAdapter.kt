package com.yuchen.howyo.plan.groupmessage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.GroupMessageData
import com.yuchen.howyo.data.GroupMessageDataItem
import com.yuchen.howyo.databinding.ItemChatBinding
import com.yuchen.howyo.databinding.ItemChatReceiveBinding
import com.yuchen.howyo.signin.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupMessageAdapter :
    ListAdapter<GroupMessageDataItem, RecyclerView.ViewHolder>(DiffCallback) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class SendChatViewHolder(private var binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(groupMessageData: GroupMessageData) {

            binding.groupMessageData = groupMessageData
            binding.executePendingBindings()
        }
    }

    class ReceiveChatViewHolder(private var binding: ItemChatReceiveBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(groupMessageData: GroupMessageData) {

            binding.groupMessageData = groupMessageData
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<GroupMessageDataItem>() {
        override fun areItemsTheSame(
            oldItem: GroupMessageDataItem,
            newItem: GroupMessageDataItem
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: GroupMessageDataItem,
            newItem: GroupMessageDataItem
        ): Boolean {
            return oldItem == newItem
        }

        private const val ITEM_VIEW_MSG_SELF = 0x00
        private const val ITEM_VIEW_MSG_RECEIVE = 0x01
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_MSG_SELF -> {
                SendChatViewHolder(
                    ItemChatBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            ITEM_VIEW_MSG_RECEIVE -> {
                ReceiveChatViewHolder(
                    ItemChatReceiveBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is SendChatViewHolder -> {
                holder.bind((getItem(position) as GroupMessageDataItem.MessageSelf).groupMessageData)
            }
            is ReceiveChatViewHolder -> {
                holder.bind((getItem(position) as GroupMessageDataItem.MessageReceive).groupMessageData)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is GroupMessageDataItem.MessageSelf -> ITEM_VIEW_MSG_SELF
            is GroupMessageDataItem.MessageReceive -> ITEM_VIEW_MSG_RECEIVE
        }
    }

    fun checkMessageOwner(list: List<GroupMessageData>) {

        adapterScope.launch {

            val groupMsgDataItems: MutableList<GroupMessageDataItem> = mutableListOf()

            list.forEach {
                when (it.userId) {
                    UserManager.userId -> groupMsgDataItems.add(GroupMessageDataItem.MessageSelf(it))
                    else -> groupMsgDataItems.add(GroupMessageDataItem.MessageReceive(it))
                }
            }

            withContext(Dispatchers.Main) {
                submitList(groupMsgDataItems)
            }
        }
    }
}
