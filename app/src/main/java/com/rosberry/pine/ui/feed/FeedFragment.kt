package com.rosberry.pine.ui.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rosberry.pine.R
import com.rosberry.pine.databinding.FragmentFeedBinding
import com.rosberry.pine.extension.getScreenWidth
import com.rosberry.pine.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : BaseFragment<FragmentFeedBinding>() {

    private val viewModel: FeedViewModel by viewModels()
    private val feedAdapter
        get() = binding?.feedList?.adapter as? ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFeedBinding? =
            FragmentFeedBinding.inflate(inflater, container, false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding?.feedList?.adapter = ImageAdapter()

        viewModel.init(getScreenWidth(), context?.cacheDir)

        setupScrollListener()

        return binding?.root
    }

    private fun setObservers() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newPage.collect { newPage ->
                    feedAdapter?.addItems(newPage)
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

        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showLoading.collect { isLoading ->
                    if (isLoading) {
                        feedAdapter?.startProgressBar()
                    } else {
                        feedAdapter?.stopProgressBar()
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

    private fun setupScrollListener() {
        binding?.feedList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                val adapterItemsCount = feedAdapter?.itemCount ?: 0
                if (lastVisiblePosition + 4 >= adapterItemsCount) {
                    viewModel.loadNewPage()
                }
            }
        })
    }
}