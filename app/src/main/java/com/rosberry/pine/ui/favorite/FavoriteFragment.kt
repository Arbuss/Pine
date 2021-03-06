package com.rosberry.pine.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rosberry.pine.databinding.FragmentFavoriteBinding
import com.rosberry.pine.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFavoriteBinding? =
            FragmentFavoriteBinding.inflate(inflater, container, false)
}