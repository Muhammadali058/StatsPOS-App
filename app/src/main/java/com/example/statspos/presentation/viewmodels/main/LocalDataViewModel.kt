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

    fun getClientId(): Flow<Long> {
        return dataStore.getClientId()
    }

    suspend fun setClientId(clientId: Long){
        dataStore.setClientId(clientId)
    }

}