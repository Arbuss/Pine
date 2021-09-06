package com.rosberry.pine.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rosberry.pine.databinding.ItemSearchBinding
import com.rosberry.pine.ui.base.BaseAdapter

class SearchAdapter : BaseAdapter<SearchItem, ItemSearchBinding>(mutableListOf()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<SearchItem, ItemSearchBinding> {
        return SearchViewHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun createDiffUtilCallback(newList: List<SearchItem>) =
            SearchDiffUtilCallback(newList)

    inner class SearchViewHolder(binding: ItemSearchBinding) : BaseViewHolder<SearchItem, ItemSearchBinding>(binding) {

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