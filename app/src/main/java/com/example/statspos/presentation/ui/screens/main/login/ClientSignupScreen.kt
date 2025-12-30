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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.R
import com.example.statspos.presentation.ui.components.CustomIcon
import com.example.statspos.presentation.ui.components.CustomSnackbarHost
import com.example.statspos.presentation.ui.components.OutlinedTextbox
import com.example.statspos.presentation.ui.components.PasswordOutlinedTextbox
import com.example.statspos.presentation.ui.theme.StatsPOSTheme
import com.example.statspos.presentation.viewmodels.main.ClientsViewModel
import com.example.statspos.utils.SnackbarType
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
    var currentSnackbarType by remember { mutableStateOf(SnackbarType.INFORMATION) }
    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = viewModel::onEvent,
            changeSnackbarType = {
                currentSnackbarType = it
            }
        )
    }

    Scaffold(
        snackbarHost = {
            CustomSnackbarHost(
                snackbarHostState = snackbarHostState,
                currentSnackbarType = currentSnackbarType
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
//        Spacer(
//            Modifier.height(32.dp)
//        )
//        Image(
//            painterResource(R.drawable.statspos),
//            contentDescription = null,
//            modifier = Modifier
//                .size(140.dp)
//        )
//        Spacer(
//            Modifier.height(16.dp)
//        )
        OutlinedTextbox(
            value = businessName,
            onValueChange = onBusinessNameChange,
            label = "Business Name",
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = {
                CustomIcon(icon = Icons.Default.Business)
            }
        )
        OutlinedTextbox(
            value = contact,
            onValueChange = onContactChange,
            label = "Contact",
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = {
                CustomIcon(icon = Icons.Default.Contacts)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )
        OutlinedTextbox(
            value = username,
            onValueChange = onUsernameChange,
            label = "Username",
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = {
                CustomIcon(icon = R.drawable.ic_user)
            }
        )
        PasswordOutlinedTextbox(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = {
                CustomIcon(icon = R.drawable.ic_password)
            }
        )
        PasswordOutlinedTextbox(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            labelText = "Confirm Password",
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = {
                CustomIcon(icon = R.drawable.ic_password)
            },
            imeAction = ImeAction.Done
        )
        Spacer(
            Modifier.height(16.dp)
        )
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
            {}
        )
    }
}
