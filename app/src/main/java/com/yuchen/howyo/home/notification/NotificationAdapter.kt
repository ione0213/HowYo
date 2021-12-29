package com.yuchen.howyo.home.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.Notification
import com.yuchen.howyo.data.NotificationItem
import com.yuchen.howyo.databinding.ItemNotifyFollowBinding
import com.yuchen.howyo.databinding.ItemNotifyLikeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationAdapter(private val viewModel: NotificationViewModel) :
    ListAdapter<NotificationItem, RecyclerView.ViewHolder>(DiffCallback) {
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class LikeViewHolder(private var binding: ItemNotifyLikeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification) {
            binding.notification = notification
            binding.executePendingBindings()
        }
    }

    class FollowViewHolder(private var binding: ItemNotifyFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification, viewModel: NotificationViewModel) {
            binding.user =
                viewModel.userDataSet.value?.first { it.id == notification.fromUserId }
            binding.notification = notification
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<NotificationItem>() {
        override fun areItemsTheSame(
            oldItem: NotificationItem,
            newItem: NotificationItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: NotificationItem,
            newItem: NotificationItem
        ): Boolean {
            return oldItem == newItem
        }

        private const val ITEM_VIEW_FOLLOW_ITEM = 0x00
        private const val ITEM_VIEW_LIKE_ITEM = 0x01
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_LIKE_ITEM -> {
                LikeViewHolder(
                    ItemNotifyLikeBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ).apply { viewModel = this@NotificationAdapter.viewModel }
                )
            }
            ITEM_VIEW_FOLLOW_ITEM -> {
                FollowViewHolder(
                    ItemNotifyFollowBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ).apply { viewModel = this@NotificationAdapter.viewModel }
                )
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LikeViewHolder -> {
                holder.bind((getItem(position) as NotificationItem.LikeItem).notification)
            }
            is FollowViewHolder -> {
                holder.bind(
                    (getItem(position) as NotificationItem.FollowItem).notification,
                    viewModel
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is NotificationItem.LikeItem -> ITEM_VIEW_LIKE_ITEM
            is NotificationItem.FollowItem -> ITEM_VIEW_FOLLOW_ITEM
        }
    }

    fun addNotificationItem(list: List<Notification>) {
        adapterScope.launch {
            val notificationItems: MutableList<NotificationItem> = mutableListOf()

            list.forEach {
                when (it.notificationType) {
                    NotificationType.LIKE.type -> {
                        notificationItems.add(NotificationItem.LikeItem(it))
                    }
                    NotificationType.FOLLOW.type -> {
                        notificationItems.add(NotificationItem.FollowItem(it))
                    }
                }
            }

            withContext(Dispatchers.Main) {
                submitList(notificationItems)
            }
        }
    }
}
