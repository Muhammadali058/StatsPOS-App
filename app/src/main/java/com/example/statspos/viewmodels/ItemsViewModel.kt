package com.example.statspos.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.statspos.models.Categories
import com.example.statspos.utils.RetrofitInstance
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CategoriesViewModel: ViewModel() {
    val categoriesApi by lazy { RetrofitInstance.getCategoriesApi() }

    fun loadCategories(){
        val jsonObject = JsonObject().apply {
            addProperty("text", "")
            addProperty("clientId", 1)
            addProperty("branchId", 1)
            addProperty("branchGroupId", 0)
        }

        viewModelScope.launch {
//            Call this method in compose in button onClick
//            viewModel.loadCategories()
            try {
                val result = categoriesApi.loadCategories(jsonObject)

                if(result.isSuccessful){
                    val body = result.body()
                    body?.let {
                        val abc = it.getAsJsonArray("rows")
                        for (a in abc){
                            val cat = Gson().fromJson(a, Categories::class.java)
                            Log.d("Abc", cat.imageUrl)
                        }
                    }
                }else{
                    val errorBodyString = result.errorBody()?.string()
                    Log.d("Error", errorBodyString.toString())
                }
            }catch (e: Exception){
                Log.d("Error", e.localizedMessage.toString())
            }
        }
    }

    fun uploadImage(file: File){
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

            val result = categoriesApi.uploadImage(
                image = MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    file.asRequestBody()
                ),
                body = MultipartBody.Part.createFormData("data", jsonObject.toString())
            )

            if(result.isSuccessful){
                val body = result.body()
                Log.d("Result", body.toString())
            }else{
                val errorBodyString = result.errorBody()?.string()
                Log.d("Error", errorBodyString.toString())
            }
        }
    }
}