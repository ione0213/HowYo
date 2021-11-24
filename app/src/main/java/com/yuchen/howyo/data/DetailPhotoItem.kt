package com.yuchen.howyo.data

sealed class DetailPhotoItem {
    abstract val imgUrl: String

    object AddBtn : DetailPhotoItem() {
        override val imgUrl = ""
    }

    data class ImageData(val photoData: PhotoData) : DetailPhotoItem() {
        override val imgUrl: String
            get() = photoData.url ?: ""
    }
}
