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

enum class PasswordFor {
    DELETE_ITEM,
    DELETE_ACCOUNT,
    EDIT_SALES_BILL,
    EDIT_PURCHASE_BILL,
    DELETE_SALES_BILL,
    DELETE_PURCHASE_BILL,
    DELETE_ENTRY,
    PRINT_DUPLICATES,
    AUDIT,
}

enum class EntryType {
    RECEIPT,
    PAYMENT,
    EXPENSE,
    SALES,
    SALES_RETURN,
    PURCHASE,
    PURCHASE_RETURN,
    JOURNAL,
    STOCK,
}

enum class FixedAccounts {
    CASH,
    SALES,
    SALES_RETURN,
    PURCHASE,
    PURCHASE_RETURN,
}

enum class UserTypes {
    ADMINISTRATOR,
    POS_USER,
    INVENTORY_MANAGER,
}

fun getEntryType(entryType: EntryType): Int {
    return entryType.ordinal + 1
}

fun getFixedAccount(fixedAccount: FixedAccounts): Long {
    return (fixedAccount.ordinal + 1).toLong()
}

fun getUserType(userType: UserTypes): Long {
    return (userType.ordinal + 1).toLong()
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
    onError: (String) -> Unit,
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
            onMessage(event.message)
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

inline fun <reified T> Gson.getListOf(jsonArray: JsonArray): List<T> =
    fromJson(jsonArray, object : TypeToken<List<T>>() {}.type)

inline fun <reified T> Gson.get(jsonObject: JsonObject): T =
    fromJson(jsonObject, object : TypeToken<T>() {}.type)
