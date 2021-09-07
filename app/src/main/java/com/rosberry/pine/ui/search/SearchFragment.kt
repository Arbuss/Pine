package com.rosberry.pine.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rosberry.pine.R
import com.rosberry.pine.databinding.FragmentSearchBinding
import com.rosberry.pine.ui.activity.AppActivity
import com.rosberry.pine.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newPage.collect {
//                    (binding?.searchList?.adapter as? SearchAdapter)?.addItems(it)
                }
            }
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchBinding? =
            FragmentSearchBinding.inflate(inflater, container, false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        requireActivity().window.setBackgroundDrawableResource(R.color.background_color) // TODO возможно что-нибудь другое придумать
        binding?.searchList?.adapter = SearchAdapter()

        binding?.searchField?.doOnTextChanged { text, _, _, _ ->
            binding?.clearButton?.isVisible = text?.isNotEmpty() == true
            if (text?.length?.compareTo(3) ?: 0 > 0) {
                viewModel.search(text.toString())
            }
        }

        binding?.searchField.doAfterTextChanged {  }

        return binding?.root
    }
}