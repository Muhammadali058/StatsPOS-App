package com.example.statspos.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataStore @Inject constructor(
    val dataStore: DataStore<Preferences>
){
    companion object {
        private val clientIdKey = longPreferencesKey("clientId")
        private val localClientIdKey = intPreferencesKey("localClientId")
        private val baseUrlKey = stringPreferencesKey("baseUrl")
    }

    // clientId
    fun getClientId(): Flow<Long>{
        return dataStore.data.map { preferences -> preferences[clientIdKey] ?: 0 }
    }

    suspend fun setClientId(clientId:Long){
        dataStore.edit { settings ->
            settings[clientIdKey] = clientId
        }
    }

    // localClientId
    fun getLocalClientId(): Flow<Int>{
        return dataStore.data.map { preferences -> preferences[localClientIdKey] ?: 0 }
    }

    suspend fun setLocalClientId(localClientId: Int){
        dataStore.edit { settings ->
            settings[localClientIdKey] = localClientId
        }
    }

    // baseUrl
    fun getBaseUrl(): Flow<String?>{
        return dataStore.data.map { preferences -> preferences[baseUrlKey] }
    }

    suspend fun setBaseUrl(baseUrl: String){
        dataStore.edit { settings ->
            settings[baseUrlKey] = baseUrl
        }
    }
}