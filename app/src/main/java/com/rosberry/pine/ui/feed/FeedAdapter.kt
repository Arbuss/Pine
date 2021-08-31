package com.rosberry.pine.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rosberry.pine.R
import com.rosberry.pine.databinding.ItemFeedBinding
import com.rosberry.pine.ui.base.BaseAdapter
import com.squareup.picasso.Picasso

class FeedAdapter(private val screenWidth: Int) : BaseAdapter<FeedItem, ItemFeedBinding>(mutableListOf()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<FeedItem, ItemFeedBinding> {
        return ImageViewHolder(
                ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    inner class ImageViewHolder(binding: ItemFeedBinding) : BaseViewHolder<FeedItem, ItemFeedBinding>(binding) {
        //        init {
        //            binding.image.updateLayoutParams {
        //                width = screenWidth
        //                height = screenHeight
        //            }
        //        }

        override fun bind(item: FeedItem) {
            binding.description.text = item.description

            val multiplier = item.width / screenWidth + 1

            val imageWidth = item.width / multiplier
            val imageHeight = item.height / multiplier

            if(item.isLiked) {
                binding.like.setImageResource(R.drawable.ic_liked)
            } else {
                binding.like.setImageResource(R.drawable.ic_unliked)
            }

            Picasso.get()
                .load(item.url)
                .resize(imageWidth, imageHeight)
                .into(binding.image)
        }
    }
}