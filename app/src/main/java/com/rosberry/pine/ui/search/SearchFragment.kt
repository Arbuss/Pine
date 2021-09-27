package com.rosberry.pine.ui.search

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
class SearchFragment : ListedFragment<FragmentSearchBinding>(), OnSearchItemClickListener {

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

        binding?.searchField?.setOnFocusChangeListener { _, isFocused ->
            onSearchFieldFocusChanged(isFocused)
        }

        binding?.searchField?.setOnEditorActionListener { _, actionId, keyEvent ->
            if (keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(binding?.searchField?.text?.toString() ?: "")
                hideKeyboard()
                binding?.searchField?.clearFocus()
            }
            false
        }

        binding?.searchList?.adapter = SearchHistoryAdapter(this)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(viewModel.imageListIsEmpty()) {
            showKeyboard()
        } else {
            binding?.searchList?.isVisible = false
        }
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
        setSearchListObserver()
        setClearListObserver()
    }

    private fun setSearchListObserver() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchList.collect {
                    (binding?.searchList?.adapter as? SearchHistoryAdapter)?.addItems(it)
                }
            }
        }
    }

    private fun setClearListObserver() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.clearImageListEvent.collect {
                    imageAdapter?.clear()
                }
            }
        }
    }

    private fun showKeyboard() {
        if (binding?.searchField?.requestFocus() == true) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(binding?.searchField, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding?.root?.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun onSearchFieldFocusChanged(isFocused: Boolean) {
        binding?.searchList?.isVisible = isFocused
        if (isFocused) {
            viewModel.fillSearchList()
        }
    }

    override fun onItemClicked(query: String) {
        binding?.searchField?.clearFocus()
        binding?.searchField?.setText(query)
        viewModel.search(query)
        hideKeyboard()
    }
}