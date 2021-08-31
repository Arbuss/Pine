package com.rosberry.pine.ui.feed

import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.domain.ImageInteractor
import com.rosberry.pine.ui.base.BaseViewModel
import com.rosberry.pine.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(router: Router, private val imageInteractor: ImageInteractor) :
        BaseViewModel(router) {

    private val photos = mutableListOf<Image>()

    private val _newPage = MutableStateFlow(Resource.Success(listOf<FeedItem>()))
    val newPage: StateFlow<Resource<List<FeedItem>>> = _newPage

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val newPhotos = imageInteractor.getPage(1, 10)
            photos.addAll(photos)

            if (newPhotos is Resource.Success) {
                _newPage.value = Resource.Success(newPhotos.item.map { image: Image ->
                    FeedItem(image.id,
                            image.description ?: "",
                            image.urls.small,
                            image.width,
                            image.height,
                            image.isLiked)
                })
            }
        }
    }
}