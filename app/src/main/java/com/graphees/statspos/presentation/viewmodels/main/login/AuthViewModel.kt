package com.graphees.statspos.presentation.viewmodels.main.login

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.SnackbarType
import com.graphees.statspos.utils.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {
    // region ScreenState
    data class ScreenState(
        val sendingOtp: Boolean = false,
        val verifyingOtp: Boolean = false,

        val isLoading: Boolean = false,
        val message: String? = null,
        val error: String? = null,
    )

    var state = MutableStateFlow(ScreenState())
        private set

    fun beforeRequest() {
        state.update { it.copy(isLoading = true, error = null) }
    }

    private val _event = Channel<UiEvent>()
    var event = _event.receiveAsFlow()
    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ShowSnackbar -> {
                viewModelScope.launch {
                    _event.send(UiEvent.ShowSnackbar(event.message, event.type))
                }
            }

            is UiEvent.ShowMessage -> {
                viewModelScope.launch {
                    _event.send(UiEvent.ShowMessage(event.message))
                }
            }

            is UiEvent.ShowError -> {
                viewModelScope.launch {
                    _event.send(UiEvent.ShowError(event.error))
                }
            }

            else -> {
                viewModelScope.launch {
                    _event.send(UiEvent.Idle)
                }
            }
        }
    }

    fun showSnackbar(message: String, type: SnackbarType = SnackbarType.INFORMATION) {
        onEvent(UiEvent.ShowSnackbar(message, type))
    }

    fun showMessage(message: String?) {
        showSnackbar(message ?: "")

//        state.update { it.copy(isLoading = false, error = null, message = message) }
//        onEvent(UiEvent.ShowMessage(message ?: ""))
    }

    fun showError(error: String?) {
        state.update { it.copy(error = error) }
        onEvent(UiEvent.ShowError(error ?: ""))
    }

    // endregion

    private val auth = FirebaseAuth.getInstance()

    var verificationId by mutableStateOf<String?>(null)
        private set

    var otpAutoVerified by mutableStateOf(false)

    fun sendOtp(
        phone: String,
        activity: Activity,
        onOtpSent: () -> Unit,
        onAutoVerified: () -> Unit,
        onOtpFailed: (String) -> Unit,
    ) {
        state.update { it.copy(sendingOtp = true) }
        Log.d("TAG Otp sent", "sendOtp: Called")
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // ✅ AUTO VERIFIED (NO OTP SCREEN NEEDED)
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { result ->
                        state.update { it.copy(sendingOtp = false) }

                        if (result.isSuccessful) {
                            otpAutoVerified = true
                            onAutoVerified()
                        }
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("TAG Otp failed", e.message.toString())
                state.update { it.copy(sendingOtp = false) }
                onOtpFailed(e.message.toString())
            }

            override fun onCodeSent(
                verId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("TAG Otp sent", "onCodeSent")
                state.update { it.copy(sendingOtp = false) }
                verificationId = verId
                onOtpSent()
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(HP.getContact(phone))
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(
        otp: String,
        onSuccess: () -> Unit
    ) {
        state.update { it.copy(verifyingOtp = true) }
        val verId = verificationId ?: return

        val credential = PhoneAuthProvider.getCredential(verId, otp)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { result ->
                state.update { it.copy(verifyingOtp = false) }

                if (result.isSuccessful) {
                    onSuccess()
                }
            }
            .addOnFailureListener { result ->
                state.update { it.copy(verifyingOtp = false) }
                showError(result.message)
            }
    }
}