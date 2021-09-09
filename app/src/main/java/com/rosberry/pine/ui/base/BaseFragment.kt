package com.rosberry.pine.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    protected var binding: VB? = null // TODO попробовать создавать его тут

    abstract fun createViewBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VB?

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = createViewBinding(inflater, container)

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun showSnackbar(@StringRes titleTextId: Int, @StringRes actionTextId: Int, action: () -> Unit) {
        binding?.root?.let { view ->
            Snackbar.make(view, getString(titleTextId), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionTextId)) { action() }
                .show()
        }
    }
}