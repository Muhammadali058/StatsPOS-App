package com.graphees.statspos.presentation.viewmodels.utilities.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.domain.models.utilities.settings.Passwords
import com.graphees.statspos.domain.models.utilities.settings.Settings
import com.graphees.statspos.domain.repository.utilities.SettingsRepository
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.SnackbarType
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.get
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalesSettingsViewModel @Inject constructor(
    private val api: SettingsRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val defaultRate: DropdownItem = HP.defaultRate[0],
        val printLanguage: DropdownItem = HP.printLanguages[0],

        val saleUnderStock: Boolean = false,
        val costWarning: Boolean = false,
        val autoCreditSelect: Boolean = false,
        val showItemStock: Boolean = false,
        val editOldCreditBill: Boolean = false,
        val autoRetailChange: Boolean = false,
        val showLedgerInBill: Boolean = false,
        val saleCartons: Boolean = false,
        val fourRateSystem: Boolean = false,
        val sameDateBillEdit: Boolean = false,
        val showCustomerLastRate: Boolean = false,
        val alwaysUseLastRate: Boolean = false,
        val itemExistsInSalesWarning: Boolean = false,

        // Extras
        val hasLoadedOnce: Boolean = false,

        val isLoading: Boolean = false,
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
    fun onDefaultRateChange(value: DropdownItem) {
        state.update { it.copy(defaultRate = value) }
    }

    fun onPrintLanguageChange(value: DropdownItem) {
        state.update { it.copy(printLanguage = value) }
    }

    fun onSaleUnderStockChange(value: Boolean) {
        state.update { it.copy(saleUnderStock = value) }
    }

    fun onCostWarningChange(value: Boolean) {
        state.update { it.copy(costWarning = value) }
    }

    fun onAutoCreditSelectChange(value: Boolean) {
        state.update { it.copy(autoCreditSelect = value) }
    }

    fun onShowItemStockChange(value: Boolean) {
        state.update { it.copy(showItemStock = value) }
    }

    fun onEditOldCreditBillChange(value: Boolean) {
        state.update { it.copy(editOldCreditBill = value) }
    }

    fun onAutoRetailChangeChange(value: Boolean) {
        state.update { it.copy(autoRetailChange = value) }
    }

    fun onShowLedgerInBillChange(value: Boolean) {
        state.update { it.copy(showLedgerInBill = value) }
    }

    fun onSaleCartonsChange(value: Boolean) {
        state.update { it.copy(saleCartons = value) }

        if (value) {
            if (state.value.fourRateSystem)
                state.update { it.copy(fourRateSystem = false) }

            state.update { it.copy(defaultRate = HP.defaultRate[1]) }
        } else {
            state.update { it.copy(defaultRate = HP.defaultRate[0]) }
        }
    }

    fun onFourRateSystemChange(value: Boolean) {
        state.update { it.copy(fourRateSystem = value) }

        if (value) {
            if (state.value.saleCartons)
                state.update { it.copy(saleCartons = false) }

            state.update { it.copy(defaultRate = HP.defaultRate[0]) }
        } else {
            if (state.value.saleCartons)
                state.update { it.copy(defaultRate = HP.defaultRate[1]) }
            else
                state.update { it.copy(defaultRate = HP.defaultRate[0]) }
        }
    }

    fun onSameDateBillEditChange(value: Boolean) {
        state.update { it.copy(sameDateBillEdit = value) }
    }

    fun onShowCustomerLastRateChange(value: Boolean) {
        state.update { it.copy(showCustomerLastRate = value) }
    }

    fun onAlwaysUseLastRateChange(value: Boolean) {
        state.update { it.copy(alwaysUseLastRate = value) }
    }

    fun onItemExistsInSalesWarningChange(value: Boolean) {
        state.update { it.copy(itemExistsInSalesWarning = value) }
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }

    // endregion

    // region Network calls
    fun updateSettings(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            val settings = getFormDataSettings()
            val passwords = Passwords()

            when (val result = api.updateSettings(settings, passwords)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    HP.settings =
                        Gson().get<Settings>(result.data.get("settings").asJsonObject)
                    HP.passwords =
                        Gson().get<Passwords>(result.data.get("passwords").asJsonObject)

                    onSuccess()
                }
            }
        }
    }

    fun editData() {
        viewModelScope.launch {
            setFormDataSettings(HP.settings)
        }
    }
    // endregion

    // region Methods
    private fun getFormDataSettings(): Settings {
        return Settings(
            isDefaultRateRetail = state.value.defaultRate.id == 0L,
            printLanguage = state.value.printLanguage.id.toInt(),

            saleUnderStock = state.value.saleUnderStock,
            costWarning = state.value.costWarning,
            autoCreditSelect = state.value.autoCreditSelect,
            showItemStock = state.value.showItemStock,
            editOldCreditBill = state.value.editOldCreditBill,
            autoRetailChange = state.value.autoRetailChange,
            showLedgerInBill = state.value.showLedgerInBill,
            saleCartons = state.value.saleCartons,
            fourRateSystem = state.value.fourRateSystem,
            sameDateBillEdit = state.value.sameDateBillEdit,
            showCustomerLastRate = state.value.showCustomerLastRate,
            alwaysUseLastRate = state.value.alwaysUseLastRate,
            itemExistsInSalesWarning = state.value.itemExistsInSalesWarning,
        )
    }

    private fun setFormDataSettings(setting: Settings) {
        state.update {
            it.copy(
                defaultRate = HP.defaultRate[if (setting.isDefaultRateRetail!!) 0 else 1],
                printLanguage = HP.printLanguages[setting.printLanguage!! - 1],

                saleUnderStock = setting.saleUnderStock!!,
                costWarning = setting.costWarning!!,
                autoCreditSelect = setting.autoCreditSelect!!,
                showItemStock = setting.showItemStock!!,
                editOldCreditBill = setting.editOldCreditBill!!,
                autoRetailChange = setting.autoRetailChange!!,
                showLedgerInBill = setting.showLedgerInBill!!,
                saleCartons = setting.saleCartons!!,
                fourRateSystem = setting.fourRateSystem!!,
                sameDateBillEdit = setting.sameDateBillEdit!!,
                showCustomerLastRate = setting.showCustomerLastRate!!,
                alwaysUseLastRate = setting.alwaysUseLastRate!!,
                itemExistsInSalesWarning = setting.itemExistsInSalesWarning!!,
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