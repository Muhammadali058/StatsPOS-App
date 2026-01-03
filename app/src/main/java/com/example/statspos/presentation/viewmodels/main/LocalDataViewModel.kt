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
    fun saveLoginInfo(remember: Boolean, username: String, password: String) {
        viewModelScope.launch {
            dataStore.setRemember(remember)
            dataStore.setUsername(username)
            dataStore.setPassword(password)
        }
    }

    fun getLoginInfo(onSuccess:(Boolean, String, String) -> Unit) {
        viewModelScope.launch {
            val remember = dataStore.getRemember().first() ?: false
            val username = dataStore.getUsername().first() ?: ""
            val password = dataStore.getPassword().first() ?: ""

            onSuccess(remember, username, password)
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