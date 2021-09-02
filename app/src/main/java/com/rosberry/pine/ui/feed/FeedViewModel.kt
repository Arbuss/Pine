package com.rosberry.pine.ui.feed

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.domain.ImageInteractor
import com.rosberry.pine.ui.base.BaseViewModel
import com.rosberry.pine.util.BlurHashDecoder
import com.rosberry.pine.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.graphics.BitmapFactory




@HiltViewModel
class FeedViewModel @Inject constructor(router: Router, private val imageInteractor: ImageInteractor) :
        BaseViewModel(router) {

    private val photos = mutableListOf<Image>()

    private val _newPage = MutableStateFlow(Resource.Success(listOf<FeedItem>()))
    val newPage: StateFlow<Resource<List<FeedItem>>> = _newPage

    private var isLoading = false
    private var screenWidth = 1

    private var picCount = 0

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
            val newPhotos = imageInteractor.getPage(currentPage + 1, 10)
            photos.addAll(photos)
            responseResultHandling(newPhotos)
        }
    }

    private suspend fun responseResultHandling(newPhotos: Resource<List<Image>>) {
        when (newPhotos) {
            is Resource.Success -> {
                newPhotos.item.forEach {
                    addItem(it)
                }
                currentPage++
                isLoading = false
            }
            is Resource.Error -> {
            }
        }
    }

    private suspend fun addItem(image: Image) {
        val (imageWidth, imageHeight) = calcImageSize(image.width, image.height)

        val blurHash = BlurHashDecoder.decode(image.blurHash, screenWidth, imageHeight, bitmapConfig = Bitmap.Config.RGB_565)

        val item = FeedItem(image.id,
                image.description ?: "",
                image.urls.small,
                imageWidth,
                imageHeight,
                blurHash,
                image.isLiked)
        Log.d("ItemCount", "count = ${++picCount}")

        _newPage.value = Resource.Success(listOf(item))
    }

    private fun calcImageSize(width: Int, height: Int): Pair<Int, Int> {
        val multiplier = width / screenWidth + 1

        val imageWidth = screenWidth
        val imageHeight = height / multiplier
        return imageWidth to imageHeight
    }
}