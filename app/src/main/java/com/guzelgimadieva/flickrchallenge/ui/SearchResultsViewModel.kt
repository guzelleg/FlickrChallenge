package com.guzelgimadieva.flickrchallenge.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guzelgimadieva.flickrchallenge.api.FlickrAPI
import com.guzelgimadieva.flickrchallenge.di.AppModule
import com.guzelgimadieva.flickrchallenge.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchResultsViewModel @Inject constructor(
    private val restInterface: FlickrAPI = AppModule.provideFlickrApi()
) : ViewModel() {

    private val _searchUiState = MutableStateFlow(SearchUiState())
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    private val _searchQueryState = MutableStateFlow(String())
    val searchQueryState: StateFlow<String> = _searchQueryState.asStateFlow()

    init {
        _searchQueryState
            .debounce(500L)
            .onEach {
                try {
                    _searchUiState.update { currentState ->
                        currentState.copy(listOfItems = getPhotos(it))
                    }
                } catch (t: Throwable) {
                    Timber.w(t, "error getting images")
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateSearch(s: CharSequence) {
        _searchQueryState.value = s.toString().trim()
        if (searchQueryState.value.isEmpty()) {
            _searchUiState.update { currentState ->
                currentState.copy(listOfItems = listOf())
            }
        }
    }

    private suspend fun getPhotos(tag: String): List<Item> {
        return if (tag.isNotEmpty()) {
            withContext(Dispatchers.IO)
            {
                restInterface.getPhotos(tag)
            }.items
        } else listOf()
    }

    fun setSelectedPhoto(selectedPhoto: Item) {
        _searchUiState.update { currentState ->
            currentState.copy(currentSelectedPhoto = selectedPhoto)
        }
    }
}