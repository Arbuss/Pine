package com.rosberry.pine.ui.feed

import com.github.terrakok.cicerone.Router
import com.rosberry.pine.domain.ImageInteractor
import com.rosberry.pine.ui.base.ListedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(router: Router, imageInteractor: ImageInteractor) :
        ListedViewModel(router, imageInteractor) {

}