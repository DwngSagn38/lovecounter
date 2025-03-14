package com.example.applove.roomdb.repository

import com.example.applove.roomdb.DBHelper
import com.example.applove.roomdb.model.PersonModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PersonRepository(val dbHelper: DBHelper) {
    suspend fun insertPerson(person: PersonModel) {
        dbHelper.personDao().insertPerson(person)
    }

    suspend fun getPersonById(id: Int): PersonModel? {
        return withContext(Dispatchers.IO) { // 💡 Chạy trên luồng IO (Background Thread)
            dbHelper.personDao().getPersonById(id)
        }
    }


    suspend fun getAllPerson(): Flow<List<PersonModel>> {
        return dbHelper.personDao().getAllPerson()
    }

    suspend fun updatePerson(person: PersonModel) {
        dbHelper.personDao().updatePerson(person)
    }

    suspend fun deletePersonById() {
        dbHelper.personDao().deletePersonById()

    }
}