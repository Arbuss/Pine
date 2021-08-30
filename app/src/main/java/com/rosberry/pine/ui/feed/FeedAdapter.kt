package com.rosberry.pine.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rosberry.pine.databinding.ItemFeedBinding
import com.rosberry.pine.ui.base.BaseAdapter
import com.squareup.picasso.Picasso

class FeedAdapter : BaseAdapter<FeedItem, ItemFeedBinding>(mutableListOf()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<FeedItem, ItemFeedBinding> {
        return ImageViewHolder(
                ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    private class ImageViewHolder(binding: ItemFeedBinding) : BaseViewHolder<FeedItem, ItemFeedBinding>(binding) {

        override fun bind(item: FeedItem) {
            binding.description.text = item.description

            val imageWidth = item.width
            val imageHeight = item.width

            Picasso.get()
                .load(item.url)
                .resize(imageWidth, imageHeight)
                .into(binding.image)
        }
    }
}