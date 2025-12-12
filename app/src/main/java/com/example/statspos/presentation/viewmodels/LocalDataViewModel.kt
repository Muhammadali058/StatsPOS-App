package com.example.statspos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.statspos.utils.LocalDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class LocalDataViewModel @Inject constructor(
    private var dataStore: LocalDataStore
) : ViewModel() {

    fun getClientId(): Flow<Int>{
        return dataStore.getClientId()
    }

    suspend fun setClientId(clientId:Int){
        dataStore.setClientId(clientId)
    }

}