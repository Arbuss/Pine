package com.rosberry.pine.ui.feed

import android.graphics.Bitmap
import com.rosberry.pine.ui.base.BaseAdapterItem

data class FeedItem(
        val id: String,
        val description: String,
        val url: String,
        val width: Int,
        val height: Int,
        val blurHash: Bitmap?,
        val isLiked: Boolean
): BaseAdapterItem
