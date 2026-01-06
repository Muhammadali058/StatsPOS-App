package com.example.statspos.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken


enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

fun Context.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, msg, length).show()

sealed class UiEvent {
    data object Idle : UiEvent()
    data class ShowSnackbar(
        val message: String,
        val type: SnackbarType = SnackbarType.INFORMATION
    ) : UiEvent()

    data class ShowMessage(val message: String) : UiEvent()
    data class ShowError(val error: String) : UiEvent()
}

enum class SnackbarType { INFORMATION, ERROR }

suspend fun checkEvent(
    event: UiEvent,
    snackbarHostState: SnackbarHostState,
    onError: (String) -> Unit = {},
    onMessage: (String) -> Unit = {},
    viewModelIdleEvent: (event: UiEvent) -> Unit,
//    changeSnackbarType: (SnackbarType) -> Unit
) {
    when (event) {
        is UiEvent.ShowSnackbar -> {

//            val snackbarType = when (event.type) {
//                SnackbarType.INFORMATION -> SnackbarType.INFORMATION
//                else -> SnackbarType.ERROR
//            }
//            changeSnackbarType(snackbarType)

            snackbarHostState.showSnackbar(
                message = event.message,
                withDismissAction = true,
            )
            viewModelIdleEvent(UiEvent.Idle)
//            viewModel.onEvent(UiEvent.Idle)
        }

        is UiEvent.ShowMessage -> {
            onError(event.message)
            viewModelIdleEvent(UiEvent.Idle)
//            viewModel.onEvent(UiEvent.Idle)
        }

        is UiEvent.ShowError -> {
            onError(event.error)
            viewModelIdleEvent(UiEvent.Idle)
//            viewModel.onEvent(UiEvent.Idle)
        }

        else -> {}
    }
}

inline fun <reified T> Gson.getListOf(jsonArray: String): List<T> =
    fromJson(jsonArray, object : TypeToken<List<T>>() {}.type)

inline fun <reified T> Gson.getListOf(jsonArray: JsonArray): List<T> =
    fromJson(jsonArray, object : TypeToken<List<T>>() {}.type)

inline fun <reified T> Gson.get(jsonObject: JsonObject): T =
    fromJson(jsonObject, object : TypeToken<T>() {}.type)
