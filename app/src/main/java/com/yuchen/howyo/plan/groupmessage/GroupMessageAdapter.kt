package com.yuchen.howyo.plan.groupmessage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.GroupMessage
import com.yuchen.howyo.databinding.ItemChatBinding

class GroupMessageAdapter :
    ListAdapter<GroupMessage, GroupMessageAdapter.ChatViewHolder>(DiffCallback) {

    class ChatViewHolder(private var binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(groupMessage: GroupMessage) {
            when (groupMessage.name) {
                "Traveller" -> {
                    binding.apply {
                        imgChatReceivedAvatar.visibility = View.GONE
                        textviewChatReceivedMsg.visibility = View.GONE
                        textviewChatReceivedName.visibility = View.GONE
                        textviewChatReceivedTime.visibility = View.GONE
                    }
                }
                else -> {
                    binding.apply {
                        textviewChatSent.visibility = View.GONE
                        textviewChatSentTime.visibility = View.GONE
                    }
                }
            }
            binding.groupMessage = groupMessage
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<GroupMessage>() {
        override fun areItemsTheSame(oldItem: GroupMessage, newItem: GroupMessage): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: GroupMessage, newItem: GroupMessage): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            ItemChatBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}