package com.example.statspos.presentation.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.R
import com.example.statspos.presentation.ui.components.CustomSnackbarHost
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.OutlinedTextbox
import com.example.statspos.presentation.ui.components.PasswordOutlinedTextbox
import com.example.statspos.presentation.ui.theme.StatsPOSTheme
import com.example.statspos.presentation.viewmodels.main.ClientsViewModel
import com.example.statspos.utils.UiEvent

@Composable
fun ClientLoginScreen(
    onLogin: (clientId: Long) -> Unit
) {
    val viewModel = hiltViewModel<ClientsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)

    var showErrorDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(event) {
        when (event) {
            is UiEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = (event as UiEvent.ShowSnackbar).message,
                    withDismissAction = true,
                )
                viewModel.onEvent(UiEvent.Idle)
            }

            is UiEvent.ShowError -> {
                showErrorDialog = true
                viewModel.onEvent(UiEvent.Idle)
            }

            else -> {}
        }
    }

    if (showErrorDialog) {
        ErrorDialog(
            text = state.error!!
        ) {
            showErrorDialog = false
        }
    } else {
        Scaffold(
            snackbarHost = {
                CustomSnackbarHost(snackbarHostState = snackbarHostState)
            }
        ) { innerPadding ->
            Body(
                modifier = Modifier.padding(innerPadding),
                username = state.username,
                password = state.password,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onLogin = {
                    viewModel.clientLogin { clientId ->
                        onLogin(clientId)
                    }
                }
            )
        }
    }
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(
            Modifier.height(32.dp)
        )
        Image(
            painterResource(R.drawable.statspos),
            contentDescription = null,
            modifier = Modifier
                .size(140.dp)
        )
        Spacer(
            Modifier.height(16.dp)
        )
        Text(
            text = "Welcome Back!",
            style = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        Text(
            text = "Let's Start Today's Business",
            style = TextStyle(
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 18.sp,
            )
        )
        Spacer(
            Modifier.height(16.dp)
        )
        OutlinedTextbox(
            value = username,
            onValueChange = onUsernameChange,
            labelText = "Username",
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = {
                Icon(painterResource(R.drawable.ic_user), null)
            }
        )
        PasswordOutlinedTextbox(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = {
                Icon(painterResource(R.drawable.ic_password), null)
            },
            onKeyboardActionsDone = {
                onLogin()
            }
        )
        Spacer(
            Modifier.height(16.dp)
        )
        Button(
            onClick = {
                onLogin()
            },
            modifier = Modifier
                .width(120.dp)
        ) {
            Text("Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun prev() {
    StatsPOSTheme{
        Body(
            modifier = Modifier,
            username = "",
            password = "",
            {},
            {},
            {}
        )
    }
}