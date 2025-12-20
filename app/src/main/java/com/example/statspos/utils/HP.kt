package com.example.statspos.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.viewModelScope
import com.example.statspos.domain.models.main.LocalBranches
import com.example.statspos.domain.models.main.LocalClients
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

object HP {
    var localClient: LocalClients? = null
}

fun Context.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, msg, length).show()

sealed class UiEvent {
    data object Idle : UiEvent()
    data class ShowSnackbar(val message: String) : UiEvent()
    data class ShowError(val message: String) : UiEvent()
}

suspend fun checkEvent(
    event: UiEvent,
    snackbarHostState: SnackbarHostState,
    viewModelIdleEvent: (event: UiEvent) -> Unit,
    onError: () -> Unit,
) {
    when (event) {
        is UiEvent.ShowSnackbar -> {
            snackbarHostState.showSnackbar(
                message = event.message,
                withDismissAction = true,
            )
            viewModelIdleEvent(UiEvent.Idle)
//            viewModel.onEvent(UiEvent.Idle)
        }

        is UiEvent.ShowError -> {
            viewModelIdleEvent(UiEvent.Idle)
//            viewModel.onEvent(UiEvent.Idle)
            onError()
//            showErrorDialog = true
        }

        else -> {}
    }
}
