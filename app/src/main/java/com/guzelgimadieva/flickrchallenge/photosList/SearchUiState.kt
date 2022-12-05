package com.guzelgimadieva.flickrchallenge.photosList

import com.guzelgimadieva.flickrchallenge.model.Item

data class SearchUiState(
    val listOfItems: List<Item> = listOf(),
    val searchQuery: String? = null,
    val currentSelectedPhoto: Item? = null
)
