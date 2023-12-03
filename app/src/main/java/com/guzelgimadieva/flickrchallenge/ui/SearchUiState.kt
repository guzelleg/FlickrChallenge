package com.guzelgimadieva.flickrchallenge.ui

import com.guzelgimadieva.flickrchallenge.model.Item

data class SearchUiState(
    val listOfItems: List<Item> = listOf(),
    val currentSelectedPhoto: Item? = null
)
