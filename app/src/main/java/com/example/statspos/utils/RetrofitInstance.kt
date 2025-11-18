package com.example.statspos.utils

import com.example.statspos.api.CategoriesApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val host = "http://192.168.100.28:8000"
    private const val apiUrl = "$host/api/"

    private fun getInstance(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getCategoriesApi(): CategoriesApi {
        return getInstance().create(CategoriesApi::class.java)
    }


}