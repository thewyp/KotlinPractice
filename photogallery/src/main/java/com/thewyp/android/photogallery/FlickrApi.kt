package com.thewyp.android.photogallery

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {

    @GET("services/rest/?method=flickr.interestingness.getList&date=2021-05-10")
    fun fetchInterestingnessPhotos(@Query("page") page: Int): Call<FlickrResponse>

    @GET("services/rest/?method=flickr.photos.search")
    fun searchPhotos(@Query("text") query: String): Call<FlickrResponse>
}