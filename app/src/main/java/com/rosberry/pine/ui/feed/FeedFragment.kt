package com.rosberry.pine.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.rosberry.pine.databinding.FragmentFeedBinding
import com.rosberry.pine.ui.base.ListedFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : ListedFragment<FragmentFeedBinding>() {

    override val viewModel: FeedViewModel by viewModels()

    override val imageList: RecyclerView?
        get() = binding?.imageList
    override val errorTitleView: TextView?
        get() = binding?.errorTitle
    override val errorBodyView: TextView?
        get() = binding?.errorBody

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFeedBinding? =
            FragmentFeedBinding.inflate(inflater, container, false)
}