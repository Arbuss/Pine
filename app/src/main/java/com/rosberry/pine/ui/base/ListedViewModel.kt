package com.rosberry.pine.ui.base

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.domain.ImageInteractor
import com.rosberry.pine.ui.image.ImageError
import com.rosberry.pine.ui.image.ImageItem
import com.rosberry.pine.util.BlurHashDecoder
import com.rosberry.pine.util.FileUtil
import com.rosberry.pine.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.io.File

abstract class ListedViewModel(router: Router, private val imageInteractor: ImageInteractor) : BaseViewModel(router) {

    private val photos = mutableListOf<ImageItem>()

    protected val _newPage = MutableStateFlow(listOf<ImageItem>())
    val newPage: StateFlow<List<ImageItem>> = _newPage

    protected val _error = MutableStateFlow<ImageError>(ImageError.NoError())
    val error: StateFlow<ImageError> = _error

    protected val _showLoading = MutableStateFlow(false)
    val showLoading: StateFlow<Boolean> = _showLoading

    protected var isLoading = false

    private var screenWidth: Int? = null
    private var cacheDir: File? = null

    var currentPage: Int = 0
        protected set

    open fun init(screenWidth: Int, cacheDir: File?) {
        this.screenWidth = screenWidth
        this.cacheDir = cacheDir
        if (photos.isEmpty()) {
            loadNewPage()
        } else {
            _newPage.value = photos
        }
    }

    open fun loadNewPage() {
        if (!isLoading) {
            getPage()
            isLoading = true
            _showLoading.value = true
        }
    }

    protected open fun getPage() {
        viewModelScope.launch(Dispatchers.IO) {
            val newPhotos = imageInteractor.getPage(currentPage + 1, 10)
            responseResultHandling(newPhotos)
        }
    }

    protected open suspend fun responseResultHandling(resource: Resource<List<Image>>) {
        when (resource) {
            is Resource.Success -> {
                val resultList = resource.item.filter { newPhoto ->
                    !photos.any { oldPhoto -> newPhoto.id == oldPhoto.id }
                }
                    .map { castImageToAdapterItem(it) }
                    .toMutableList()

                _newPage.value = resultList
                photos.addAll(resultList)
                currentPage++
                isLoading = false
                _showLoading.value = false

                _error.value = ImageError.NoError()
            }
            is Resource.Error -> {
                isLoading = false
                _showLoading.value = false
                _error.value = resource.exception as ImageError
            }
        }
    }

    private suspend fun castImageToAdapterItem(image: Image): ImageItem {
        yield()
        val (imageWidth, imageHeight) = calcImageSize(image.width, image.height)

        val blurHash = BlurHashDecoder.decode(image.blurHash, screenWidth!!, imageHeight,
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
        val multiplier = width / screenWidth!! + 1

        val imageWidth = screenWidth!!
        val imageHeight = height / multiplier
        return imageWidth to imageHeight
    }
}