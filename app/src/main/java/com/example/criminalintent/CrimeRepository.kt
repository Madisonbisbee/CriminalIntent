package com.example.criminalintent

import android.content.Context
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CrimeRepository private constructor(context: Context) {

    private val crimeDao =
        CrimeDatabase.getDatabase(context).crimeDao()

    fun getCrimes(): Flow<List<Crime>> {
        return crimeDao.getCrimes()
    }

    suspend fun getCrime(id: UUID): Crime? {
        return crimeDao.getCrime(id)
    }

    suspend fun addCrime(crime: Crime) {
        crimeDao.addCrime(crime)
    }

    suspend fun updateCrime(crime: Crime) {
        crimeDao.updateCrime(crime)
    }

    suspend fun deleteCrime(crime: Crime) {
        crimeDao.deleteCrime(crime)
    }

    companion object {

        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE
                ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}