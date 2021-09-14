package com.rosberry.pine.ui.main

import com.github.terrakok.cicerone.Router
import com.rosberry.pine.navigation.Screens
import com.rosberry.pine.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(router: Router): BaseViewModel(router) {

    fun openSearchScreen() {
        router.navigateTo(Screens.Search)
    }
}