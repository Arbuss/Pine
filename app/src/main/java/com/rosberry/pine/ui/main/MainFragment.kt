package com.rosberry.pine.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rosberry.pine.R
import com.rosberry.pine.databinding.FragmentMainBinding
import com.rosberry.pine.ui.base.BaseFragment

class MainFragment : BaseFragment<FragmentMainBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding? =
            FragmentMainBinding.inflate(inflater, container, false)

    override fun setTitle() {
        binding?.topBar?.appName?.text = getText(R.string.main_fragment_app_bar_title)
    }
}