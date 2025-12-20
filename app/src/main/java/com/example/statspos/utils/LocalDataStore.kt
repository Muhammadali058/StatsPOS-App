package com.example.statspos.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
        private val usernameKey = stringPreferencesKey("username")
        private val passwordKey = stringPreferencesKey("password")
    }

    // clientId
    fun getClientId(): Flow<Long>{
        return dataStore.data.map { preferences -> preferences[clientIdKey] ?: 0 }
    }

    suspend fun setClientId(value:Long){
        dataStore.edit { settings ->
            settings[clientIdKey] = value
        }
    }

    // localClientId
    fun getLocalClientId(): Flow<Int>{
        return dataStore.data.map { preferences -> preferences[localClientIdKey] ?: 0 }
    }

    suspend fun setLocalClientId(value: Int){
        dataStore.edit { settings ->
            settings[localClientIdKey] = value
        }
    }

    // baseUrl
    fun getBaseUrl(): Flow<String?>{
        return dataStore.data.map { preferences -> preferences[baseUrlKey] }
    }

    suspend fun setBaseUrl(value: String){
        dataStore.edit { settings ->
            settings[baseUrlKey] = value
        }
    }

    // username
    fun getUsername(): Flow<String?>{
        return dataStore.data.map { preferences -> preferences[usernameKey] }
    }

    suspend fun setUsername(value: String){
        dataStore.edit { settings ->
            settings[usernameKey] = value
        }
    }

    // password
    fun getPassword(): Flow<String?>{
        return dataStore.data.map { preferences -> preferences[passwordKey] }
    }

    suspend fun setPassword(value: String){
        dataStore.edit { settings ->
            settings[passwordKey] = value
        }
    }

}