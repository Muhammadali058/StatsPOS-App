package com.example.statspos.presentation.viewmodels.utilities.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.utilities.settings.AdminPasswords
import com.example.statspos.domain.models.utilities.settings.AdminSettings
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
class AdminSettingsViewModel @Inject constructor(
    private val api: SettingsRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,

        val showSuppliersInPurchase: Boolean = false,
        val searchInSales: Boolean = false,
        val searchInPurchase: Boolean = false,
        val estimatedBill: Boolean = false,
        val useWeightScale: Boolean = false,
        val showDashboard: Boolean = false,
        val detailedSearchInPOS: Boolean = false,

        // Passwords
        val printDuplicates: String = "",
        val audit: String = "",

        val usePrintDuplicates: Boolean = false,
        val useAudit: Boolean = false,

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
    //    Settings
    fun onShowSuppliersInPurchaseChange(value: Boolean) {
        state.update { it.copy(showSuppliersInPurchase = value) }
    }

    fun onSearchInSalesChange(value: Boolean) {
        state.update { it.copy(searchInSales = value) }
    }

    fun onSearchInPurchaseChange(value: Boolean) {
        state.update { it.copy(searchInPurchase = value) }
    }

    fun onEstimatedBillChange(value: Boolean) {
        state.update { it.copy(estimatedBill = value) }
    }

    fun onUseWeightScaleChange(value: Boolean) {
        state.update { it.copy(useWeightScale = value) }
    }

    fun onShowDashboardChange(value: Boolean) {
        state.update { it.copy(showDashboard = value) }
    }

    fun onDetailedSearchInPOSChange(value: Boolean) {
        state.update { it.copy(detailedSearchInPOS = value) }
    }
    // Passwords
    fun onPrintDuplicatesChange(value: String) {
        state.update { it.copy(printDuplicates = value) }
    }

    fun onAuditChange(value: String) {
        state.update { it.copy(audit = value) }
    }

    fun onUsePrintDuplicatesChange(value: Boolean) {
        state.update { it.copy(usePrintDuplicates = value) }
    }

    fun onUseAuditChange(value: Boolean) {
        state.update { it.copy(useAudit = value) }
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }

    // endregion

    // region Network calls
    fun updateAdminSettings(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            state.update { it.copy(isSaving = true) }

            val adminSettings = getFormDataAdminSettings()
            val adminPasswords = getFormDataAdminPasswords()
            val result = api.updateAdminSettings(adminSettings, adminPasswords)

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> showMessage(result.message)
                is Resource.Success -> {

                    HP.adminSettings =
                        Gson().get<AdminSettings>(result.data.get("adminSettings").asJsonObject)
                    HP.adminPasswords =
                        Gson().get<AdminPasswords>(result.data.get("adminPasswords").asJsonObject)

                    onSuccess()
                }
            }
        }
    }

    fun editData() {
        viewModelScope.launch {
            setFormDataSettings(HP.adminSettings)
            setFormDataAdminPasswords(HP.adminPasswords)
        }
    }
    // endregion

    // region Methods
    private fun getFormDataAdminSettings(): AdminSettings {
        return AdminSettings(
            showSuppliersInPurchase = state.value.showSuppliersInPurchase,
            searchInSales = state.value.searchInSales,
            searchInPurchase = state.value.searchInPurchase,
            estimatedBill = state.value.estimatedBill,
            useWeightScale = state.value.useWeightScale,
            showDashboard = state.value.showDashboard,
            detailedSearchInPOS = state.value.detailedSearchInPOS,
        )
    }

    private fun getFormDataAdminPasswords(): AdminPasswords {
        return AdminPasswords(
            printDuplicates = state.value.printDuplicates,
            audit = state.value.audit,

            usePrintDuplicates = state.value.usePrintDuplicates,
            useAudit = state.value.useAudit,
        )
    }

    private fun setFormDataSettings(adminSettings: AdminSettings) {
        state.update {
            it.copy(
                showSuppliersInPurchase = adminSettings.showSuppliersInPurchase!!,
                searchInSales = adminSettings.searchInSales!!,
                searchInPurchase = adminSettings.searchInPurchase!!,
                estimatedBill = adminSettings.estimatedBill!!,
                useWeightScale = adminSettings.useWeightScale!!,
                showDashboard = adminSettings.showDashboard!!,
                detailedSearchInPOS = adminSettings.detailedSearchInPOS!!,
            )
        }
    }

    private fun setFormDataAdminPasswords(adminPasswords: AdminPasswords) {
        state.update {
            it.copy(
                printDuplicates = adminPasswords.printDuplicates.toString(),
                audit = adminPasswords.audit.toString(),

                usePrintDuplicates = adminPasswords.usePrintDuplicates!!,
                useAudit = adminPasswords.useAudit!!,
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