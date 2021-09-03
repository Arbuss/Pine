package com.rosberry.pine.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class ObservableBaseFragment<VB : ViewBinding> : BaseFragment<VB>() {

    abstract fun setObservers()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setObservers()
        return binding?.root
    }
}