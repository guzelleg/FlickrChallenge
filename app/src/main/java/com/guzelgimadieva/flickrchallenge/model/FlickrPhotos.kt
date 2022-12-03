package com.guzelgimadieva.flickrchallenge.model

data class FlickrPhotos(
    val description: String,
    val generator: String,
    val items: List<Item>,
    val link: String,
    val modified: String,
    val title: String
)