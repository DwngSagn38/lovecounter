package com.example.applove.roomdb.repository

import com.example.applove.roomdb.DBHelper
import com.example.applove.roomdb.model.DateModel
import com.example.applove.roomdb.model.PersonModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class DateRepository(val dbHelper: DBHelper) {

    suspend fun insertDate(date: DateModel) {
        dbHelper.dateDao().insertDate(date)
    }

    suspend fun getDateById(id: Int) {
        dbHelper.dateDao().getDateById(id)
    }

    fun getAllDate(): Flow<List<DateModel>> {
        return dbHelper.dateDao().getAllDate()
    }

    suspend fun updateDate(date: DateModel) {
        dbHelper.dateDao().updateDate(date)
    }

    suspend fun deleteDate(dateModel: DateModel) {
        dbHelper.dateDao().delete(dateModel)
    }

}