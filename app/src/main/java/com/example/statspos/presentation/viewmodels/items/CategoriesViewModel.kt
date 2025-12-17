package com.example.statspos.presentation.viewmodels.items

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.items.Categories
import com.example.statspos.domain.repository.items.CategoriesRepository
import com.example.statspos.utils.Resource
import com.google.gson.Gson
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
        val error: String? = null,
        val infoMessage: String? = null,
    )

    var state = MutableStateFlow(ScreenState())
        private set

    init {
        loadCategories()
    }

    fun initState(){
        state.value = state.value.copy(
            isLoading = true,
            error = null,
            infoMessage = null,
        )
    }

    fun loadCategories() {
        viewModelScope.launch {
            if(state.value.isLoading)
                return@launch

            initState()

            val params = JsonObject().apply {
                addProperty("text", "")
                addProperty("clientId", 1)
                addProperty("branchId", 1)
                addProperty("branchGroupId", 0)
            }

            when (val result = categoriesRepository.loadCategories(params)) {
                is Resource.Error -> {
                    state.value = ScreenState(error = result.message)
                }
                is Resource.Information -> {
                    state.value = ScreenState(infoMessage = result.infoMessage)
                }
                is Resource.Success -> {
                    val jsonArray = result.data.getAsJsonArray("rows") ?: emptyList()

                    val categories = mutableListOf<Categories>()
                    for (a in jsonArray) {
                        val cat = Gson().fromJson(a, Categories::class.java)
                        categories.add(cat)
                    }

                    state.value = state.value.copy(
                        isLoading = false,
                        categories = categories
                    )
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
                is Resource.Error -> {
                    state.value = ScreenState(error = result.message)
                }
                is Resource.Information -> {
                    state.value = ScreenState(infoMessage = result.infoMessage)
                }
                is Resource.Success -> {
                    state.value = state.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
}