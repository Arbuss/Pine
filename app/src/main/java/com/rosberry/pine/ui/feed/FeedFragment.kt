package com.rosberry.pine.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.rosberry.pine.databinding.FragmentFeedBinding
import com.rosberry.pine.extension.getScreenWidth
import com.rosberry.pine.ui.base.ObservableBaseFragment
import com.rosberry.pine.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : ObservableBaseFragment<FragmentFeedBinding>() {

    private val viewModel: FeedViewModel by viewModels()
    private val feedAdapter
        get() = binding?.feedList?.adapter as? FeedAdapter

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFeedBinding? =
            FragmentFeedBinding.inflate(inflater, container, false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding?.feedList?.adapter = FeedAdapter()

        viewModel.init(getScreenWidth(), context?.cacheDir)

        setupListenerPostListScroll()

        return binding?.root
    }

    override fun setObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newPage.collect { uiState ->
                    when (uiState) {
                        is Resource.Success -> {
                            feedAdapter?.addItems(uiState.item)
                            Toast.makeText(context, "Items added", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is Resource.Error -> {
                        }
                    }
                }
            }
        }
    }

    private fun setupListenerPostListScroll() {
        val scrollDirectionDown = 1 // Scroll down is +1, up is -1.

        binding?.feedList?.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)

                        if (!recyclerView.canScrollVertically(scrollDirectionDown)
                                && newState == RecyclerView.SCROLL_STATE_IDLE
                        ) {
                            Toast.makeText(context, "End", Toast.LENGTH_SHORT)
                                .show()
                            viewModel.loadNewPage()
                        }
                    }
                })
    }
}