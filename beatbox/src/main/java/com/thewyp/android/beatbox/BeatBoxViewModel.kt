package com.thewyp.android.beatbox

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class BeatBoxViewModel(application: Application) : AndroidViewModel(application) {

    var beatBox: BeatBox = BeatBox(application.assets)


    override fun onCleared() {
        super.onCleared()
        beatBox.release()
    }
}