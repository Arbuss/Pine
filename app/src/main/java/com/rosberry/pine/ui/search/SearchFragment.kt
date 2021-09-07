package com.rosberry.pine.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rosberry.pine.R
import com.rosberry.pine.databinding.FragmentSearchBinding
import com.rosberry.pine.extension.getScreenWidth
import com.rosberry.pine.ui.base.BaseFragment
import com.rosberry.pine.ui.feed.ImageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private val viewModel: SearchViewModel by viewModels()

    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newPage.collect {
                    Log.d("###SEARCH", "collected")
                    (binding?.imageList?.adapter as? ImageAdapter)?.addItems(it)
                    binding?.searchList?.isVisible = false
                    //                    (binding?.searchList?.adapter as? SearchAdapter)?.addItems(it)
                }
            }
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchBinding? =
            FragmentSearchBinding.inflate(inflater, container, false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding?.searchList?.adapter = SearchAdapter()
        binding?.imageList?.adapter = ImageAdapter()

        viewModel.init(getScreenWidth(), context?.cacheDir)

        binding?.searchField?.doOnTextChanged { text, _, _, _ ->
            binding?.clearButton?.isVisible = text?.isNotEmpty() == true
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                delay(300)
                if (text?.length?.compareTo(3) ?: 0 > 0) {
                    viewModel.search(text.toString())
                }
                Log.d("###SEARCH", "doOnTextChanged: $text")
            }
        }

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
}