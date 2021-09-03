package com.rosberry.pine.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.rosberry.pine.R
import com.rosberry.pine.databinding.FragmentFeedBinding
import com.rosberry.pine.extension.getScreenWidth
import com.rosberry.pine.ui.base.ObservableBaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
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
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newPage.collect { newPage ->
                    feedAdapter?.addItems(newPage)
                    Toast.makeText(context, "Items added", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { error ->
                    when (error) {
                        is FeedError.NoConnection -> {
                            showError(getString(R.string.error_no_connection_title),
                                    getString(R.string.error_no_connection_body))
                        }
                        is FeedError.ServerError -> {
                            showError(getString(R.string.error_server_title),
                                    getString(R.string.error_server_body))
                        }
                        is FeedError.NothingFound -> {
                        }
                        is FeedError.NoError -> {
                            hideError()
                        }
                    }
                }
            }
        }
    }

    private fun showError(errorTitle: String, errorBody: String) {
        binding?.errorTitle?.isVisible = true
        binding?.errorTitle?.text = errorTitle

        binding?.errorBody?.isVisible = true
        binding?.errorBody?.text = errorBody
    }

    private fun hideError() {
        binding?.errorTitle?.isVisible = false
        binding?.errorBody?.isVisible = false
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