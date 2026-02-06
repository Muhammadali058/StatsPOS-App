package com.example.statspos.presentation.viewmodels.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.items.Items
import com.example.statspos.domain.repository.items.ItemsRepository
import com.example.statspos.domain.repository.main.MainRepository
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
import okhttp3.MultipartBody
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddUpdateItemViewModel @Inject constructor(
    private val api: ItemsRepository,
    private val mainRepo: MainRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val itemname: String = "",
        val barcode: String = "",
        val urduname: String = "",
        val refCode: String = "",

        val cost: String = "",
        val retail: String = "",
        val wholesale: String = "",
        val rate3: String = "",
        val rate4: String = "",
        val crtnRate: String = "",
        val crtnSize: String = "",
        val marketPrice: String = "",

        val isDiscRsPer: Boolean = false,
        val disc: String = "",
        val expirable: Boolean = false,
        val expiry: LocalDate = LocalDate.now(),

        val stockWarningMin: String = "",
        val stockWarningMax: String = "",
        val maxSalePcs: String = "",
        val maxSaleCrtn: String = "",

        val openingCost: Double = 0.0,
        val openingStockPcs: String = "",
        val openingStockCrtn: String = "",
        val openingCrtnSize: Int = 0,

        val currentStockPcs: String = "",
        val currentStockCrtn: String = "",

        val repeatable: Boolean = false,
        val searchable: Boolean = true,
        val changeable: Boolean = false,
        val button: Boolean = false,
        val lockPcs: Boolean = false,
        val lockCrtn: Boolean = false,
        val saleUnderStock: Boolean = true,

        val categoryId: Long = 0L,
        val subCategoryId: Long = 0L,
        val vendorId: Long = 0L,

        val packing: String = "",
        val location: String = "",
        val imageUrl: String = "",

        // Extras
        val categoryName: String = "",
        val subCategoryName: String = "",
        val vendorName: String = "",

        val isUpdate: Boolean = false,
        val updateId: Long = 0L,
        val item: Items? = null,
        val openingStockPcsTBEnabled: Boolean = true,
        val openingStockCrtnTBEnabled: Boolean = true,

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
    fun onItemnameChange(value: String) {
        state.update { it.copy(itemname = value) }
    }

    fun onUrdunameChange(value: String) {
        state.update { it.copy(urduname = value) }
    }

    fun onBarcodeChange(value: String) {
        state.update { it.copy(barcode = value) }
    }

    fun onRefCodeChange(value: String) {
        state.update { it.copy(refCode = value) }
    }

    fun onCostChange(value: String) {
        state.update { it.copy(cost = value) }
    }

    fun onRetailChange(value: String) {
        state.update { it.copy(retail = value) }
    }

    fun onWholesaleChange(value: String) {
        state.update { it.copy(wholesale = value) }
    }

    fun onRate3Change(value: String) {
        state.update { it.copy(rate3 = value) }
    }

    fun onRate4Change(value: String) {
        state.update { it.copy(rate4 = value) }
    }

    fun onCrtnRateChange(value: String) {
        state.update { it.copy(crtnRate = value) }
    }

    fun onCrtnSizeChange(value: String) {
        state.update { it.copy(crtnSize = value) }
    }

    fun onMarketPriceChange(value: String) {
        state.update { it.copy(marketPrice = value) }
    }

    fun onCategoryIdChange(value: Long) {
        state.update { it.copy(categoryId = value) }
    }

    fun onSubCategoryIdChange(value: Long) {
        state.update { it.copy(subCategoryId = value) }
    }

    fun onVendorIdChange(value: Long) {
        state.update { it.copy(vendorId = value) }
    }

    fun onCategoryNameChange(value: String) {
        state.update { it.copy(categoryName = value) }
    }

    fun onSubCategoryNameChange(value: String) {
        state.update { it.copy(subCategoryName = value) }
    }

    fun onVendorNameChange(value: String) {
        state.update { it.copy(vendorName = value) }
    }

    fun onStockWarningMinChange(value: String) {
        state.update { it.copy(stockWarningMin = value) }
    }

    fun onStockWarningMaxChange(value: String) {
        state.update { it.copy(stockWarningMax = value) }
    }

    fun onMaxSalePcsChange(value: String) {
        state.update { it.copy(maxSalePcs = value) }
    }

    fun onMaxSaleCrtnChange(value: String) {
        state.update { it.copy(maxSaleCrtn = value) }
    }

    fun onOpeningStockPcsChange(value: String) {
        state.update { it.copy(openingStockPcs = value) }
    }

    fun onOpeningStockCrtnChange(value: String) {
        state.update { it.copy(openingStockCrtn = value) }
    }

    fun onCurrentStockPcsChange(value: String) {
        state.update { it.copy(currentStockPcs = value) }
    }

    fun onCurrentStockCrtnChange(value: String) {
        state.update { it.copy(currentStockCrtn = value) }
    }

    fun onExpirableChange(value: Boolean) {
        state.update { it.copy(expirable = value) }
    }

    fun onExpiryChange(value: LocalDate) {
        state.update { it.copy(expiry = value) }
    }

    fun onDiscChange(value: String) {
        state.update { it.copy(disc = value) }
    }

    fun onIsDiscRsPerChange(value: Boolean) {
        state.update { it.copy(isDiscRsPer = value) }
    }

    fun onPackingChange(value: String) {
        state.update { it.copy(packing = value) }
    }

    fun onLocationChange(value: String) {
        state.update { it.copy(location = value) }
    }

    fun onChangeableChange(value: Boolean) {
        state.update { it.copy(changeable = value) }
    }

    fun onRepeatableChange(value: Boolean) {
        state.update { it.copy(repeatable = value) }
    }

    fun onLockPcsChange(value: Boolean) {
        state.update { it.copy(lockPcs = value) }
    }

    fun onLockCrtnChange(value: Boolean) {
        state.update { it.copy(lockCrtn = value) }
    }

    fun onButtonChange(value: Boolean) {
        state.update { it.copy(button = value) }
    }

    fun onSearchableChange(value: Boolean) {
        state.update { it.copy(searchable = value) }
    }

    fun onSaleUnderStockChange(value: Boolean) {
        state.update { it.copy(saleUnderStock = value) }
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

            val item = getFormData()

            val result = if (state.value.isUpdate) {
                item.id = state.value.updateId
                api.updateItem(item)
            } else {
                api.insertItem(item)
            }

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> {
                    if(result.data != null){
                        val message = result.message + " ${result.data.get("data").asJsonObject.get("itemname").asString}"
                        showMessage(message)
                    }else{
                        showMessage(result.message)
                    }
                }
                is Resource.Success -> {
                    val item = Gson().get<Items>(result.data.asJsonObject)
                    if(!HP.autoCompleteItems.contains(item.itemname))
                        HP.autoCompleteItems = (HP.autoCompleteItems + listOf(item.itemname)) as List<String>

                    clearTextboxes()
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

            when (val result = api.deleteItem(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

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

            when (val result = api.getItem(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val item = Gson().get<Items>(result.data.asJsonObject)
                    setFormData(item)
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
                    state.update { it.copy(
                        isUploadingImage = false,
                        imageUrl = fileName,
                    ) }
                }
            }
        }
    }
    // endregion

    // region Methods
    private fun getFormData(): Items {
        val item = Items(
            barcode = state.value.barcode,
            refCode = state.value.refCode,
            itemname = state.value.itemname,
            urduname = state.value.urduname,
            cost = HP.getDoubleValue(state.value.cost),
            retail = HP.getDoubleValue(state.value.retail),
            wholesale = HP.getDoubleValue(state.value.wholesale),
            rate3 = HP.getDoubleValue(state.value.rate3),
            rate4 = HP.getDoubleValue(state.value.rate4),
            crtnRate = HP.getDoubleValue(state.value.crtnRate),
            crtnSize = HP.getIntValue(state.value.crtnSize),
            marketPrice = HP.getDoubleValue(state.value.marketPrice),

            categoryId = state.value.categoryId,
            subCategoryId = state.value.subCategoryId,
            vendorId = state.value.vendorId,

            stockWarningMin = HP.getIntValue(state.value.stockWarningMin),
            stockWarningMax = HP.getIntValue(state.value.stockWarningMax),
            maxSalePcs = HP.getIntValue(state.value.maxSalePcs),
            maxSaleCrtn = HP.getIntValue(state.value.maxSaleCrtn),

            expirable = state.value.expirable,
            expiry = HP.getZonedDate(state.value.expiry),
            disc = HP.getDoubleValue(state.value.disc),
            isDiscRsPer = state.value.isDiscRsPer,
            packing = state.value.packing,
            location = state.value.location,

            changeable = state.value.changeable,
            repeatable = state.value.repeatable,
            lockPcs = state.value.lockPcs,
            lockCrtn = state.value.lockCrtn,
            button = state.value.button,
            searchable = state.value.searchable,
            saleUnderStock = state.value.saleUnderStock,

            imageUrl = state.value.imageUrl,
        )

        if (!state.value.isUpdate) {
            item.openingCost = HP.getDoubleValue(state.value.cost)
            item.openingStockPcs = HP.getDoubleValue(state.value.openingStockPcs)
            item.openingStockCrtn = HP.getLongValue(state.value.openingStockCrtn)
            item.openingCrtnSize = HP.getIntValue(state.value.crtnSize)
        }

        return item
    }

    private fun setFormData(item: Items) {
        state.update {
            it.copy(
                item = item,

                barcode = item.barcode.toString(),
                refCode = item.refCode.toString(),
                itemname = item.itemname.toString(),
                urduname = item.urduname.toString(),

                cost = item.cost.toString(),
                marketPrice = item.marketPrice.toString(),
                retail = item.retail.toString(),
                wholesale = item.wholesale.toString(),
                rate3 = item.rate3.toString(),
                rate4 = item.rate4.toString(),
                crtnRate = item.crtnRate.toString(),
                crtnSize = item.crtnSize.toString(),

                categoryId = item.categoryId!!,
                subCategoryId = item.subCategoryId!!,
                vendorId = item.vendorId!!,
                categoryName = item.categoryName.toString(),
                subCategoryName = item.subCategoryName.toString(),
                vendorName = item.vendorName.toString(),

                stockWarningMin = item.stockWarningMin.toString(),
                stockWarningMax = item.stockWarningMax.toString(),
                maxSalePcs = item.maxSalePcs.toString(),
                maxSaleCrtn = item.maxSaleCrtn.toString(),
                openingStockPcs = item.openingStockPcs.toString(),
                openingStockCrtn = item.openingStockCrtn.toString(),
                currentStockPcs = item.stockPcs.toString(),
                currentStockCrtn = item.stockCrtn.toString(),

                expirable = item.expirable!!,
                expiry = HP.toLocalDate(item.expiry.toString()),
                disc = item.disc.toString(),
                isDiscRsPer = item.isDiscRsPer!!,
                packing = item.packing.toString(),
                location = item.location.toString(),

                changeable = item.changeable!!,
                repeatable = item.repeatable!!,
                lockPcs = item.lockPcs!!,
                lockCrtn = item.lockCrtn!!,
                button = item.button!!,
                searchable = item.searchable!!,
                saleUnderStock = item.saleUnderStock!!,

                imageUrl = item.imageUrl!!,

                // Extras
                openingStockPcsTBEnabled = false,
                openingStockCrtnTBEnabled = false,
            )
        }
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                barcode = "",
                refCode = "",
                itemname = "",
                urduname = "",

                cost = "",
                marketPrice = "",
                retail = "",
                wholesale = "",
                rate3 = "",
                rate4 = "",
                crtnRate = "",
                crtnSize = "",

                categoryId = 0L,
                subCategoryId = 0L,
                vendorId = 0L,
                categoryName = "",
                subCategoryName = "",
                vendorName = "",

                stockWarningMin = "",
                stockWarningMax = "",
                maxSalePcs = "",
                maxSaleCrtn = "",
                openingStockPcs = "",
                openingStockCrtn = "",
                currentStockPcs = "",
                currentStockCrtn = "",

                expirable = false,
                expiry = LocalDate.now(),
                disc = "",
                isDiscRsPer = false,
                packing = "",
                location = "",

                changeable = false,
                repeatable = false,
                lockPcs = false,
                lockCrtn = false,
                button = false,
                searchable = true,
                saleUnderStock = true,

                imageUrl = "",

                // Extras
                item = null,
                isUpdate = false,
                updateId = 0L,
                openingStockPcsTBEnabled = true,
                openingStockCrtnTBEnabled = true,
            )
        }
    }

    private fun isValid(): Boolean {
        if (state.value.barcode.isEmpty()) {
            showMessage("Please enter barcode")
            return false
        }

        if (state.value.itemname.isEmpty()) {
            showMessage("Please enter itemname")
            return false
        }

        return isValidCrtnSize()
    }

    private fun isValidCrtnSize(): Boolean{
        var isAllOk = false

        val retail = HP.getDoubleValue(state.value.retail)
        val wholesale = HP.getDoubleValue(state.value.wholesale)
        val crtnRate = HP.getDoubleValue(state.value.crtnRate)
        val crtnSize = HP.getIntValue(state.value.crtnSize)

        if(retail == 0.0 && wholesale == 0.0 && crtnRate != 0.0){
            state.update { it.copy(crtnSize = "1") }
            isAllOk = true
        }
        else if(retail == 0.0 && wholesale == 0.0 && crtnRate == 0.0){
            state.update { it.copy(crtnSize = "0") }
            isAllOk = true
        }
        else if((retail != 0.0 || wholesale != 0.0) && crtnRate == 0.0){
            state.update { it.copy(crtnSize = "0") }
            isAllOk = true
        }
        else if((retail != 0.0 || wholesale != 0.0) && crtnRate != 0.0){
            if(state.value.crtnSize.isEmpty()){
                showMessage("Please enter PCS in carton")
                isAllOk = false
            }else{
                if(crtnSize < 2){
                    showMessage("PCS in carton should be more than 1")
                    isAllOk = false
                }else{
                    isAllOk = true
                }
            }
        }

        return isAllOk
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