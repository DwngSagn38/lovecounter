package com.example.applove.roomdb.repository

import androidx.lifecycle.LiveData
import com.example.applove.roomdb.DBHelper
import com.example.applove.roomdb.model.BackgroundModel
import kotlinx.coroutines.flow.Flow

class BackgroundRepository(private val db: DBHelper) {

    suspend fun insertBackground(background: BackgroundModel) {
        db.backgroundDao().insertBackground(background)
    }

    fun getAllBackgrounds(): LiveData<List<BackgroundModel>> {
        return db.backgroundDao().getAllBackgrounds()
    }
}

