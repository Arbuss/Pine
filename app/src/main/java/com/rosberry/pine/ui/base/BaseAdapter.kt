package com.rosberry.pine.ui.base

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T : BaseAdapterItem>(protected val items: MutableList<T>) :
        RecyclerView.Adapter<BaseAdapter.BaseViewHolder<T>>() {

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T>

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun addItems(newItems: List<T>) {
        val diffUtilCallback = createDiffUtilCallback(items + newItems)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        val oldItems = items.toList()
        items.clear()
        items.addAll(oldItems + newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    abstract fun createDiffUtilCallback(newList: List<T>): BaseDiffUtilCallback<T>

    abstract class BaseViewHolder<in T : BaseAdapterItem>(val _binding: ViewBinding) :
            RecyclerView.ViewHolder(_binding.root) {

        abstract fun bind(item: T)
    }

    abstract inner class BaseDiffUtilCallback<T>(protected val newList: List<T>) :
            DiffUtil.Callback() {

        override fun getOldListSize() = items.size

        override fun getNewListSize() = newList.size
    }
}
