package com.rosberry.pine.ui.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T : BaseAdapterItem,
        B : ViewBinding>(protected val items: MutableList<T>) :
        RecyclerView.Adapter<BaseAdapter.BaseViewHolder<T, B>>() {

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T, B>

    override fun onBindViewHolder(holder: BaseViewHolder<T, B>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun addItems(newItems: List<T>) {
        val lastIndex = items.lastIndex
        items.addAll(newItems)
        notifyItemRangeInserted(lastIndex, lastIndex + newItems.size)
    }

    abstract class BaseViewHolder<in T : BaseAdapterItem, B : ViewBinding>(val binding: B) :
            RecyclerView.ViewHolder(binding.root) {

        abstract fun bind(item: T)
    }
}
