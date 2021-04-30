package com.thewyp.android.criminalintent.ui.crime

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.thewyp.android.criminalintent.model.Crime
import com.thewyp.android.criminalintent.repository.Repository
import java.util.*

class CrimeDetailViewModel : ViewModel() {

    private val crimeIdLiveData = MutableLiveData<UUID>()

    val crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIdLiveData) {
            Repository.get().getCrime(it)
        }

    fun loadCrime(crimeId: UUID) {
        crimeIdLiveData.value = crimeId
    }

    fun saveCrime(crime: Crime) {
        Repository.get().updateCrime(crime)
    }

    fun getPhotoFile(crime: Crime) = Repository.get().getPhotoFile(crime)
}