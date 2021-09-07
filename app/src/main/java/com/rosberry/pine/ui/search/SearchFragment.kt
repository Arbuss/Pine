package com.rosberry.pine.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rosberry.pine.R
import com.rosberry.pine.databinding.FragmentSearchBinding
import com.rosberry.pine.extension.getScreenWidth
import com.rosberry.pine.ui.base.BaseFragment
import com.rosberry.pine.ui.feed.FeedError
import com.rosberry.pine.ui.feed.ImageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private val viewModel: SearchViewModel by viewModels()

    private val imageAdapter: ImageAdapter?
        get() = (binding?.imageList?.adapter as? ImageAdapter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchBinding? =
            FragmentSearchBinding.inflate(inflater, container, false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding?.searchList?.adapter = SearchAdapter()
        binding?.imageList?.adapter = ImageAdapter()

        viewModel.init(getScreenWidth(), context?.cacheDir)

        binding?.clearButton?.setOnClickListener {
            binding?.searchField?.editableText?.clear()
        }

        binding?.searchField?.doOnTextChanged { text, _, _, _ ->
            binding?.clearButton?.isVisible = text?.isNotEmpty() == true
        }

        binding?.searchField?.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(binding?.searchField?.text?.toString() ?: "")
            }
            false
        }

        setupScrollListener()

        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setBackgroundDrawableResource(
                R.color.background_color) // TODO возможно что-нибудь другое придумать
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.setBackgroundDrawableResource(
                R.drawable.d_logo_pine_black_nine_patch) // TODO возможно что-нибудь другое придумать
    }

    private fun setupScrollListener() {
        binding?.imageList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                val adapterItemsCount = imageAdapter?.itemCount ?: 0
                if (lastVisiblePosition + 4 >= adapterItemsCount) {
                    viewModel.loadNewPage()
                }
            }
        })
    }

    private fun setObservers() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newPage.collect {
                    imageAdapter?.addItems(it)
                    binding?.searchList?.isVisible = false
                }
            }
        }

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

        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.clearImageListEvent.collect { isLoading ->
                    imageAdapter?.clear()
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
                            showError(getString(R.string.error_nothing_found_title),
                                    getString(R.string.error_nothing_found_body))
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
}