package com.example.statspos.presentation.viewmodels.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.main.Branches
import com.example.statspos.domain.models.main.Clients
import com.example.statspos.domain.repository.main.ClientsRepository
import com.example.statspos.domain.repository.main.MainRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.LocalDataStore
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
import com.example.statspos.utils.getListOf
import com.example.statspos.utils.preloadImages
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val api: MainRepository,
    private val clientsRepo: ClientsRepository,
    private var dataStore: LocalDataStore,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val isLoading: Boolean = false,
        val error: String? = null,
    )

    var state = MutableStateFlow(ScreenState())
        private set

    fun beforeRequest() {
        state.update { it.copy(isLoading = true, error = null) }
    }

    private val _event = Channel<UiEvent>()
    var event = _event.receiveAsFlow()
    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ShowSnackbar -> {
                viewModelScope.launch {
                    _event.send(UiEvent.ShowSnackbar(event.message, event.type))
                }
            }

            is UiEvent.ShowError -> {
                viewModelScope.launch {
                    _event.send(UiEvent.ShowError(event.error))
                }
            }

            else -> {
                viewModelScope.launch {
                    _event.send(UiEvent.Idle)
                }
            }
        }
    }

    fun showSnackbar(message: String, type: SnackbarType = SnackbarType.INFORMATION) {
        onEvent(UiEvent.ShowSnackbar(message, type))
    }

    // endregion

    // region Network calls
    fun uploadImage(multipart: MultipartBody.Part) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.uploadImage(multipart)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val fileName = result.data.asJsonObject.get("fileName").asString
//                    state.update { it.copy(
//                        isUploadingImage = false,
//                        imageUrl = fileName,
//                    ) }
                }
            }
        }
    }

    fun updateBranches(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            val clientId = dataStore.getClientId().first()

            when (val result = clientsRepo.getBranches(clientId)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val branches =
                        Gson().getListOf<Branches>(result.data.get("branches").asJsonArray)
                    dataStore.setBranches(branches)
                    onSuccess()
                }
            }
        }
    }

    fun getClient(onSuccess: (Clients) -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            val clientId = dataStore.getClientId().first()

            when (val result = clientsRepo.getClient(clientId)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val client = Gson().get<Clients>(result.data.asJsonObject)
                    HP.client = client
                    onSuccess(client)
                }
            }
        }
    }

    fun loadMainData(onSuccess: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch
            beforeRequest()

            when (val result = api.loadData()) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    HP.setDropdowns(result.data)
                    preloadImages(listOf(HP.getImageUrl(HP.printSettings.imageUrl.toString())))

                    getClient { client ->
                        if (client.isRegisteredWeek == false || client.isRegisteredMonth == false) {
                            showSnackbar("Please activate your app")
                            onSuccess(false)
                        }else{
                            onSuccess(true)
                        }
                    }
                }
            }
        }
    }

    // endregion

    // region Others
    private fun resultError(error: String?) {
        state.update { it.copy(isLoading = false, error = error) }
        error?.let { onEvent(UiEvent.ShowError(it)) }
    }

    private fun resultInformation(message: String?) {
        state.update { it.copy(isLoading = false) }
        message?.let { showSnackbar(it) }
    }

    private fun resultSuccess() {
        state.update { it.copy(isLoading = false, error = null) }
    }
    // endregion
}