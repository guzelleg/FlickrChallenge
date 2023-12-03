package com.guzelgimadieva.flickrchallenge.api

import com.guzelgimadieva.flickrchallenge.model.FlickrPhotos
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrAPI {
    @GET("services/feeds/photos_public.gne?format=json&nojsoncallback=1")
    suspend fun getPhotos(
        @Query("tags") tag: String
    ): FlickrPhotos
}