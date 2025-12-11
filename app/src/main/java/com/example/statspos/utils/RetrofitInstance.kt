package com.example.statspos.utils

import com.example.statspos.api.CategoriesApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private fun getInstance(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(HP.apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getCategoriesApi(): CategoriesApi {
        return getInstance().create(CategoriesApi::class.java)
    }


}