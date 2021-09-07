package com.rosberry.pine.ui.search

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.domain.SearchInteractor
import com.rosberry.pine.ui.base.BaseViewModel
import com.rosberry.pine.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(router: Router, private val searchInteractor: SearchInteractor) :
        BaseViewModel(router) {

    private val _newPage = MutableStateFlow(mutableListOf<Image>())
    val newPage = _newPage

    private var isSearching = false
    private var currentPage = 0

    fun search(query: String) {
        if (!isSearching) {
            isSearching = true
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
                Log.d("SEARCH", query)
                val searchResult = searchInteractor.getSearchResult(query.trim(), currentPage + 1, 10)
            }
        }
    }

    private fun handleResponse(resource: Resource<List<Image>>) {
        when (resource) {
            is Resource.Success -> {
                newPage.value = resource.item.toMutableList()
                isSearching = false
            }
            is Resource.Error -> {
            }
        }
    }
}