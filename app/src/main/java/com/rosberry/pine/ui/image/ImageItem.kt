package com.rosberry.pine.ui.image

data class ImageItem(
        val id: String,
        val description: String,
        val url: String,
        val width: Int,
        val height: Int,
        val blurHashUri: String?,
        val isLiked: Boolean,
        val isProgress: Boolean = false
) : BaseImageItem
