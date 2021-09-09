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
import com.rosberry.pine.R
import com.rosberry.pine.databinding.FragmentSearchBinding
import com.rosberry.pine.ui.base.ListedFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : ListedFragment<FragmentSearchBinding>() {

    override val viewModel: SearchViewModel by viewModels()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchBinding? =
            FragmentSearchBinding.inflate(inflater, container, false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding?.clearButton?.setOnClickListener {
            binding?.searchField?.editableText?.clear()
        }

        binding?.backButton?.setOnClickListener {
            viewModel.onBackPressed()
        }

        binding?.searchField?.doOnTextChanged { text, _, _, _ ->
            binding?.clearButton?.isVisible = text?.isNotEmpty() == true
        }

        binding?.searchField?.setOnEditorActionListener { _, actionId, keyEvent ->
            if (keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(binding?.searchField?.text?.toString() ?: "")
            }
            false
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

    override fun setObservers() {
        super.setObservers()
        setListClearObserver()
    }

    private fun setListClearObserver() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.clearImageListEvent.collect {
                    imageAdapter?.clear()
                }
            }
        }
    }
}