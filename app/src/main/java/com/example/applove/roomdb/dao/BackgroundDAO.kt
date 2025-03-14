package com.example.applove.roomdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.applove.roomdb.model.BackgroundModel
import kotlinx.coroutines.flow.Flow

@Dao
interface BackgroundDAO {
    @Insert
    fun insertBackground(background: BackgroundModel)

    @Query("SELECT * FROM Background ORDER BY id ASC")
    fun getAllBackgrounds(): LiveData<List<BackgroundModel>>

    @Query("DELETE FROM Background WHERE id = :id")
    suspend fun deleteBackground(id: Int)
}
