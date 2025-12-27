package com.example.statspos.presentation.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.utils.LocalDataStore
import com.example.statspos.utils.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalDataViewModel @Inject constructor(
    private var dataStore: LocalDataStore
) : ViewModel() {

    // clientId
    fun getClientId(): Flow<Int> {
        return dataStore.getClientId()
    }

    fun setClientId(value: Int){
        viewModelScope.launch {
            dataStore.setClientId(value)
        }
    }

    // localClientId
    fun getLocalClientId(): Flow<Int> {
        return dataStore.getLocalClientId()
    }

    fun setLocalClientId(value: Int){
        viewModelScope.launch {
            dataStore.setLocalClientId(value)
        }
    }

    // baseUrl
    fun getBaseUrl(): Flow<String?> {
        return dataStore.getBaseUrl()
    }

    fun setBaseUrl(value: String){
        viewModelScope.launch {
            dataStore.setBaseUrl(value)
        }
    }

    // Login Info
    fun saveLoginInfo(username: String, password: String) {
        viewModelScope.launch {
            dataStore.setUsername(username)
            dataStore.setPassword(password)
        }
    }

    fun getLoginInfo(onSuccess:(String, String) -> Unit) {
        viewModelScope.launch {
            val username = dataStore.getUsername().first() ?: ""
            val password = dataStore.getPassword().first() ?: ""

            onSuccess(username, password)
        }
    }

    // theme
    fun getTheme(): Flow<ThemeMode> {
        return dataStore.getTheme()
    }

    fun setTheme(value: ThemeMode){
        viewModelScope.launch {
            dataStore.setTheme(value)
        }
    }

}