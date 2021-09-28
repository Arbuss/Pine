package com.rosberry.pine.ui.base

import android.os.Bundle
import android.util.Log
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

    protected val imageAdapter: ImageAdapter by lazy { ImageAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.observeLiked()
        viewModel.loadNewPage()
        setObservers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        feedViewBinding = ViewFeedBinding.bind(binding!!.root)
        imageList?.adapter = imageAdapter

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
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.error.collect { error ->
                    when (error) {
                        is ImageError.NoConnection -> {
                            showError(getString(R.string.error_no_connection_title),
                                    getString(R.string.error_no_connection_body))
                        }
                        is ImageError.NoConnectionWithPagination -> {
                            showSnackbar(R.string.snackbar_no_connection_title,
                                    R.string.snackbar_no_connection_action) {
                                viewModel.loadNewPage()
                            }
                        }
                        is ImageError.ServerError -> {
                            showError(getString(R.string.error_server_title),
                                    getString(R.string.error_server_body))
                        }
                        is ImageError.NothingFound -> {
                            viewModel.nothingFoundHappened = true
                            if (viewModel.imageListIsEmpty()) {
                                showError(getString(R.string.error_nothing_found_title),
                                        getString(R.string.error_nothing_found_body))
                            }
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
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.images.collect {
                    Log.d("#Favorites", "collect")
                    imageAdapter.setItems(it)
                }
            }
        }
    }

    protected open fun setLoadingObservers() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isLoading.collect { isLoading ->
                    if (isLoading) {
                        imageAdapter.startProgressBar()
                    } else {
                        imageAdapter.stopProgressBar()
                    }
                }
            }
        }
    }

    protected open fun onImageListEnded() {
        viewModel.loadNewPage()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun setScrollListener() {
        imageList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                val adapterItemsCount = imageAdapter.itemCount
                if (lastVisiblePosition >= 0 && lastVisiblePosition + 4 >= adapterItemsCount) {
                    onImageListEnded()
                }
            }
        })
    }
}