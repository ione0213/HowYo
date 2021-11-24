package com.yuchen.howyo.plan.detail.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.databinding.ItemDetailImageBinding

class DetailImagesAdapter(private val viewModel: DetailViewModel) :
    ListAdapter<String, DetailImagesAdapter.ImageViewHolder>(DiffCallback) {
    class ImageViewHolder(private var binding: ItemDetailImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            imageUrl.let {
                binding.imageUrl = it
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ItemDetailImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ).apply {
                viewModel = this@DetailImagesAdapter.viewModel
            }
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
