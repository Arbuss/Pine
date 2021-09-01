package com.rosberry.pine.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.rosberry.pine.ui.main.MainFragment
import com.rosberry.pine.ui.splash.SplashFragment

object Screens {

    val Splash get() = FragmentScreen { SplashFragment() }

    val Main get() = FragmentScreen { MainFragment() }
}