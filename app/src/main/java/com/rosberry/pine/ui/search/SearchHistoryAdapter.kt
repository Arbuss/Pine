package com.rosberry.pine.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rosberry.pine.databinding.ItemSearchBinding

class SearchHistoryAdapter(
        private val listener: OnSearchItemClickListener,
        private val items: MutableList<SearchItem> = mutableListOf()
) : RecyclerView.Adapter<SearchHistoryAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun addItems(newItems: List<SearchItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(private val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onItemClicked(items[layoutPosition].query)
            }
        }

        fun bind(item: SearchItem) {
            binding.searchText.text = item.query
        }

    }
}