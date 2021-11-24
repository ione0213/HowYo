package com.yuchen.howyo.profile.friends.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.User
import com.yuchen.howyo.databinding.ItemFriendBinding
import com.yuchen.howyo.profile.friends.FriendFilter
import com.yuchen.howyo.signin.UserManager

class FriendItemAdapter(
    private val viewModel: FriendItemViewModel,
    private val friendType: FriendFilter,
    private val userId: String
) : ListAdapter<User, FriendItemAdapter.FriendViewHolder>(DiffCallback) {
    class FriendViewHolder(private var binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            user: User,
            viewModel: FriendItemViewModel,
            friendType: FriendFilter,
            userId: String
        ) {
            binding.btnFriendUnfollow.visibility = when (friendType) {
                FriendFilter.FOLLOWING -> {
                    when (userId) {
                        UserManager.userId -> View.VISIBLE
                        else -> View.GONE
                    }
                }
                FriendFilter.FANS -> View.GONE
            }

            binding.btnFansRemove.visibility = when (friendType) {
                FriendFilter.FANS -> {
                    when (userId) {
                        UserManager.userId -> View.VISIBLE
                        else -> View.GONE
                    }
                }
                FriendFilter.FOLLOWING -> View.GONE
            }

            binding.user = user
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(getItem(position)!!, viewModel, friendType, userId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(
            ItemFriendBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }
}
