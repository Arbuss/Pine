package com.rosberry.pine.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.rosberry.pine.R
import com.rosberry.pine.databinding.ViewFeedBinding
import com.rosberry.pine.extension.getScreenWidth
import com.rosberry.pine.ui.image.ImageAdapter
import com.rosberry.pine.ui.image.ImageError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class ListedFragment<VB : ViewBinding> : BaseFragment<VB>() {

    protected abstract val viewModel: ListedViewModel

    private var feedViewBinding: ViewFeedBinding? = null

    protected val imageList: RecyclerView?
        get() = feedViewBinding?.imageList
    protected val errorTitleView: TextView?
        get() = feedViewBinding?.errorTitle
    protected val errorBodyView: TextView?
        get() = feedViewBinding?.errorBody

    protected val imageAdapter: ImageAdapter?
        get() = imageList?.adapter as? ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        feedViewBinding = ViewFeedBinding.bind(binding!!.root)
        imageList?.adapter = ImageAdapter()

        viewModel.init(getScreenWidth(), context?.cacheDir)

        setScrollListener()

        return binding?.root
    }

    protected open fun showError(errorTitle: String, errorBody: String) {
        errorTitleView?.isVisible = true
        errorTitleView?.text = errorTitle

        errorBodyView?.isVisible = true
        errorBodyView?.text = errorBody
    }

    protected open fun hideError() {
        errorTitleView?.isVisible = false
        errorBodyView?.isVisible = false
    }

    protected open fun setObservers() {
        setErrorObservers()
        setPagingObservers()
        setLoadingObservers()
    }

    protected open fun setErrorObservers() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { error ->
                    when (error) {
                        is ImageError.NoConnection -> {
                            if (viewModel.currentPage != 0) {
                                showSnackbar(R.string.snackbar_no_connection_title,
                                        R.string.snackbar_no_connection_action) {
                                    viewModel.loadNewPage()
                                }
                            } else {
                                showError(getString(R.string.error_no_connection_title),
                                        getString(R.string.error_no_connection_body))
                            }
                        }
                        is ImageError.ServerError -> {
                            showError(getString(R.string.error_server_title),
                                    getString(R.string.error_server_body))
                        }
                        is ImageError.NothingFound -> {
                            showError(getString(R.string.error_nothing_found_title),
                                    getString(R.string.error_nothing_found_body))
                        }
                        is ImageError.NoError -> {
                            hideError()
                        }
                    }
                }
            }
        }
    }

    protected open fun setPagingObservers() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newPage.collect { newPage ->
                    imageAdapter?.addItems(newPage)
                }
            }
        }
    }

    protected open fun setLoadingObservers() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showLoading.collect { isLoading ->
                    if (isLoading) {
                        imageAdapter?.startProgressBar()
                    } else {
                        imageAdapter?.stopProgressBar()
                    }
                }
            }
        }
    }

    protected open fun onImageListEnded() {
        viewModel.loadNewPage()
    }

    private fun setScrollListener() {
        imageList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                val adapterItemsCount = imageAdapter?.itemCount ?: 0
                if (lastVisiblePosition + 4 >= adapterItemsCount) {
                    onImageListEnded()
                }
            }
        })
    }
}