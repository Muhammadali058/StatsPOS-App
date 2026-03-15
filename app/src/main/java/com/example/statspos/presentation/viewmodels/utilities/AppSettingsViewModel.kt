package com.example.statspos.presentation.viewmodels.utilities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.utilities.settings.AppSettings
import com.example.statspos.domain.repository.utilities.SettingsRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.get
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    private val api: SettingsRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,

        val instantSearch: Boolean = false,
        val innerItemSearch: Boolean = false,
        val itemSuggestions: Boolean = false,

        // Extras
        val hasLoadedOnce: Boolean = false,

        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val message: String? = null,
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

            is UiEvent.ShowMessage -> {
                viewModelScope.launch {
                    _event.send(UiEvent.ShowMessage(event.message))
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

    fun showMessage(message: String?) {
        showSnackbar(message ?: "")

//        state.update { it.copy(isLoading = false, error = null, message = message) }
//        onEvent(UiEvent.ShowMessage(message ?: ""))
    }

    fun showError(error: String?) {
        state.update { it.copy(error = error) }
        onEvent(UiEvent.ShowError(error ?: ""))
    }

    // endregion

    // region onChangeMethods
    fun onInstantSearchChange(value: Boolean) {
        state.update { it.copy(instantSearch = value) }
    }

    fun onInnerItemSearchChange(value: Boolean) {
        state.update { it.copy(innerItemSearch = value) }
    }

    fun onItemSuggestionsChange(value: Boolean) {
        state.update { it.copy(itemSuggestions = value) }
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }

    // endregion

    // region Network calls
    fun updateAppSettings(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            state.update { it.copy(isSaving = true) }

            val appSettings = getFormData()
            val result = api.updateAppSettings(appSettings)

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> showMessage(result.message)
                is Resource.Success -> {

                    HP.appSettings =
                        Gson().get<AppSettings>(result.data.toString())

                    onSuccess()
                }
            }
        }
    }

    fun editData() {
        viewModelScope.launch {
            setFormData(HP.appSettings)
        }
    }

    // endregion

    // region Methods
    private fun getFormData(): AppSettings {
        return AppSettings(
            instantSearch = state.value.instantSearch,
            innerItemSearch = state.value.innerItemSearch,
            itemSuggestions = state.value.itemSuggestions,
        )
    }

    private fun setFormData(appSettings: AppSettings) {
        state.update {
            it.copy(
                instantSearch = appSettings.instantSearch!!,
                innerItemSearch = appSettings.innerItemSearch!!,
                itemSuggestions = appSettings.itemSuggestions!!,
            )
        }
    }

    // endregion

    // region Others
    private fun resultError(error: String?) {
        state.update { it.copy(isLoading = false) }
        showError(error)
    }

    private fun resultInformation(message: String?) {
        state.update { it.copy(isLoading = false) }
        showMessage(message)
    }

    private fun resultSuccess() {
        state.update { it.copy(isLoading = false) }
    }

    // endregion
}