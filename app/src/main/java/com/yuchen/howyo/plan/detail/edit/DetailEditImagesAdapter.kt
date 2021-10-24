package com.yuchen.howyo.plan.detail.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.DetailPhotoItem
import com.yuchen.howyo.databinding.ItemDetailEditImageAddBinding
import com.yuchen.howyo.databinding.ItemDetailEditImageBinding
import com.yuchen.howyo.databinding.ItemDetailImageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailEditImagesAdapter(private val viewModel: DetailEditViewModel) :
    ListAdapter<DetailPhotoItem, RecyclerView.ViewHolder>(DiffCallback) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class ImageViewHolder(private var binding: ItemDetailEditImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String) {

            imageUrl.let {
                binding.imageUrl = it
                binding.executePendingBindings()
            }
        }
    }

    class AddViewHolder(
        private var binding: ItemDetailEditImageAddBinding,
        private val viewModel: DetailEditViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DetailPhotoItem>() {
        override fun areItemsTheSame(oldItem: DetailPhotoItem, newItem: DetailPhotoItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: DetailPhotoItem,
            newItem: DetailPhotoItem
        ): Boolean {
            return oldItem.imgUrl == newItem.imgUrl
        }

        private const val ITEM_VIEW_ADD_BTN = 0x00
        private const val ITEM_VIEW_IMAGE_URL = 0x01
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_ADD_BTN -> AddViewHolder(
                ItemDetailEditImageAddBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                viewModel
            )
            ITEM_VIEW_IMAGE_URL -> ImageViewHolder(
                ItemDetailEditImageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is AddViewHolder -> {
                holder.bind()
            }
            is ImageViewHolder -> {
                holder.bind((getItem(position) as DetailPhotoItem.ImageUrl).imgUrl)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DetailPhotoItem.AddBtn -> ITEM_VIEW_ADD_BTN
            is DetailPhotoItem.ImageUrl -> ITEM_VIEW_IMAGE_URL
        }
    }

    fun addPhotoAndBtn(list: List<String>) {
        adapterScope.launch {
            val detailPhotoItems: MutableList<DetailPhotoItem> = mutableListOf()
            list.forEach {
                detailPhotoItems.add(DetailPhotoItem.ImageUrl(it))
            }
            when {
                list.size < 3 -> detailPhotoItems.add(DetailPhotoItem.AddBtn)
            }

            withContext(Dispatchers.Main) {
                submitList(detailPhotoItems)
            }
        }
    }
}