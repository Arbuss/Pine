package com.rosberry.pine.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import com.rosberry.pine.databinding.FragmentSplashBinding
import com.rosberry.pine.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val viewModel: SplashViewModel by viewModels()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSplashBinding? =
            FragmentSplashBinding.inflate(inflater, container, false)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        backgroundPositioning()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.goToNextScreen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun backgroundPositioning() {
        ViewCompat.setOnApplyWindowInsetsListener(binding!!.root as View) { view, insets ->
            view.updateLayoutParams {
                (this as? ViewGroup.MarginLayoutParams)?.topMargin =
                        insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom / 2
            }
            insets
        }
    }
}