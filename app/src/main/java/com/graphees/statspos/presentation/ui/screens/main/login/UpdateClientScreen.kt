package com.graphees.statspos.presentation.ui.screens.main.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.R
import com.graphees.statspos.domain.models.main.Clients
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppSaveButton
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.Textbox2
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.theme.text2Light
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.main.login.ClientsViewModel
import com.graphees.statspos.presentation.viewmodels.main.login.UpdateClientViewModel
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent

@Composable
fun UpdateClientScreen(
    client: Clients,
    onUpdate: (Clients) -> Unit,
) {
    val viewModel = hiltViewModel<UpdateClientViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = viewModel::onEvent,
            onError = {
                showErrorDialog = true
            },
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

    // Edit data when update
    LaunchedEffect(Unit) {
        if (!state.hasLoadedOnce) {
            viewModel.updateInitialState(
                client = client,
            )

            viewModel.setHasLoadedOnce(true)
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
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(ConstantPaddings.BODY_HORIZONTAL)
                .padding(top = 16.dp)
        ) {
            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Body(
                        businessName = state.businessName,
                        username = state.username,
                        password = state.password,
                        confirmPassword = state.confirmPassword,
                        onBusinessNameChange = viewModel::onBusinessNameChange,
                        onUsernameChange = viewModel::onUsernameChange,
                        onPasswordChange = viewModel::onPasswordChange,
                        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                )
                {
                    if (state.isLoading) {
                        AppCircularProgressIndicator()
                    } else {
                        AppSaveButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = "Update"
                        ) {
                            viewModel.updateClient { client ->
                                onUpdate(client)
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    businessName: String,
    username: String,
    password: String,
    confirmPassword: String,
    onBusinessNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
) {
    Column(
        modifier = modifier
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
            // region Signup
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Update Account",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Update account to get started",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center,
                    ),
                )
                Spacer(Modifier.height(12.dp))
            }
            // endregion

            // region Textboxes
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Title("Business Name")
                Textbox2(
                    value = businessName,
                    onValueChange = onBusinessNameChange,
                    placeholder = "Business Name",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = text2Light,
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                )
                Title("Username")
                Textbox2(
                    value = username,
                    onValueChange = onUsernameChange,
                    placeholder = "Username",
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_user),
                            contentDescription = null,
                            tint = text2Light,
                            modifier = Modifier
                                .size(22.dp)
                        )
                    }
                )

                Title("Password")
                Textbox2(
                    value = password,
                    onValueChange = onPasswordChange,
                    placeholder = "Password",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.password),
                            contentDescription = null,
                            tint = text2Light,
                            modifier = Modifier
                                .size(18.dp)
                        )
                    },
                )
                Title("Confirm Password")
                Textbox2(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    placeholder = "Confirm Password",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.password),
                            contentDescription = null,
                            tint = text2Light,
                            modifier = Modifier
                                .size(18.dp)
                        )
                    },
                )
            }
            // endregion
        }
        // endregion

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
