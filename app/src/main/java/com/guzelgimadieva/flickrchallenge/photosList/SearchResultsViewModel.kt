package com.guzelgimadieva.flickrchallenge.photosList

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guzelgimadieva.flickrchallenge.api.FlickrApiService
import com.guzelgimadieva.flickrchallenge.model.FlickrPhotos
import com.guzelgimadieva.flickrchallenge.model.Item
import com.guzelgimadieva.flickrchallenge.utils.Constants
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class SearchResultsViewModel(
) : ViewModel() {
    private var restInterface: FlickrApiService

    private val _searchUiState = MutableStateFlow(SearchUiState(currentSelectedPhoto = null))
    val searchUiState: StateFlow<SearchUiState>
           = _searchUiState.asStateFlow()

    private var isLoading = mutableStateOf(false)

    private val errorHandler =
        CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
        }

    init {
        val retrofit: Retrofit =
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        restInterface = retrofit.create(FlickrApiService::class.java)
    }

    fun getPhotos(tag: String?) {
        viewModelScope.launch(errorHandler) {
            isLoading.value = true
            if (tag != null) {
                _searchUiState.update { currentState ->
                    currentState.copy(listOfItems = getRemotePhotos(tag).items)
                }
            }
        }
    }

    private suspend fun getRemotePhotos(tag:String): FlickrPhotos{
        return withContext(Dispatchers.IO){
            restInterface.getPhotos(tag)
        }
    }

    fun updateUserSearch(searchTerm: String) {
        _searchUiState.update { currentState ->
            currentState.copy(searchQuery = searchTerm)
        }
    }

    fun setSelectedPhoto(selectedPhoto: Item?) {
        if (selectedPhoto != null) {
            _searchUiState.update { currentState ->
                currentState.copy(currentSelectedPhoto = selectedPhoto)
            }
        }
    }
}