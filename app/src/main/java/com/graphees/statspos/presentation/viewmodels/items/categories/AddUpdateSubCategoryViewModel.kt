package com.graphees.statspos.presentation.viewmodels.items.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.domain.models.items.SubCategories
import com.graphees.statspos.domain.repository.items.CategoriesRepository
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
class AddUpdateSubCategoryViewModel @Inject constructor(
    private val api: CategoriesRepository,
    private val mainRepo: MainRepository,
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val id: Long = 0L,
        val categoryId: Long = 0L,
        val subCategoryName: String = "",
        val imageUrl: String = "",

        // Extra
        val categoryName: String = "",
        val isUpdate: Boolean = false,
        val updateId: Long = 0L,

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
    fun onCategoryNameChange(value: String) {
        state.update { it.copy(categoryName = value) }
    }
    fun onCategoryIdChange(value: Long) {
        state.update { it.copy(categoryId = value) }
    }
    fun onSubCategoryNameChange(value: String) {
        state.update { it.copy(subCategoryName = value) }
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

            if (!isValid())
                return@launch

            state.update { it.copy(isSaving = true) }

            val subCategory = getFormData()

            val result = if (state.value.isUpdate) {
                subCategory.id = state.value.updateId
                api.updateSubCategory(subCategory)
            } else {
                api.insertSubCategory(subCategory)
            }

            state.update { it.copy(isSaving = false) }

            when (result) {
                is Resource.Error -> showError(result.error)
                is Resource.Information -> showMessage(result.message)
                is Resource.Success -> {
                    HP.subCategories = Gson().getListOf<DropdownItem>(result.data.get("subCategories").asJsonArray)
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

            beforeRequest()

            when (val result = api.deleteSubCategory(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    HP.subCategories = Gson().getListOf<DropdownItem>(result.data.get("subCategories").asJsonArray)
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

            when (val result = api.getSubCategory(id)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()
                    val subCategory = Gson().get<SubCategories>(result.data.asJsonObject)
                    setFormData(subCategory)
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
    private fun getFormData(): SubCategories {
        return SubCategories(
            categoryId = state.value.categoryId,
            subCategoryName = state.value.subCategoryName,
            imageUrl = state.value.imageUrl,
        )
    }

    private fun setFormData(subCategory: SubCategories) {
        state.update {
            it.copy(
                subCategoryName =  subCategory.subCategoryName.toString(),
                imageUrl = subCategory.imageUrl!!,
            )
        }
    }

    private fun clearTextboxes() {
        state.update {
            it.copy(
                categoryId = 0L,
                subCategoryName = "",
                imageUrl = "",
                categoryName = "",

                isUpdate = false,
                updateId = 0L,
            )
        }
    }

    private fun isValid(): Boolean {
        if (state.value.categoryId == 0L) {
            showMessage("Please select category")
            return false
        }

        if (state.value.subCategoryName.isEmpty()) {
            showMessage("Please enter sub-category name")
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

    fun updateInitialState(isUpdate: Boolean, updateId: Long, categoryId: Long) {
        state.update { it.copy(
            isUpdate = isUpdate,
            updateId = updateId,
            categoryId = categoryId,
            categoryName = if(categoryId == 0L) "" else HP.getDropdownNameById(categoryId, HP.categories)
        ) }
    }

    // endregion
}