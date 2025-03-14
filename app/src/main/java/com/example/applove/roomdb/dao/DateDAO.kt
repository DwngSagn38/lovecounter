package com.example.applove.roomdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.applove.roomdb.model.DateModel
import kotlinx.coroutines.flow.Flow

@Dao
interface DateDAO {

    @Insert
    suspend fun insertDate(date: DateModel)

    @Query("SELECT * FROM Date WHERE id = :id")
    fun getDateById(id: Int): DateModel?

    @Query("SELECT * FROM Date")
    fun getAllDate(): Flow<List<DateModel>>

    @Update
    suspend fun updateDate(date: DateModel)

    @Delete
    suspend fun delete(date: DateModel)
}