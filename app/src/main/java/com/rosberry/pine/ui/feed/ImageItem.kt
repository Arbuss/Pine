package com.rosberry.pine.ui.feed

import com.rosberry.pine.ui.base.BaseAdapterItem

data class ImageItem(
        val id: String,
        val description: String,
        val url: String,
        val width: Int,
        val height: Int,
        val blurHashUri: String?,
        val isLiked: Boolean,
        val isProgress: Boolean = false
) : BaseAdapterItem
