package com.rosberry.pine.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rosberry.pine.databinding.FragmentFeedBinding
import com.rosberry.pine.ui.base.BaseFragment

class FeedFragment : BaseFragment<FragmentFeedBinding>() {

    override fun setTitle() {}

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFeedBinding? =
            FragmentFeedBinding.inflate(inflater, container, false)
}