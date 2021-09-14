package com.rosberry.pine.ui.fullscreen

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.rosberry.pine.ui.base.BaseViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullscreenImageViewModel @Inject constructor(router: Router) : BaseViewModel(router) {

    var image: FullscreenImage? = null

    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap: StateFlow<Bitmap?> = _bitmap

    fun downloadImage() {
        viewModelScope.launch(Dispatchers.IO) {
            _bitmap.value = Picasso.get()
                .load(image?.rawImageUrl)
                .config(Bitmap.Config.RGB_565)
                .get()
        }
    }
}