package com.rosberry.pine.ui.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.rosberry.pine.data.repository.model.DTOImage
import com.rosberry.pine.domain.ImageInteractor
import com.rosberry.pine.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(router: Router, private val imageInteractor: ImageInteractor) :
        BaseViewModel(router) {

    private val photos = mutableListOf<DTOImage>()

    val newPage = MutableLiveData(listOf<FeedItem>())

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val newPhotos = imageInteractor.getPage(1, 10)
            photos.addAll(photos)
            newPage.postValue(newPhotos.map { dtoImage: DTOImage ->
                FeedItem(dtoImage.id, dtoImage.description ?: "", dtoImage.urls.small)
            })
        }
    }
}