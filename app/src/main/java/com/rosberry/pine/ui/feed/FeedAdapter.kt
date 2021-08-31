package com.rosberry.pine.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.rosberry.pine.R
import com.rosberry.pine.databinding.ItemFeedBinding
import com.rosberry.pine.ui.base.BaseAdapter
import com.squareup.picasso.Picasso
import xyz.belvi.blurhash.BlurHash
import xyz.belvi.blurhash.BlurHashDecoder
import xyz.belvi.blurhash.blurHashDrawable
import xyz.belvi.blurhash.blurPlaceHolder

class FeedAdapter : BaseAdapter<FeedItem, ItemFeedBinding>(mutableListOf()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<FeedItem, ItemFeedBinding> {
        return ImageViewHolder(
                ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    inner class ImageViewHolder(binding: ItemFeedBinding) : BaseViewHolder<FeedItem, ItemFeedBinding>(binding) {

        override fun bind(item: FeedItem) {
            binding.description.text = item.description

            if (item.isLiked) {
                binding.like.setImageResource(R.drawable.ic_liked)
            } else {
                binding.like.setImageResource(R.drawable.ic_unliked)
            }

            binding.root.updateLayoutParams {
                width = item.width
                height = item.height

                item.blurHash?.let {
                    binding.image.setImageBitmap(it)
                }
            }

            Picasso.get()
                .load(item.url)
                .noPlaceholder()
                .resize(item.width, item.height)
                .centerInside()
                .into(binding.image)
        }
    }
}