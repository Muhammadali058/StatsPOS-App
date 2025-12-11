package com.example.statspos.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import com.example.statspos.utils.LocalDataStore.Companion.clientIdKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class LocalDataViewModel @Inject constructor(
    private var dataStore: DataStore<Preferences>
) : ViewModel() {

    companion object {
        val clientIdKey = intPreferencesKey("clientId")
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