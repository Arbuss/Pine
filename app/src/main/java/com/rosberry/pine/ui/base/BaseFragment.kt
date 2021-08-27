package com.rosberry.pine.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    abstract fun setTitle()

    abstract fun createViewBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VB?

    protected var binding: VB? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = createViewBinding(inflater, container)
        setTitle()
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}