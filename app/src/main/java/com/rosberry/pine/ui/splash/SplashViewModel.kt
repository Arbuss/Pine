package com.rosberry.pine.ui.splash

import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.rosberry.pine.navigation.Screens
import com.rosberry.pine.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(router: Router): BaseViewModel(router) {

    fun goToNextScreen() {
        viewModelScope.launch {
            delay(3000)
            router.navigateTo(Screens.Main)
        }
    }
}