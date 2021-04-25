package com.thewyp.android.criminalintent

import android.app.Application
import com.thewyp.android.criminalintent.repository.Repository

class APP : Application() {
    override fun onCreate() {
        super.onCreate()
        Repository.initialize(this)
    }

    companion object {
        val executors = AppExecutors()
    }
}