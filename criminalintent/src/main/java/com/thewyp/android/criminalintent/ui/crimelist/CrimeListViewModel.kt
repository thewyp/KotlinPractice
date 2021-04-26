package com.thewyp.android.criminalintent.ui.crimelist

import androidx.lifecycle.ViewModel
import com.thewyp.android.criminalintent.model.Crime
import com.thewyp.android.criminalintent.repository.Repository

class CrimeListViewModel: ViewModel() {



    fun test() {
        for (i in 0 until 100) {
            val crime = Crime()
            crime.title = "Crime #$i"
            crime.isSoled = i % 2 == 0
            Repository.get().addCrime(crime)
        }
    }

    val crimeListLiveData = Repository.get().getCrimes()

    fun addCrime(crime: Crime) = Repository.get().addCrime(crime)
}