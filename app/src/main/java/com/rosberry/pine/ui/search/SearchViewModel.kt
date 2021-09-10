package com.rosberry.pine.ui.search

import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.rosberry.pine.domain.ImageInteractor
import com.rosberry.pine.domain.SearchInteractor
import com.rosberry.pine.ui.base.ListedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
        router: Router,
        private val searchInteractor: SearchInteractor,
        imageInteractor: ImageInteractor
) : ListedViewModel(router, imageInteractor) {

    private var job: Job? = null

    private val _clearImageListEvent = MutableStateFlow(false)
    val clearImageListEvent: StateFlow<Boolean> = _clearImageListEvent

    var lastQuery: String? = null

    override fun loadNewPage() {
        if (!isLoading && lastQuery != null) {
            search(lastQuery ?: "")
        }
    }

    fun search(query: String) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            if (lastQuery != query) {
                currentPage = 0
                _clearImageListEvent.value = !_clearImageListEvent.value
                withContext(Dispatchers.Main) {
                    _showLoading.emit(false)
                    isLoading = false
                }
            }
            lastQuery = query

            isLoading = true
            _showLoading.value = true
            responseResultHandling(searchInteractor.getSearchResult(query.trim(), currentPage + 1, 10))
        }
    }
}