package com.example.statspos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.Categories
import com.example.statspos.domain.repository.CategoriesRepository
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    data class ScreenState(
        val isLoading: Boolean = false,
        val categories: List<Categories> = emptyList(),
//        val success: String? = null,
        val error: String? = null,
        val infoMessage: String? = null,
    )

    var state = MutableStateFlow(ScreenState())
        private set

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            state.value = state.value.copy(isLoading = true)

            val jsonObject = JsonObject().apply {
                addProperty("text", "")
                addProperty("clientId", 1)
                addProperty("branchId", 1)
                addProperty("branchGroupId", 0)
            }

            when (val result = categoriesRepository.loadCategories(jsonObject)) {
                is Resource.Success -> {
                    state.value = ScreenState(categories = result.data)
                }
                is Resource.Error -> {
                    state.value = ScreenState(error = result.message)
                }
                is Resource.Information -> {
                    state.value = ScreenState(infoMessage = result.infoMessage)
                }
                else -> Unit
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
            val jsonObject = JsonObject().apply {
                addProperty("clientId", 1)
                addProperty("branchId", 1)
            }

            val result = categoriesRepository.uploadImage(
                image = MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    file.asRequestBody()
                ),
                body = MultipartBody.Part.createFormData("data", jsonObject.toString())
            )

            when (result) {
                is Resource.Success -> {

                }
                is Resource.Error -> {
                    state.value = ScreenState(error = result.message)
                }
                else -> Unit
            }
        }
    }
}
