package com.rosberry.pine.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.rosberry.pine.databinding.FragmentFeedBinding
import com.rosberry.pine.ui.base.ListedFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : ListedFragment<FragmentFeedBinding>() {

    override val viewModel: FeedViewModel by viewModels()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFeedBinding? =
            FragmentFeedBinding.inflate(inflater, container, false)

}