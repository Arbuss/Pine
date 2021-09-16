package com.rosberry.pine.ui.base

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.domain.ImageInteractor
import com.rosberry.pine.navigation.Screens
import com.rosberry.pine.ui.fullscreen.FullscreenImage
import com.rosberry.pine.ui.image.ImageError
import com.rosberry.pine.ui.image.ImageItem
import com.rosberry.pine.ui.image.OnImageClickListener
import com.rosberry.pine.util.BlurHashDecoder
import com.rosberry.pine.util.FileUtil
import com.rosberry.pine.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.io.File

abstract class ListedViewModel(router: Router, private val imageInteractor: ImageInteractor) : BaseViewModel(router),
                                                                                               OnImageClickListener {

    private val photos = mutableListOf<Image>()

    private var pagingJob: Job? = null

    protected val _newPage = MutableStateFlow(listOf<ImageItem>())
    val newPage: StateFlow<List<ImageItem>> = _newPage

    protected val _error = MutableStateFlow<ImageError>(ImageError.NoError())
    val error: StateFlow<ImageError> = _error

    protected val _showLoading = MutableStateFlow(false)
    val showLoading: StateFlow<Boolean> = _showLoading

    protected val _clearImageListEvent = MutableStateFlow(false)
    val clearImageListEvent: StateFlow<Boolean> = _clearImageListEvent

    protected val _cachedPhotosList = MutableStateFlow<List<ImageItem>>(emptyList())
    val cachedPhotosList: StateFlow<List<ImageItem>> = _cachedPhotosList

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
            viewModelScope.launch(Dispatchers.IO) {
                _cachedPhotosList.value = photos.map { castImageToAdapterItem(it) }
            }
        }
    }

    open fun loadNewPage() {
        if (!isLoading) {
            getPage()
            isLoading = true
            _showLoading.value = true
        }
    }

    fun onPause() {
        pagingJob?.cancel()
        isLoading = false
    }

    override fun onImageClick(imageId: String) {
        val savedImage = photos.find { it.id == imageId }
        savedImage?.let { image ->
            val fullscreenImage = FullscreenImage(
                    image.id,
                    image.urls.full,
                    image.urls.thumb,
                    image.urls.raw,
                    image.description
            )
            router.navigateTo(Screens.FullscreenImage(fullscreenImage))
        }
    }

    protected open fun getPage() {
        pagingJob = viewModelScope.launch(Dispatchers.IO) {
            val newPhotos = imageInteractor.getPage(currentPage + 1, 10)
            responseResultHandling(newPhotos)
        }
    }

    protected open suspend fun responseResultHandling(resource: Resource<List<Image>>) {
        when (resource) {
            is Resource.Success -> {
                val rawList = resource.item.filter { newPhoto ->
                    !photos.any { oldPhoto -> newPhoto.id == oldPhoto.id }
                }
                val resultList = rawList
                    .map { castImageToAdapterItem(it) }
                    .toMutableList()

                _newPage.value = resultList
                photos.addAll(rawList)
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

        var blurHash: Bitmap? = null

        if (cacheDir != null && !FileUtil.isFileExist(cacheDir!!, image.id)) {
            blurHash = BlurHashDecoder.decode(image.blurHash, screenWidth!! / 20, imageHeight / 20,
                    bitmapConfig = Bitmap.Config.RGB_565)
        }

        var blurHashUri: String? = null

        if (cacheDir != null) {
            blurHashUri = if (blurHash == null || FileUtil.isFileExist(cacheDir!!, image.id)) {
                File(cacheDir, image.id).absolutePath
            } else {
                FileUtil.writeBitmap(cacheDir!!, image.id, blurHash)
            }
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