package com.rosberry.pine.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rosberry.pine.databinding.ItemSearchBinding
import com.rosberry.pine.ui.base.BaseAdapter

class SearchHistoryAdapter(private val listener: OnSearchItemClickListener) : BaseAdapter<SearchItem>(mutableListOf()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<SearchItem> {
        return SearchViewHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun createDiffUtilCallback(newList: List<SearchItem>) =
            SearchDiffUtilCallback(newList)

    override fun addItems(newItems: List<SearchItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(binding: ItemSearchBinding) : BaseViewHolder<SearchItem>(binding) {

        init {
            binding.root.setOnClickListener {
                listener.onItemClicked(items[layoutPosition].query)
            }
        }

        private val binding: ItemSearchBinding
            get() = _binding as ItemSearchBinding

        override fun bind(item: SearchItem) {
            binding.searchText.text = item.query
        }

    }

    inner class SearchDiffUtilCallback(newList: List<SearchItem>) : BaseDiffUtilCallback<SearchItem>(newList) {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                items[oldItemPosition].query == newList[newItemPosition].query

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                areItemsTheSame(oldItemPosition, newItemPosition)

    }
}