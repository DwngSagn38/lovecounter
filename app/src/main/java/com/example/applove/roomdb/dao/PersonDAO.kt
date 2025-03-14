package com.example.applove.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.applove.roomdb.model.PersonModel
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDAO {
    @Insert
    suspend fun insertPerson(person: PersonModel)

    @Query("SELECT * FROM Person WHERE id = :id")
    fun getPersonById(id: Int): PersonModel?

    @Query("SELECT * FROM Person")
    fun getAllPerson(): Flow<List<PersonModel>>

    @Update
    suspend fun updatePerson(person: PersonModel)

    @Query("DELETE FROM Person")
    suspend fun deletePersonById()

}