package com.example.applove.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.applove.roomdb.model.BackgroundModel
import com.example.applove.roomdb.repository.BackgroundRepository
import com.example.lovecounter.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BackgroundViewModel(private val repository: BackgroundRepository) : BaseViewModel() {
    val backgrounds: LiveData<List<BackgroundModel>> = repository.getAllBackgrounds()

    fun saveBackground(imageUri: String) {
        val backgroundModel = BackgroundModel(0, imageUri)

        viewModelScope.launch(Dispatchers.IO) { // Chạy trên background thread
            repository.insertBackground(backgroundModel)
        }
    }

}

