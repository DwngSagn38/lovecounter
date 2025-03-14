package com.example.applove.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.applove.roomdb.dao.BackgroundDAO
import com.example.applove.roomdb.dao.DateDAO
import com.example.applove.roomdb.dao.PersonDAO
import com.example.applove.roomdb.model.BackgroundModel
import com.example.applove.roomdb.model.DateModel
import com.example.applove.roomdb.model.PersonModel

@Database(entities = [PersonModel::class, DateModel::class, BackgroundModel::class], version = 1)
abstract class DBHelper : RoomDatabase() {

    abstract fun personDao(): PersonDAO
    abstract fun dateDao(): DateDAO
    abstract fun backgroundDao(): BackgroundDAO

    companion object {
        @Volatile
        private var INTANCE : DBHelper? = null

        fun getIntance(context: Context): DBHelper{
            synchronized(this){
                var intance = INTANCE
                if (intance == null) {
                    intance = Room.databaseBuilder(
                        context.applicationContext,
                        DBHelper::class.java,
                        "my_database"
                    ).build()
                }
                return intance
            }
        }
    }
}