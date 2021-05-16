package com.thewyp.android.photogallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "yupeng"

class FlickrRepository {

    private val flickrApi: FlickrApi

    private val interceptor: Interceptor = Interceptor {
        val originalRequest = it.request()
        val newUri = originalRequest.url().newBuilder()
            .addQueryParameter("api_key", BuildConfig.APP_KEY)
            .addQueryParameter("extras", "url_s")
            .addQueryParameter("format", "json")
            .addQueryParameter("nojsoncallback", "1")
            .build()
        val newRequest = originalRequest.newBuilder().url(newUri).build()
        return@Interceptor it.proceed(newRequest);
    }

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://www.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    private fun fetchPhotoMetaData(flickrRequest: Call<FlickrResponse>): LiveData<Resource<List<Photo>>> {
        val result = MutableLiveData<Resource<List<Photo>>>()
        result.value = Resource.Loading()
        flickrRequest.enqueue(
            object : Callback<FlickrResponse> {
                override fun onResponse(
                    call: Call<FlickrResponse>,
                    response: Response<FlickrResponse>,
                ) {
                    Log.d(TAG, "onResponse: ")
                    var data = response.body()?.photos?.photo ?: mutableListOf()
                    data = data.filterNot {
                        it.url.isBlank()
                    }
                    result.value = Resource.Success(data)
                }

                override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ", t)
                    result.value = Resource.Error(t.message ?: "Unknown error.")
                }
            }
        )
        return result
    }

    fun fetchInterestingnessPhotos(page: Int) =
        fetchPhotoMetaData(flickrApi.fetchInterestingnessPhotos(page))

    fun searchPhotos(query: String) = fetchPhotoMetaData(flickrApi.searchPhotos(query))


}