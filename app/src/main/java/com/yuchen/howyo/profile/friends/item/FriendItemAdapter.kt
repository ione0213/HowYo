package com.yuchen.howyo.profile.friends.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.User
import com.yuchen.howyo.databinding.ItemFriendBinding

class FriendItemAdapter(
    private val viewModel: FriendItemViewModel
) : PagingDataAdapter<User, FriendItemAdapter.FriendViewHolder>(DiffCallback) {

    class FriendViewHolder(private var binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User, viewModel: FriendItemViewModel) {
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
        holder.bind(getItem(position)!!, viewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(
            ItemFriendBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }
}