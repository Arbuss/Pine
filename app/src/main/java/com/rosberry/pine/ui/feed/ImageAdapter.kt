package com.rosberry.pine.ui.feed

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.github.satoshun.coroutine.autodispose.view.autoDisposeScope
import com.rosberry.pine.R
import com.rosberry.pine.databinding.ItemFeedBinding
import com.rosberry.pine.databinding.ItemProgressBinding
import com.rosberry.pine.ui.base.BaseAdapter
import com.rosberry.pine.util.FileUtil
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageAdapter : BaseAdapter<ImageItem>(mutableListOf()) {

    private enum class ViewType {
        IMAGE, PROGRESS_BAR
    }

    fun startProgressBar() {
        if (items.any { it.isProgress }) return
        items.add(ImageItem("", "", "",
                0, 0, null, false, true)) // TODO почистить, возможно сделать общего родителя для двух итемов холдеров
        notifyItemInserted(items.size - 1)
    }

    fun stopProgressBar() {
        val item = items.find { it.isProgress }
        val index = items.indexOf(item)
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ImageItem> {
        return when (viewType) {
            ViewType.PROGRESS_BAR.ordinal -> {
                ProgressViewHolder(
                        ItemProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            else -> {
                ImageViewHolder(
                        ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].isProgress) {
            ViewType.PROGRESS_BAR.ordinal
        } else {
            ViewType.IMAGE.ordinal
        }
    }

    inner class ProgressViewHolder(binding: ItemProgressBinding) :
            BaseViewHolder<ImageItem>(binding) {

        override fun bind(item: ImageItem) {

        }

    }

    inner class ImageViewHolder(binding: ItemFeedBinding) : BaseViewHolder<ImageItem>(binding) {

        private val binding: ItemFeedBinding
            get() = _binding as ItemFeedBinding

        override fun bind(item: ImageItem) {
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

        private fun setImage(item: ImageItem, placeholder: Drawable?) {
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

    override fun createDiffUtilCallback(newList: List<ImageItem>) = ImageDiffUtilCallback(newList)

    inner class ImageDiffUtilCallback(newList: List<ImageItem>) :
            BaseDiffUtilCallback<ImageItem>(newList) {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                items[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                items[oldItemPosition].isLiked == newList[newItemPosition].isLiked

    }
}