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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.domain.models.main.Clients
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppSaveButton
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.Textbox2
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.main.login.ClientsViewModel
import com.graphees.statspos.presentation.viewmodels.main.login.AuthViewModel
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent

@Composable
fun RecoverAccountScreen(
    authViewModel: AuthViewModel,
    viewModel: ClientsViewModel,
    onBack: () -> Unit,
    navToOtp: (Clients) -> Unit,
    onOtpVerified: (Clients) -> Unit,
) {
    val context = LocalContext.current
    val activity = context as Activity
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = viewModel::onEvent,
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
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // region Top
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // region Login
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(ConstantPaddings.BODY_HORIZONTAL),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            val shape = RoundedCornerShape(12.dp)
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color.White, shape)
                                    .border(
                                        0.5.dp,
                                        MaterialTheme.colorScheme.onSecondaryContainer,
                                        shape
                                    )
                                    .clickable { onBack() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBackIosNew,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier
                                        .size(22.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Recover Account",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Enter you phone number",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                textAlign = TextAlign.Center,
                            ),
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                    // endregion

                    // region Textboxes
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(ConstantPaddings.BODY_HORIZONTAL),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Title("Contact")
                        Textbox2(
                            value = state.contact,
                            onValueChange = viewModel::onContactChange,
                            placeholder = "03001234567",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                            }
                        )
                        Spacer(Modifier.height(24.dp))
                        if (authState.sendingOtp || state.isLoading) {
                            AppCircularProgressIndicator(
                                modifier = Modifier
                                    .size(32.dp)
                            )
                        } else {
                            AppSaveButton (
                                text = "Send Otp",
                                onClick = {
                                    viewModel.validationToRecoverAccount { client ->
                                        authViewModel.sendOtp(
                                            phone = state.contact,
                                            activity = activity,
                                            onOtpSent = {
                                                navToOtp(client)
                                            },
                                            onAutoVerified = {
                                                onOtpVerified(client)
                                            },
                                            onOtpFailed = { error->
                                                viewModel.showError(error)
                                            },
                                        )
                                    }
                                }
                            )
                        }
                    }
                    // endregion
                }
                // endregion

            }
        }
    }
}

@Composable
private fun Title(
    title: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        )
        Spacer(Modifier.height(8.dp))
    }
}
