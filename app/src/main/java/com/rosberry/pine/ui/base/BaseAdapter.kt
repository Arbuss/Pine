package com.rosberry.pine.ui.base

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
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
        val diffUtilCallback = createDiffUtilCallback(newItems)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    abstract fun createDiffUtilCallback(newList: List<T>): BaseDiffUtilCallback<T>

    abstract class BaseViewHolder<in T : BaseAdapterItem, B : ViewBinding>(val binding: B) :
            RecyclerView.ViewHolder(binding.root) {

        abstract fun bind(item: T)
    }

    abstract inner class BaseDiffUtilCallback<T>(protected val newList: List<T>) :
            DiffUtil.Callback() {

        override fun getOldListSize() = items.size

        override fun getNewListSize() = newList.size
    }
}
