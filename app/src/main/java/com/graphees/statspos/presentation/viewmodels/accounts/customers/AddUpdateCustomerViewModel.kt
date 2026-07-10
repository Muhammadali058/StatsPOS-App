package com.graphees.statspos.presentation.viewmodels.accounts.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.domain.models.accounts.Accounts
import com.graphees.statspos.domain.repository.accounts.CustomersRepository
import com.graphees.statspos.domain.repository.main.MainRepository
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.SnackbarType
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.get
import com.graphees.statspos.utils.getListOf
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AddUpdateCustomerViewModel @Inject constructor(
    private val api: CustomersRepository,
    private val mainRepo: MainRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val accountName: String = "",
        val address: String = "",
        val contact: String = "",
        val city: String = "",
        val email: String = "",
        val remarks: String = "",
        val ntn: String = "",
        val stn: String = "",
        val cnic: String = "",
        val dueDays: String = "",

        val categoryId: Long = 0L,
        val supplierId: Long = 0L,

        val disc: String = "",
        val isDiscRsPer: Boolean = HP.settings.isDefaultDiscRs!!,
        val openingBalance: String = "",

        val isRetail: Boolean = false,
        val isCredit: Boolean = false,

        val imageUrl: String = "",

        // Extras
        val categoryName: String = "",
        val supplierName: String = "",

        val isUpdate: Boolean = false,
        val updateId: Long = 0L,
        val openingBalanceTBEnabled: Boolean = true,

        val hasLoadedOnce: Boolean = false,

        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val isUploadingImage: Boolean = false,
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
    fun onAccountNameChange(value: String) {
        state.update { it.copy(accountName = value) }
    }

    fun onAddressChange(value: String) {
        state.update { it.copy(address = value) }
    }

    fun onContactChange(value: String) {
        state.update { it.copy(contact = value) }
    }

    fun onCityChange(value: String) {
        state.update { it.copy(city = value) }
    }

    fun onEmailChange(value: String) {
        state.update { it.copy(email = value) }
    }

    fun onRemarksChange(value: String) {
        state.update { it.copy(remarks = value) }
    }

    fun onNtnChange(value: String) {
        state.update { it.copy(ntn = value) }
    }

    fun onStnChange(value: String) {
        state.update { it.copy(stn = value) }
    }

    fun onCnicChange(value: String) {
        state.update { it.copy(cnic = value) }
    }

    fun onDueDaysChange(value: String) {
        state.update { it.copy(dueDays = value) }
    }

    fun onCategoryIdChange(value: Long) {
        state.update { it.copy(categoryId = value) }
    }

    fun onSupplierIdChange(value: Long) {
        state.update { it.copy(supplierId = value) }
    }

    fun onCategoryNameChange(value: String) {
        state.update { it.copy(categoryName = value) }
    }

    fun onSupplierNameChange(value: String) {
        state.update { it.copy(supplierName = value) }
    }

    fun onDiscChange(value: String) {
        state.update { it.copy(disc = value) }
    }

    fun onIsDiscRsPerChange(value: Boolean) {
        state.update { it.copy(isDiscRsPer = value) }
    }

    fun onOpeningBalanceChange(value: String) {
        state.update { it.copy(openingBalance = value) }
    }

    fun onIsRetailChange(value: Boolean) {
        state.update { it.copy(isRetail = value) }
    }

    fun onIsCreditChange(value: Boolean) {
        state.update { it.copy(isCredit = value) }
    }

    fun setHasLoadedOnce(value: Boolean) {
        state.update { it.copy(hasLoadedOnce = value) }
    }

    // endregion

    // region Network calls
    fun insertOrUpdateData(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            if (state.value.isUploadingImage)
                return@launch

            if (!isValid())
                return@launch

            state.update { it.copy(isSaving = true) }

            val customer = getFormData()

            val result = if (state.value.isUpdate) {
                customer.id = state.value.updateId
                api.updateCustomer(customer)
            } else {
                api.insertCustomer(customer)
            }

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    HP.customers =
                        Gson().getListOf<DropdownItem>(result.data.get("customers").asJsonArray)
//                    clearTextboxes()
                    onSuccess()
                }
            }
        }
    }

    fun deleteData(id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            if (state.value.isUploadingImage)
                return@launch

            beforeRequest()

            when (val result = api.deleteCustomer(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    HP.customers =
                        Gson().getListOf<DropdownItem>(result.data.get("customers").asJsonArray)
                    onSuccess()
                }
            }
        }
    }

    fun editData(id: Long) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            when (val result = api.getCustomer(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val customer = Gson().get<Accounts>(result.data.asJsonObject)
                    setFormData(customer)
                }
            }
        }
    }

    fun uploadImage(multipart: MultipartBody.Part) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if (state.value.isSaving)
                return@launch

            if (state.value.isUploadingImage)
                return@launch

            state.update { it.copy(isUploadingImage = true) }

            when (val result = mainRepo.uploadImage(multipart)) {
                is Resource.Error -> {
                    state.update { it.copy(isUploadingImage = false) }
                    showError(result.error)
                }

                is Resource.Information -> {
                    state.update { it.copy(isUploadingImage = false) }
                    showMessage(result.message)
                }

                is Resource.Success -> {
                    val fileName = result.data.asJsonObject.get("fileName").asString
                    state.update {
                        it.copy(
                            isUploadingImage = false,
                            imageUrl = fileName,
                        )
                    }
                }
            }
        }
    }

    fun deleteImage(imageUrl: String) {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            if(imageUrl.isEmpty())
                return@launch

            beforeRequest()

            when (val result = mainRepo.deleteImage(imageUrl)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    state.update { it.copy(imageUrl = "") }
                }
            }
        }
    }

    // endregion

    // region Methods
    private fun getFormData(): Accounts {
        return Accounts(
            accountName = state.value.accountName,
            address = state.value.address,
            contact = state.value.contact,
            city = state.value.city,
            email = state.value.email,
            remarks = state.value.remarks,
            ntn = state.value.ntn,
            stn = state.value.stn,
            cnic = state.value.cnic,
            dueDays = HP.getIntValue(state.value.dueDays),

            categoryId = state.value.categoryId,
            supplierId = state.value.supplierId,

            disc = HP.getDoubleValue(state.value.disc),
            isDiscRsPer = state.value.isDiscRsPer,
            openingBalance = HP.getDoubleValue(state.value.openingBalance),

            isRetail = state.value.isRetail,
            isCredit = state.value.isCredit,

            imageUrl = state.value.imageUrl,
        )
    }

    private fun setFormData(customer: Accounts) {
        state.update {
            it.copy(
                accountName = customer.accountName.toString(),
                address = customer.address.toString(),
                contact = customer.contact.toString(),
                city = customer.city.toString(),
                email = customer.email.toString(),
                remarks = customer.remarks.toString(),
                ntn = customer.ntn.toString(),
                stn = customer.stn.toString(),
                cnic = customer.cnic.toString(),
                dueDays = customer.dueDays.toString(),

                categoryId = customer.categoryId!!,
                supplierId = customer.supplierId!!,

                disc = customer.disc.toString(),
                isDiscRsPer = customer.isDiscRsPer!!,

                isRetail = customer.isRetail!!,
                isCredit = customer.isCredit!!,

                imageUrl = customer.imageUrl!!,

                // Extras
                openingBalanceTBEnabled = false,
            )
        }
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                accountName = "",
                address = "",
                contact = "",
                city = "",
                email = "",
                remarks = "",
                ntn = "",
                stn = "",
                cnic = "",
                dueDays = "",

                categoryId = 0L,
                supplierId = 0L,

                disc = "",
                isDiscRsPer = HP.settings.isDefaultDiscRs!!,
                openingBalance = "",

                isRetail = false,
                isCredit = false,

                imageUrl = "",

                // Extras
                isUpdate = false,
                updateId = 0L,

                openingBalanceTBEnabled = true,
            )
        }
    }

    private fun isValid(): Boolean {
        if (state.value.accountName.isEmpty()) {
            showMessage("Please enter customer name")
            return false
        }

        return true
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

    fun updateInitialState(isUpdate: Boolean, updateId: Long) {
        state.update { it.copy(isUpdate = isUpdate, updateId = updateId) }
    }

    // endregion
}