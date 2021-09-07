package com.rosberry.pine.ui.feed

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.domain.ImageInteractor
import com.rosberry.pine.ui.base.BaseViewModel
import com.rosberry.pine.util.BlurHashDecoder
import com.rosberry.pine.util.FileUtil
import com.rosberry.pine.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(router: Router, private val imageInteractor: ImageInteractor) :
        BaseViewModel(router) {

    private val photos = mutableListOf<Image>()

    private val _newPage = MutableStateFlow(listOf<ImageItem>())
    val newPage: StateFlow<List<ImageItem>> = _newPage

    private val _error = MutableStateFlow<FeedError>(FeedError.NoError())
    val error = _error

    private val _showLoading = MutableStateFlow(false)
    val showLoading = _showLoading

    private var isLoading = false

    private var screenWidth = 1
    private var cacheDir: File? = null

    var currentPage: Int = 1
        private set

    fun init(screenWidth: Int, cacheDir: File?) {
        this.screenWidth = screenWidth
        this.cacheDir = cacheDir
        loadNewPage()
    }

    fun loadNewPage() {
        if (!isLoading) {
            getPage()
            isLoading = true
            _showLoading.value = isLoading
        }
    }

    private fun getPage() {
        viewModelScope.launch(Dispatchers.IO) {
            val newPhotos = imageInteractor.getPage(currentPage + 1, 10)
            photos.addAll(photos)
            responseResultHandling(newPhotos)
        }
    }

    private suspend fun responseResultHandling(resource: Resource<List<Image>>) {
        when (resource) {
            is Resource.Success -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _newPage.value = resource.item.map { castImageToAdapterItem(it) }
                        .toMutableList()
                    currentPage++
                    isLoading = false
                    _showLoading.value = isLoading
                }
                error.value = FeedError.NoError()
            }
            is Resource.Error -> {
                _showLoading.value = false
                _error.value = resource.exception as FeedError
            }
        }
    }

    private suspend fun castImageToAdapterItem(image: Image): ImageItem {
        val (imageWidth, imageHeight) = calcImageSize(image.width, image.height)

        val blurHash = BlurHashDecoder.decode(image.blurHash, screenWidth, imageHeight,
                bitmapConfig = Bitmap.Config.RGB_565)

        var blurHashUri: String? = null

        if (cacheDir != null && blurHash != null) {
            blurHashUri = FileUtil.writeBitmap(cacheDir!!, image.id, blurHash)
        }

        return ImageItem(image.id,
                image.description ?: "",
                image.urls.small,
                imageWidth,
                imageHeight,
                blurHashUri,
                image.isLiked)
    }

    private fun calcImageSize(width: Int, height: Int): Pair<Int, Int> {
        val multiplier = width / screenWidth + 1

        val imageWidth = screenWidth
        val imageHeight = height / multiplier
        return imageWidth to imageHeight
    }
}