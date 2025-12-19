package com.example.statspos.presentation.viewmodels.main

import androidx.lifecycle.ViewModel
import com.example.statspos.utils.LocalDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class LocalDataViewModel @Inject constructor(
    private var dataStore: LocalDataStore
) : ViewModel() {

    // clientId
    fun getClientId(): Flow<Long> {
        return dataStore.getClientId()
    }

    suspend fun setClientId(clientId: Long){
        dataStore.setClientId(clientId)
    }

    // localClientId
    fun getLocalClientId(): Flow<Int> {
        return dataStore.getLocalClientId()
    }

    suspend fun setLocalClientId(localClientId: Int){
        dataStore.setLocalClientId(localClientId)
    }

    // baseUrl
    fun getBaseUrl(): Flow<String?> {
        return dataStore.getBaseUrl()
    }

    suspend fun setBaseUrl(baseUrl: String){
        dataStore.setBaseUrl(baseUrl)
    }

}