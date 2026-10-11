package com.example.criminalintent

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CrimeDao {

    @Query("SELECT * FROM Crime")
    fun getCrimes(): Flow<List<Crime>>

    @Query("SELECT * FROM Crime WHERE id = :id")
    suspend fun getCrime(id: UUID): Crime?

    @Insert
    suspend fun addCrime(crime: Crime)

    @Update
    suspend fun updateCrime(crime: Crime)

    @Delete
    suspend fun deleteCrime(crime: Crime)
}