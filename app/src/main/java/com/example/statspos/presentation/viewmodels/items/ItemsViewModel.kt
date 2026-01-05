package com.example.statspos.presentation.viewmodels.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.items.Categories
import com.example.statspos.domain.models.items.Items
import com.example.statspos.domain.repository.items.CategoriesRepository
import com.example.statspos.domain.repository.items.ItemsRepository
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.getListOf
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val itemsRepo: ItemsRepository
) : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val items: List<Items> = emptyList(),
        val categoryName: String = "",
        val subCategoryName: String = "",
        val categoryId: Long = 0.toLong(),
        val subCategoryId: Long = 0.toLong(),

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
            is UiEvent.ShowMessage -> {}
            else -> {
                viewModelScope.launch {
                    _event.send(UiEvent.Idle)
                }
            }
        }
    }

    fun showMessage(message: String, type: SnackbarType = SnackbarType.INFORMATION) {
        viewModelScope.launch {
            _event.send(UiEvent.ShowMessage(message, type))
        }
    }
    // endregion

    // region onChangeMethods
    fun onCategoryNameChange(value: String) {
        state.update { it.copy(categoryName = value) }
    }
    fun onSubCategoryNameChange(value: String) {
        state.update { it.copy(subCategoryName = value) }
    }
    fun onCategoryIdChange(value: Long) {
        state.update { it.copy(categoryId = value) }
    }
    fun onSubCategoryIdChange(value: Long) {
        state.update { it.copy(subCategoryId = value) }
    }
    // endregion

    // region Network calls
    fun loadItems() {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            val params = JsonObject().apply {
                addProperty("text", "")
            }

            when (val result = itemsRepo.loadItems(params)) {
                is Resource.Error -> resultError(result.error)
                is Resource.Information -> resultInformation(result.message)
                is Resource.Success -> {
                    resultSuccess()

                    val itemsList = Gson().getListOf<Items>(result.data.get("rows").asJsonArray)

                    state.update {
                        it.copy(
                            items = itemsList
                        )
                    }
                }
            }
        }
    }

    // endregion

    // region Others
    private fun resultError(error: String?) {
        state.update { it.copy(isLoading = false, error = error) }
        error?.let { showMessage(it, SnackbarType.ERROR) }
    }

    private fun resultInformation(message: String?) {
        state.update { it.copy(isLoading = false) }
        message?.let { showMessage(it) }
    }

    private fun resultSuccess() {
        state.update { it.copy(isLoading = false, error = null) }
    }
    // endregion
}