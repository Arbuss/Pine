package com.rosberry.pine.ui.feed

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.github.satoshun.coroutine.autodispose.view.autoDisposeScope
import com.rosberry.pine.R
import com.rosberry.pine.databinding.ItemFeedBinding
import com.rosberry.pine.ui.base.BaseAdapter
import com.rosberry.pine.util.FileUtil
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

                item.blurHashUri?.let {
                    binding.root.autoDisposeScope.launch(Dispatchers.IO) {
                        val bitmapDrawable = BitmapDrawable(binding.root.resources, FileUtil.readBitmap(it))
                        withContext(Dispatchers.Main) {
                            setImage(item, bitmapDrawable)
                        }
                    }
                } ?: run {
                    setImage(item, null)
                }
            }
        }

        private fun setImage(item: FeedItem, placeholder: Drawable?) {
            val picasso = Picasso.get()

            val requestCreator = picasso.load(item.url)

            if (placeholder == null) {
                requestCreator.noPlaceholder()
            } else {
                requestCreator.placeholder(placeholder)
            }

            requestCreator
                .resize(item.width, item.height)
                .centerCrop()
                .into(binding.image)
        }
    }

    override fun createDiffUtilCallback(newList: List<FeedItem>) = ImageDiffUtilCallback(newList)

    inner class ImageDiffUtilCallback(newList: List<FeedItem>) :
            BaseDiffUtilCallback<FeedItem>(newList) {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                items[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                items[oldItemPosition].isLiked == newList[newItemPosition].isLiked

    }
}