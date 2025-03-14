package com.example.applove.roomdb.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Date")
data class DateModel(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "start_date") var date: String?,
    @ColumnInfo(name = "day") var day: Int?,
)