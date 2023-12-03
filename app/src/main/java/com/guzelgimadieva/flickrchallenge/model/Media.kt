package com.guzelgimadieva.flickrchallenge.model

import com.squareup.moshi.Json

data class Media(
    @Json(name = "m")
    val mSizeUrl: String
)