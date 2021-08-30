package com.rosberry.pine.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.rosberry.pine.databinding.FragmentFeedBinding
import com.rosberry.pine.ui.base.ObservableBaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : ObservableBaseFragment<FragmentFeedBinding>() {

    private val viewModel: FeedViewModel by viewModels()

    override fun setTitle() {}

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFeedBinding? =
            FragmentFeedBinding.inflate(inflater, container, false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding?.feedList?.adapter = FeedAdapter()
        viewModel.init()
        return binding?.root
    }

    override fun setObservers() {
        viewModel.newPage.observe(viewLifecycleOwner, Observer { newItems ->
            (binding?.feedList?.adapter as? FeedAdapter)?.addItems(newItems)
        })
    }
}