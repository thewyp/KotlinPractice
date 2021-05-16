package com.thewyp.android.photogallery

import android.app.Application
import androidx.lifecycle.*

class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {


    private val repository: FlickrRepository = FlickrRepository()

    private val _page = MutableLiveData<Int>()
    val photosLiveData: LiveData<Resource<List<Photo>>> = _page.switchMap {
        repository.fetchInterestingnessPhotos(it)
    }

    private val _query = MutableLiveData<String>()
    val searchPhotosLiveData: LiveData<Resource<List<Photo>>> = _query.switchMap {
        if (it.isBlank()) {
            AbsentLiveData.create()
        } else {
            repository.searchPhotos(it)
        }
    }

    val searchTerm: String
        get() = QueryPreferences.getStoredQuery(app)

    init {
//        _query.value = QueryPreferences.getStoredQuery(app)
    }

    fun fetchInterestingnessPhotos(page: Int) {
        _page.value = page
    }

    fun searchPhtots(query: String) {
        QueryPreferences.setStoredQuery(app, query)
        _query.value = query
    }

}