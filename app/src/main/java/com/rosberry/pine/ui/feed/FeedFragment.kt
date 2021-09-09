package com.rosberry.pine.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.rosberry.pine.databinding.FragmentFeedBinding
import com.rosberry.pine.databinding.ViewFeedBinding
import com.rosberry.pine.ui.base.ListedFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : ListedFragment<FragmentFeedBinding>() {

    override val viewModel: FeedViewModel by viewModels()

    var feedViewBinding: ViewFeedBinding? = null

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFeedBinding? {
        val bnd = FragmentFeedBinding.inflate(inflater, container, false)
        feedViewBinding = ViewFeedBinding.bind(bnd.root)
        return bnd
    }
}