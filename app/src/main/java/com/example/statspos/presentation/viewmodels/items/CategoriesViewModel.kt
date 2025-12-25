package com.example.statspos.presentation.viewmodels.items

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.items.Categories
import com.example.statspos.domain.repository.items.CategoriesRepository
import com.example.statspos.utils.Resource
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {
    // region ScreenState

    data class ScreenState(
        val categories: List<Categories> = emptyList(),

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
            is UiEvent.ShowSnackbar -> {}
            else -> {
                viewModelScope.launch {
                    _event.send(UiEvent.Idle)
                }
            }
        }
    }

    fun showSnackbar(message: String, type: SnackbarType = SnackbarType.INFORMATION) {
        viewModelScope.launch {
            _event.send(UiEvent.ShowSnackbar(message, type))
        }
    }

    // endregion

    // region Network calls
    fun loadCategories() {
        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            val params = JsonObject().apply {
                addProperty("text", "")
                addProperty("clientId", 1)
                addProperty("branchId", 1)
                addProperty("branchGroupId", 0)
            }

            when (val result = categoriesRepository.loadCategories(params)) {
                is Resource.Error -> resultError(result.message)
                is Resource.Information -> resultInformation(result.infoMessage)
                is Resource.Success -> {
                    resultSuccess()

                    val jsonArray = result.data.getAsJsonArray("rows") ?: emptyList()

                    val categories = mutableListOf<Categories>()
                    for (a in jsonArray) {
                        val cat = Gson().fromJson(a, Categories::class.java)
                        categories.add(cat)
                    }

                    state.update {
                        it.copy(
                            isLoading = false,
                            categories = categories
                        )
                    }
                }
            }
        }
    }

    fun uploadImage(file: File) {
        // Call this method in compose in button onClick
//        val context = LocalContext.current
//        val file = File(context.cacheDir, "bearing1.jpg")
//        file.outputStream().use {
//            context.assets.open("bearing.jpg").copyTo(it)
//        }
//        viewModel.uploadImage(file)

        viewModelScope.launch {
            if (state.value.isLoading)
                return@launch

            beforeRequest()

            val params = JsonObject().apply {
                addProperty("clientId", 1)
                addProperty("branchId", 1)
            }

            val result = categoriesRepository.uploadImage(
                image = MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    file.asRequestBody()
                ),
                body = MultipartBody.Part.createFormData("data", params.toString())
            )

            when (result) {
                is Resource.Error -> resultError(result.message)
                is Resource.Information -> resultInformation(result.infoMessage)
                is Resource.Success -> {
                    resultSuccess()
                }
            }
        }
    }
    // endregion

    // region Others
    private fun resultError(message: String?) {
        state.update { it.copy(isLoading = false, error = message) }
        message?.let { showSnackbar(it, SnackbarType.ERROR) }
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