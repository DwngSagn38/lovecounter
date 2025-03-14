package com.example.applove.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.applove.R
import com.example.applove.roomdb.repository.PersonRepository
import com.example.applove.roomdb.DBHelper
import com.example.applove.roomdb.model.PersonModel
import com.example.lovecounter.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PersonViewModel(private val personRepository: PersonRepository) : BaseViewModel() {
    private val _persons = MutableLiveData<List<PersonModel>>()
    val persons: LiveData<List<PersonModel>> get() = _persons

//    val persons: Flow<List<PersonModel>> = personRepository.getAllPerson()

    init {
        fetchPersons()
    }

    private fun fetchPersons(){
        viewModelScope.launch {
            personRepository.getAllPerson().collect{ persons ->
                _persons.postValue(persons)
            }
        }
    }


    fun insertPerson( id : Int, name : String, gender : String, birthday : String, image : String) : Boolean {
        if (name.isEmpty() || gender.isEmpty() || birthday.isEmpty() ||  image.isEmpty()){
            return false
        }

        val person = PersonModel(id ,name,gender,birthday, image)
        viewModelScope.launch {
            personRepository.insertPerson(person)
        }
        return true
    }

    suspend fun updatePerson(id: Int, name: String, gender: String, birthday: String, image: String): String {
        if (name.isEmpty() || gender.isEmpty() || birthday.isEmpty()) {
            return "Fail"
        }

        val person = personRepository.getPersonById(id)
        Log.d("PersonViewModel", "🔄 Updating person: $person")

        return if (person != null) {
            val updatedPerson = person.copy(name = name, gender = gender, birthday = birthday, image = image)
            personRepository.updatePerson(updatedPerson)
            Log.d("PersonViewModel", "✅ Updated person: $updatedPerson")
            "Success"
        } else {
            Log.d("PersonViewModel", "❌ Person not found!")
            "Fail"
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            personRepository.getAllPerson().collect { persons ->
                Log.d("PersonViewModel", "📢 Refreshing data: $persons")
                _persons.postValue(persons) // Cập nhật LiveData để giao diện lắng nghe thay đổi
            }
        }
    }

}
