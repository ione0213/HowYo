package com.yuchen.howyo.plan.companion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.User
import com.yuchen.howyo.databinding.ItemCompanionBinding

class CompanionAdapter(val viewModel: CompanionViewModel): ListAdapter<String, CompanionAdapter.FriendViewHolder>(DiffCallback) {

    class FriendViewHolder(
        private var binding: ItemCompanionBinding,
        private val viewModel: CompanionViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: String) {
            binding.viewModel = viewModel
//            binding.viewHolder = this
            user.let {
                binding.user = it
                binding.executePendingBindings()
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(
            ItemCompanionBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}