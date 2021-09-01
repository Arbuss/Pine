package com.rosberry.pine.ui.activity

import com.github.terrakok.cicerone.Router
import com.rosberry.pine.navigation.Screens
import com.rosberry.pine.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(router: Router) : BaseViewModel(router) {

    fun showSplash() {
        router.newRootScreen(Screens.Splash)
    }
}