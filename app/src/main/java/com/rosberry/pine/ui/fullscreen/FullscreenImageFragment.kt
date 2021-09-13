package com.rosberry.pine.ui.fullscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.rosberry.pine.databinding.FragmentImageBinding
import com.rosberry.pine.ui.base.BaseFragment

class FullscreenImageFragment() : BaseFragment<FragmentImageBinding>() {

    private val IMAGE_KEY = "image_key"

    private val viewModel: FullscreenImageViewModel by viewModels()

    constructor(image: FullscreenImage) : this() {
        arguments = bundleOf(
                IMAGE_KEY to image
        )
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentImageBinding? =
            FragmentImageBinding.inflate(inflater, container, false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel.image = requireArguments().getParcelable(IMAGE_KEY)
        super.onCreateView(inflater, container, savedInstanceState)
        return binding?.root
    }
}