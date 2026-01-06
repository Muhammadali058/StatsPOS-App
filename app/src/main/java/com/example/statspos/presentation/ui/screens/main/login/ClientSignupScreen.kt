package com.example.statspos.presentation.ui.screens.main.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.R
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.PasswordTextbox
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.theme.StatsPOSTheme
import com.example.statspos.presentation.viewmodels.main.ClientsViewModel
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent

@Composable
fun ClientSignupScreen(
    onSignup: (clientId: Int) -> Unit
) {
    val viewModel = hiltViewModel<ClientsViewModel>()
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
            },
        )
    }

    if(showErrorDialog){
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
        }
    ) { innerPadding ->
        Body(
            modifier = Modifier
                .padding(innerPadding),
            businessName = state.businessName,
            contact = state.contact,
            username = state.username,
            password = state.password,
            confirmPassword = state.confirmPassword,
            onBusinessNameChange = viewModel::onBusinessNameChange,
            onContactChange = viewModel::onContactChange,
            onUsernameChange = viewModel::onUsernameChange,
            onPasswordChange = viewModel::onPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            isLoading = state.isLoading,
            onSignup = {
                viewModel.clientSignup { clientId ->
                    onSignup(clientId)
                }
            }
        )
    }
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    businessName: String,
    contact: String,
    username: String,
    password: String,
    confirmPassword: String,
    onBusinessNameChange: (String) -> Unit,
    onContactChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onSignup: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Textbox(
            value = businessName,
            onValueChange = onBusinessNameChange,
            label = {
                Text("Business Name")
            },
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = {
                AppIcon(icon = Icons.Default.Business)
            },
            contentPadding = ConstantPaddings.DEFAULT_TEXTBOX_INSIDE,
        )
        Textbox(
            value = contact,
            onValueChange = onContactChange,
            label = {
                Text("Contact")
            },
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = {
                AppIcon(icon = Icons.Default.Contacts)
            },
            contentPadding = ConstantPaddings.DEFAULT_TEXTBOX_INSIDE,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )
        Textbox(
            value = username,
            onValueChange = onUsernameChange,
            label = {
                Text("Username")
            },
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = {
                AppIcon(icon = R.drawable.ic_user)
            },
            contentPadding = ConstantPaddings.DEFAULT_TEXTBOX_INSIDE,
        )
        PasswordTextbox(
            modifier = Modifier
                .fillMaxWidth(),
            value = password,
            onValueChange = onPasswordChange,
            label = {
                Text("Password")
            },
            leadingIcon = {
                AppIcon(icon = R.drawable.ic_password)
            },
            contentPadding = ConstantPaddings.DEFAULT_TEXTBOX_INSIDE,
        )
        PasswordTextbox(
            modifier = Modifier
                .fillMaxWidth(),
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = {
                Text("Confirm Password")
            },
            leadingIcon = {
                AppIcon(icon = R.drawable.ic_password)
            },
            contentPadding = ConstantPaddings.DEFAULT_TEXTBOX_INSIDE,
            imeAction = ImeAction.Done
        )
        Spacer(
            Modifier.height(16.dp)
        )
        if (isLoading) {
            AppCircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    onSignup()
                },
                modifier = Modifier
                    .width(120.dp)
            ) {
                Text("Signup")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BodyPreview() {
    StatsPOSTheme {
        Body(
            Modifier,
            businessName = "",
            contact = "",
            username = "",
            password = "",
            confirmPassword = "",
            {},
            {},
            {},
            {},
            {},
            false,
            {}
        )
    }
}
