package com.rosberry.pine.ui.feed

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.rosberry.pine.databinding.FragmentFeedBinding
import com.rosberry.pine.ui.base.ObservableBaseFragment
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
        binding?.feedList?.itemAnimator = null

        viewModel.init(getScreenWidth())

        setupListenerPostListScroll()

        return binding?.root
    }

    override fun setObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newPage.collect { newPage ->
                    (binding?.feedList?.adapter as? FeedAdapter)?.addItems(newPage)
                    Toast.makeText(context, "Items added", Toast.LENGTH_SHORT)
                        .show()

                }

                viewModel.error.collect { error ->
                    when(error) {
                        FeedError.NO_CONNECTION -> {}
                        FeedError.SERVER_ERROR -> {}
                        FeedError.NOTHING_FOUND -> {}
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