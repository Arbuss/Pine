package com.rosberry.pine.ui.feed

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rosberry.pine.databinding.FragmentFeedBinding
import com.rosberry.pine.ui.base.ObservableBaseFragment
import com.rosberry.pine.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : ObservableBaseFragment<FragmentFeedBinding>() {

    private val viewModel: FeedViewModel by viewModels()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFeedBinding? =
            FragmentFeedBinding.inflate(inflater, container, false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding?.feedList?.adapter = FeedAdapter()

        viewModel.init(getScreenWidth())

        return binding?.root
    }

    override fun setObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newPage.collect { uiState ->
                    when (uiState) {
                        is Resource.Success -> {
                            (binding?.feedList?.adapter as? FeedAdapter)?.addItems(uiState.item)
                        }
                        is Resource.Error -> {
                        }
                    }
                }
            }
        }
    }

    private fun getScreenWidth(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }
}