package com.rosberry.pine.ui.fullscreen

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.domain.FavoriteInteractor
import com.rosberry.pine.ui.base.BaseViewModel
import com.rosberry.pine.util.FileUtil
import com.squareup.picasso.Picasso
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FullscreenImageViewModel @Inject constructor(
        router: Router,
        private val favoriteInteractor: FavoriteInteractor
) :
        BaseViewModel(router) {

    var image: Image? = null
        set(value) {
            _imageIsLiked.value = value?.isLiked ?: false
            field = value
        }

    private val _imageIsLiked = MutableStateFlow(false)
    val imageIsLiked: StateFlow<Boolean> = _imageIsLiked

    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap: StateFlow<Bitmap?> = _bitmap

    private val _sharingItemAddress = MutableStateFlow<String?>(null)
    val sharingItemAddress: StateFlow<String?> = _sharingItemAddress

    fun downloadImage() {
        viewModelScope.launch(Dispatchers.IO) {
            _bitmap.value = getBitmap()
        }
    }

    fun saveBitmapToCache(cacheDir: File) {
        viewModelScope.launch(Dispatchers.IO) {
            image?.let {
                _sharingItemAddress.value = null
                _sharingItemAddress.value = FileUtil.writeBitmap(cacheDir, it.id + "raw", getBitmap())
            }
        }
    }

    fun like() {
        image?.let { image ->
            viewModelScope.launch(Dispatchers.IO) {
                this@FullscreenImageViewModel.image = if (image.isLiked) {
                    favoriteInteractor.unlike(image)
                    image.copy(isLiked = false)
                } else {
                    favoriteInteractor.like(image)
                    image.copy(isLiked = true)
                }
            }
        }
    }

    private suspend fun getBitmap(): Bitmap {
        return Picasso.get()
            .load(image?.urls?.raw)
            .config(Bitmap.Config.RGB_565)
            .get()
    }
}