package com.example.statspos.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalDataStore(private val context: Context){
    val Context.dataStore by preferencesDataStore("prefs")

//    val clientId = context.dataStore.data.map { preferences -> preferences[clientIdKey] ?: 0 }

    fun getClientId(): Flow<Int>{
        return context.dataStore.data.map { preferences -> preferences[clientIdKey] ?: 0 }
    }

    suspend fun setClientId(clientId:Int){
        context.dataStore.edit { settings ->
            settings[clientIdKey] = clientId
        }
    }

    companion object {
        val clientIdKey = intPreferencesKey("clientId")
    }
}