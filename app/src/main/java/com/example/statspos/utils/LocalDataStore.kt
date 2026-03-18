package com.example.statspos.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.main.Branches
import com.example.statspos.domain.models.main.Clients
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataStore @Inject constructor(
    val dataStore: DataStore<Preferences>
){
    companion object {
        private val clientIdKey = intPreferencesKey("clientId")
        private val isOnlineKey = booleanPreferencesKey("isOnline")
        private val branchesKey = stringPreferencesKey("branches")
        private val baseUrlKey = stringPreferencesKey("baseUrl")
        private val rememberKey = booleanPreferencesKey("remember")
        private val usernameKey = stringPreferencesKey("username")
        private val passwordKey = stringPreferencesKey("password")
        private val themeKey = stringPreferencesKey("theme")
    }

    // clientId
    fun getClientId(): Flow<Int>{
        return dataStore.data.map { preferences -> preferences[clientIdKey] ?: 0 }
    }

    fun getIsOnline(): Flow<Boolean> {
        return dataStore.data.map { preferences -> preferences[isOnlineKey] ?: true }
    }

    fun getBranches(): Flow<List<Branches>> {
        return dataStore.data.map { preferences ->
            val json = preferences[branchesKey] ?: "[]"
            Gson().getListOf<Branches>(json)
        }
    }

    suspend fun setBranches(branches: List<Branches>) {
        dataStore.edit { settings ->
            settings[branchesKey] = Gson().toJson(branches)
        }
    }

    suspend fun setClient(client: Clients, branches: List<Branches>){
        dataStore.edit { settings ->
            settings[clientIdKey] = client.id!!
            settings[isOnlineKey] = client.isOnline!!
            settings[branchesKey] = Gson().toJson(branches)

            if(!client.isOnline!!){
                settings[baseUrlKey] = branches[0].baseUrl!!
            }
        }
    }

    // baseUrl
    fun getBaseUrl(): Flow<String>{
        return dataStore.data.map { preferences -> preferences[baseUrlKey] ?: "" }
    }

    suspend fun setBaseUrl(value: String){
        dataStore.edit { settings ->
            settings[baseUrlKey] = value
        }
    }

    // remember
    fun getRemember(): Flow<Boolean?>{
        return dataStore.data.map { preferences -> preferences[rememberKey] }
    }

    suspend fun setRemember(value: Boolean){
        dataStore.edit { settings ->
            settings[rememberKey] = value
        }
    }

    suspend fun saveLoginInfo(remember: Boolean, username: String, password: String) {
        dataStore.edit { settings ->
            settings[rememberKey] = remember
            settings[usernameKey] = username
            settings[passwordKey] = password
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

    // theme
    fun getTheme(): Flow<ThemeMode> {
        return dataStore.data.map { preferences ->
            ThemeMode.valueOf(preferences[themeKey] ?: ThemeMode.LIGHT.name)
        }
    }

    suspend fun setTheme(value: ThemeMode){
        dataStore.edit { settings ->
            settings[themeKey] = value.name
        }
    }

}