package com.rosberry.pine.ui.image

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.github.satoshun.coroutine.autodispose.view.autoDisposeScope
import com.rosberry.pine.R
import com.rosberry.pine.databinding.ItemFeedBinding
import com.rosberry.pine.databinding.ItemProgressBinding
import com.rosberry.pine.databinding.ItemProgressFullscreenBinding
import com.rosberry.pine.util.FileUtil
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageAdapter(
        private val listener: OnImageClickListener,
        private val items: MutableList<BaseImageItem> = mutableListOf()
) :
        RecyclerView.Adapter<ImageAdapter.BaseImageViewHolder>() {

    private enum class ViewType {
        IMAGE, PROGRESS_BAR, PROGRESS_BAR_FULLSCREEN
    }

    fun startProgressBar() {
        if (hasProgress()) return

        items.add(ProgressItem(items.size == 0))
        notifyItemInserted(items.size - 1)
    }

    fun stopProgressBar() {
        val item = items.find { it is ProgressItem }
        val index = items.indexOf(item)
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun addItems(newItems: List<ImageItem>) {
        if (hasProgress()) {
            stopProgressBar()
        }
        val diffUtilCallback = ImageDiffUtilCallback((items + newItems) as List<ImageItem>)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        val oldItems = items.toList()
        items.clear()
        items.addAll(oldItems + newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setItems(newItems: List<ImageItem>) {
        if (hasProgress()) {
            stopProgressBar()
        }
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun hasProgress() = items.any { it is ProgressItem }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseImageViewHolder {
        return when (viewType) {
            ViewType.PROGRESS_BAR.ordinal -> {
                ProgressViewHolder(
                        ItemProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            ViewType.PROGRESS_BAR_FULLSCREEN.ordinal -> {
                ProgressFullscreenViewHolder(
                        ItemProgressFullscreenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        return when (items[position]) {
            is ProgressItem -> {
                if ((items[position] as? ProgressItem)?.isFullscreen == true) {
                    ViewType.PROGRESS_BAR_FULLSCREEN.ordinal
                } else {
                    ViewType.PROGRESS_BAR.ordinal
                }
            }
            else -> {
                ViewType.IMAGE.ordinal
            }
        }
    }

    abstract inner class BaseImageViewHolder(protected val _binding: ViewBinding) :
            RecyclerView.ViewHolder(_binding.root)

    inner class ProgressViewHolder(binding: ItemProgressBinding) :
            BaseImageViewHolder(binding)

    inner class ProgressFullscreenViewHolder(binding: ItemProgressFullscreenBinding) :
            BaseImageViewHolder(binding)

    inner class ImageViewHolder(binding: ItemFeedBinding) : BaseImageViewHolder(binding) {

        init {
            binding.image.setOnClickListener {
                (items[layoutPosition] as? ImageItem)?.let {
                    listener.onImageClick(it.id)
                }
            }
        }

        private val binding: ItemFeedBinding
            get() = _binding as ItemFeedBinding

        fun bind(item: ImageItem) {
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

    inner class ImageDiffUtilCallback(private val newList: List<ImageItem>) :
            DiffUtil.Callback() {

        override fun getOldListSize() = items.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                getOldItemByPosition(oldItemPosition)?.id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                getOldItemByPosition(oldItemPosition)?.isLiked == newList[newItemPosition].isLiked

        private fun getOldItemByPosition(position: Int): ImageItem? {
            return items[position] as? ImageItem
        }

    }

    override fun onBindViewHolder(holder: BaseImageViewHolder, position: Int) {
        if (holder is ImageViewHolder) {
            holder.bind(items[position] as ImageItem)
        }
    }

    override fun getItemCount() = items.size
}