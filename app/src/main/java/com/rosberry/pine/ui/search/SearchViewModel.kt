package com.rosberry.pine.ui.search

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.domain.SearchInteractor
import com.rosberry.pine.ui.base.BaseViewModel
import com.rosberry.pine.ui.feed.ImageItem
import com.rosberry.pine.util.BlurHashDecoder
import com.rosberry.pine.util.FileUtil
import com.rosberry.pine.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(router: Router, private val searchInteractor: SearchInteractor) :
        BaseViewModel(router) {

    private val _newPage = MutableStateFlow(mutableListOf<ImageItem>())
    val newPage = _newPage

    private var isSearching = false
    private var currentPage = 0

    private var screenWidth = 1
    private var cacheDir: File? = null

    fun init(screenWidth: Int, cacheDir: File?) {
        this.screenWidth = screenWidth
        this.cacheDir = cacheDir
    }

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("###SEARCH", query)
            handleResponse(searchInteractor.getSearchResult(query.trim(), currentPage + 1, 10))
        }
    }

    private fun handleResponse(resource: Resource<List<Image>>) {
        when (resource) {
            is Resource.Success -> {
                viewModelScope.launch(Dispatchers.IO) {
                    newPage.value = resource.item.map { castImageToAdapterItem(it) }
                        .toMutableList()
                }
                isSearching = false
            }
            is Resource.Error -> {
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

        Log.d("###SEARCH", "image ${image.id}")
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