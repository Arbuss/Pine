package com.rosberry.pine.ui.feed

import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.rosberry.pine.data.repository.RepositoryError
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.domain.ImageInteractor
import com.rosberry.pine.ui.base.BaseViewModel
import com.rosberry.pine.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import xyz.belvi.blurhash.BlurHashDecoder
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(router: Router, private val imageInteractor: ImageInteractor) :
        BaseViewModel(router) {

    private val photos = mutableListOf<Image>()

    private val _newPage = MutableStateFlow(listOf<FeedItem>())
    val newPage: StateFlow<List<FeedItem>> = _newPage

    private val _error = MutableStateFlow(FeedError.NO_ERROR)
    val error = _error

    private var isLoading = false
    private var screenWidth = 1

    var currentPage: Int = 1
        private set

    fun init(screenWidth: Int) {
        this.screenWidth = screenWidth
        loadNewPage()
    }

    fun loadNewPage() {
        if (!isLoading) {
            getPage()
            isLoading = true
        }
    }

    private fun getPage() {
        viewModelScope.launch(Dispatchers.IO) {
            val resource = imageInteractor.getPage(currentPage + 1, 10)
            responseResultHandling(resource)
        }
    }

    private suspend fun responseResultHandling(resource: Resource<List<Image>>) {
        when (resource) {
            is Resource.Success -> {
                _newPage.value = castInteractorItemsToViewItems(resource.item)
                currentPage++
                isLoading = false
            }
            is Resource.Error -> {
                errorHandling(resource.exception)
            }
        }
    }

    private fun errorHandling(exception: Throwable) {
        _error.value = when (exception) {
            is RepositoryError.ServerError -> FeedError.SERVER_ERROR
            is RepositoryError.NoConnectionError -> FeedError.NO_CONNECTION
            is RepositoryError.NothingFound -> FeedError.NOTHING_FOUND
            else -> FeedError.NO_ERROR
        }
    }

    private suspend fun castInteractorItemsToViewItems(items: List<Image>): List<FeedItem> {
        return items.map { image: Image ->
            val multiplier = image.width / screenWidth + 1

            val imageWidth = screenWidth
            val imageHeight = image.height / multiplier

            FeedItem(image.id,
                    image.description ?: "",
                    image.urls.small,
                    imageWidth,
                    imageHeight,
                    BlurHashDecoder.decode(image.blurHash, screenWidth, imageHeight),
                    image.isLiked)
        }
    }
}