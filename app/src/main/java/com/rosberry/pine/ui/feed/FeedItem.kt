package com.rosberry.pine.ui.feed

import com.rosberry.pine.ui.base.BaseAdapterItem

data class FeedItem(
        val id: String,
        val description: String,
        val url: String,
        val width: Int,
        val height: Int,
        val isLiked: Boolean
): BaseAdapterItem
