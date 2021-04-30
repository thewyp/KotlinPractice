package com.thewyp.android.criminalintent.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.thewyp.android.criminalintent.APP
import com.thewyp.android.criminalintent.model.Crime
import com.thewyp.android.criminalintent.database.CrimeDatabase
import com.thewyp.android.criminalintent.database.migration_1_2
import java.io.File
import java.util.*

private const val DATABASE_NAME = "crime-database"

class Repository private constructor(context: Context) {

    private val database: CrimeDatabase = Room.databaseBuilder(
        context,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).addMigrations(migration_1_2)
        .build()

    private val crimeDao = database.crimeDao()

    private val filesDir = context.applicationContext.filesDir

    fun getPhotoFile(crime: Crime) = File(filesDir, crime.photoFileName)

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)
    fun addCrime(crime: Crime) {
        APP.executors.diskIO().execute {
            crimeDao.addCrime(crime)
        }
    }

    fun updateCrime(crime: Crime) {
        APP.executors.diskIO().execute {
            crimeDao.updateCrime(crime)
        }
    }


    companion object {
        private var INSTANCE: Repository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = Repository(context)
            }
        }

        fun get(): Repository {
            return INSTANCE ?: throw IllegalStateException("Repository must be initialized")
        }
    }
}