package com.example.statspos.presentation.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.utils.LocalDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalDataViewModel @Inject constructor(
    private var dataStore: LocalDataStore
) : ViewModel() {

    // clientId
    fun getClientId(): Flow<Long> {
        return dataStore.getClientId()
    }

    suspend fun setClientId(value: Long){
        dataStore.setClientId(value)
    }

    // localClientId
    fun getLocalClientId(): Flow<Int> {
        return dataStore.getLocalClientId()
    }

    suspend fun setLocalClientId(value: Int){
        dataStore.setLocalClientId(value)
    }

    // baseUrl
    fun getBaseUrl(): Flow<String?> {
        return dataStore.getBaseUrl()
    }

    suspend fun setBaseUrl(value: String){
        dataStore.setBaseUrl(value)
    }

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

}