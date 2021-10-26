package com.yuchen.howyo.data

sealed class DetailPhotoItem {

    abstract val imgUrl: String

    object AddBtn : DetailPhotoItem() {
        override val imgUrl = ""
    }

    data class ImageUrl(val url: String) : DetailPhotoItem() {
        override val imgUrl: String
            get() = url
    }
}