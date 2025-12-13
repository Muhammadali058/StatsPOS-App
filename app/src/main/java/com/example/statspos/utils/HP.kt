package com.example.statspos.utils

import android.content.Context
import android.widget.Toast

fun Context.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, msg, length).show()

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String?) : Resource<T>()
    data class Information<T>(val infoMessage: String?) : Resource<T>()
//    data class Loading<T>(val isLoading: Boolean = true) : Resource<Nothing>()
}

object HP {
    const val HOST = "http://192.168.100.28:8000/"
    const val API = "${HOST}api/"
}