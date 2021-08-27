package com.rosberry.pine.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.rosberry.pine.R
import com.rosberry.pine.databinding.FragmentMainBinding
import com.rosberry.pine.ui.base.BaseFragment
import com.rosberry.pine.ui.favorite.FavoriteFragment
import com.rosberry.pine.ui.feed.FeedFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {

    private val fragments = arrayOf(
            FeedFragment(), FavoriteFragment()
    )

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding? =
            FragmentMainBinding.inflate(inflater, container, false)

    override fun setTitle() {
        binding?.topBar?.appName?.text = getText(R.string.main_fragment_app_bar_title)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initViewPager()
        initTabs()
        return binding?.root
    }

    private fun initViewPager() {
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }

        binding?.viewPager?.adapter = adapter
    }

    private fun initTabs() {
        TabLayoutMediator(binding!!.tabs, binding!!.viewPager) { tab, position ->
            tab.text = requireContext().resources.getStringArray(R.array.main_fragment_view_pager_tabs)
                .getOrElse(position) { "" }
        }.attach()
    }
}