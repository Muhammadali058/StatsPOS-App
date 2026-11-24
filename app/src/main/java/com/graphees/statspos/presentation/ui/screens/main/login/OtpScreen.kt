package com.graphees.statspos.presentation.ui.screens.main.login

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppSaveButton
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.viewmodels.main.login.AuthViewModel
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(
    authViewModel: AuthViewModel,
    contact:String,
    onOtpVerified: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as Activity
    val state by authViewModel.state.collectAsStateWithLifecycle()
    val event by authViewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }

    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = authViewModel::onEvent,
            onError = {
                showErrorDialog = true
            }
        )
    }

    if (showErrorDialog) {
        ErrorDialog(
            error = state.error,
            onDismiss = {
                showErrorDialog = false
            },
        )
    }


    fun verifyOtp(){
        authViewModel.verifyOtp(otp) {
            onOtpVerified()
        }
    }

    Scaffold(
        snackbarHost = {
            AppSnackbarHost(
                snackbarHostState = snackbarHostState,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(48.dp))

                Text(
                    text = "Verify your number",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Enter 6-digit code sent to your phone",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(24.dp))

                OtpInputField(
                    otpLength = 6,
                    onOtpComplete = {
                        otp = it
                    }
                )

                Spacer(Modifier.height(24.dp))

                ResendOtpSection(
                    initialTime = 60,
                    onResendClick = {
                        authViewModel.sendOtp(
                            phone = contact,
                            activity = activity,
                            onOtpSent = {

                            },
                            onAutoVerified = {
                                onOtpVerified()
                            },
                            onOtpFailed = { error ->
                                authViewModel.showError(error)
                            }
                        )
                    }
                )

                Spacer(Modifier.height(24.dp))

                if(state.verifyingOtp){
                    AppCircularProgressIndicator()
                }else {
                    AppSaveButton(
                        text = "Verify",
                        onClick = {
                            if (otp.isEmpty() || otp.length < 6)
                                return@AppSaveButton

                            verifyOtp()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OtpInputField(
    otpLength: Int = 6,
    onOtpComplete: (String) -> Unit
) {

    var otp by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // 🔥 Hidden text field (real input handler)
    BasicTextField(
        value = otp,
        onValueChange = { value ->
            if (value.length <= otpLength) {
                otp = value

                if (value.length == otpLength) {
                    onOtpComplete(value)
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        modifier = Modifier
            .focusRequester(focusRequester)
            .size(1.dp) // better than alpha(0f)
    )

    // 🔥 Visible UI
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                focusRequester.requestFocus()
                keyboardController?.show()
            }
    ) {

        repeat(otpLength) { index ->

            val char = otp.getOrNull(index)?.toString() ?: ""
            val isActive = otp.length == index

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(48.dp)
                    .height(56.dp)
                    .border(
                        width = 2.dp,
                        color = if (isActive) Color.Black else Color.Gray,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    text = char,
                    fontSize = 20.sp
                )
            }
        }
    }

    // 🔥 Auto focus on open
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}

@Composable
fun ResendOtpSection(
    initialTime: Int = 60,
    onResendClick: () -> Unit,
) {
    var timeLeft by remember { mutableIntStateOf(initialTime) }
    var isRunning by remember { mutableStateOf(true) }

    // 🔥 Timer logic
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            isRunning = false
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (timeLeft > 0) {
            Text(
                text = "Resend code in ${timeLeft}s",
                color = Color.Gray
            )
        } else {
            TextButton(onClick = {
                onResendClick()

                // reset timer
                timeLeft = initialTime
                isRunning = true
            }) {
                Text("Resend OTP")
            }
        }
    }
}