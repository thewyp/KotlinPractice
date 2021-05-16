package com.thewyp.android.photogallery

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PhotoResponse(

    @SerializedName("photo")
    @Expose
    var photo: List<Photo> = emptyList(),
)

data class Photo(
    var title: String = "",
    var id: String = "",
    @SerializedName("url_s")
    var url: String = "",
)
