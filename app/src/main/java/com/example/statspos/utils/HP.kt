package com.example.statspos.utils

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

fun Context.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, msg, length).show()

sealed class UiEvent{
    data object Idle: UiEvent()
    data class ShowSnackbar(val message: String): UiEvent()
    data class ShowError(val message: String): UiEvent()
}

object HP {

}