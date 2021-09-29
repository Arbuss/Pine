package com.rosberry.pine.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.ui.fullscreen.FullscreenImage
import com.rosberry.pine.ui.fullscreen.FullscreenImageFragment
import com.rosberry.pine.ui.main.MainFragment
import com.rosberry.pine.ui.search.SearchFragment
import com.rosberry.pine.ui.splash.SplashFragment

object Screens {

    val Splash get() = FragmentScreen { SplashFragment() }

    val Main get() = FragmentScreen { MainFragment() }

    val Search get() = FragmentScreen { SearchFragment() }

    fun FullscreenImage(image: Image) = FragmentScreen { FullscreenImageFragment(image) }
}