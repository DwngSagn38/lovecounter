package com.example.applove.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.applove.roomdb.model.DateModel
import com.example.applove.roomdb.model.PersonModel
import com.example.applove.roomdb.repository.DateRepository
import com.example.applove.roomdb.repository.PersonRepository
import com.example.lovecounter.base.BaseViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DateViewModel(private val dateRepository: DateRepository) : BaseViewModel() {

    private val _dates = MutableLiveData<List<DateModel>>()
    val dates: LiveData<List<DateModel>> get() = _dates

    private val _selectedDate = MutableLiveData<String?>()
    val selectedDate: LiveData<String?> get() = _selectedDate

    init {
        fetchDate()
    }

    private fun fetchDate() {
        viewModelScope.launch {
            dateRepository.getAllDate().collect { dates ->
                _dates.value = dates
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDaysFromStart(dateString: String): Long {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val startDate = LocalDate.parse(dateString, formatter)
            val currentDate = LocalDate.now()
            ChronoUnit.DAYS.between(startDate, currentDate)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertOrReplaceDate(dateString: String) {
        viewModelScope.launch {
            val dateList = dateRepository.getAllDate().first() // Chỉ lấy 1 lần, không lặp vô hạn
            val days = calculateDaysFromStart(dateString)

            if (dateList.isNotEmpty()) {
                val existingDate = dateList.first()
                dateRepository.deleteDate(existingDate) // Xóa phần tử cũ
            }

            val newDate = DateModel(date = dateString, day = days.toInt())
            dateRepository.insertDate(newDate) // Chèn phần tử mới

            // Cập nhật LiveData để UI tự động đổi ngày
            _selectedDate.postValue(dateString)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getCountDay(): String {
        val dateList = dateRepository.getAllDate().first()
        return if (dateList.isNotEmpty()) {
            val countDay = dateList.first().day // Lấy ngày đầu tiên
            countDay.toString()
        } else {
            "0"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getSavedDate() {
        viewModelScope.launch {
            dateRepository.getAllDate().collect { dateList ->
                if (dateList.isNotEmpty()) {
                    val firstDate = dateList.first().date // Lấy ngày đầu tiên
                    _selectedDate.postValue(firstDate) // Cập nhật UI
                }
            }
        }
    }
}