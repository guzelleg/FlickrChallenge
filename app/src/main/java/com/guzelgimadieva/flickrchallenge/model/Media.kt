package com.guzelgimadieva.flickrchallenge.model

import com.google.gson.annotations.SerializedName

data class Media(
    @SerializedName("m")
    val size_m_url: String
)