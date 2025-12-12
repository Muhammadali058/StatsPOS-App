package com.example.statspos.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataStore @Inject constructor(
    val dataStore: DataStore<Preferences>
){
    companion object {
        private val clientIdKey = intPreferencesKey("clientId")
    }

    fun getClientId(): Flow<Int>{
        return dataStore.data.map { preferences -> preferences[clientIdKey] ?: 0 }
    }

    suspend fun setClientId(clientId:Int){
        dataStore.edit { settings ->
            settings[clientIdKey] = clientId
        }
    }
}