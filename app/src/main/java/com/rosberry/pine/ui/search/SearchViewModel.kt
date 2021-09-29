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

    private val _searchList = MutableStateFlow(listOf<SearchItem>())
    val searchList: StateFlow<List<SearchItem>> = _searchList

    var lastQuery: String? = null

    override fun loadNewPage() {
        if (!isLoading.value && lastQuery != null && !nothingFoundHappened) {
            search(lastQuery ?: "")
        }
    }

    fun clearList() {
        photos.clear()
    }

    fun search(query: String) {
        nothingFoundHappened = false
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            if (lastQuery != query) {
                currentPage = 0
                photos.clear()
                _images.value = emptyList()
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
            lastQuery = query

            _isLoading.value = true
            responseResultHandling(searchInteractor.getSearchResult(query.trim(), currentPage + 1, 10))
        }
    }

    fun fillSearchList() {
        viewModelScope.launch(Dispatchers.IO) {
            _searchList.value = searchInteractor.getLastSearchQueries(10)
                .map { SearchItem(it) }
        }
    }
}