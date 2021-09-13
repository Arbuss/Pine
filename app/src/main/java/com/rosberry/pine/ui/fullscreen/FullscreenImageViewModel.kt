package com.rosberry.pine.ui.fullscreen

import com.github.terrakok.cicerone.Router
import com.rosberry.pine.ui.base.BaseViewModel
import javax.inject.Inject

class FullscreenImageViewModel @Inject constructor(router: Router) : BaseViewModel(router) {

    var image: FullscreenImage? = null
}