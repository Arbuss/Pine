package com.rosberry.pine.ui.favorite

import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.domain.FavoriteInteractor
import com.rosberry.pine.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
        router: Router,
        private val favoriteInteractor: FavoriteInteractor
) :
        BaseViewModel(router) {

    private var images: Flow<List<Image>>

    init {
        images = MutableStateFlow(emptyList())
        viewModelScope.launch(Dispatchers.IO) {
            images = favoriteInteractor.getAllLikedImagesInFlow()
            subscribe()
        }
    }

    private fun subscribe() {
        viewModelScope.launch(Dispatchers.IO) {
            images.collect { }
        }
    }
}