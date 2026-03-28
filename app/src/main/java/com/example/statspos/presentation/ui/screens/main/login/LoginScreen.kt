package com.example.statspos.presentation.ui.screens.main.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.R
import com.example.statspos.presentation.ui.components.AppCheckbox
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.CalculatorTB
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.PasswordTextbox
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.theme.StatsPOSTheme
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.ui.utils.ConstantSize
import com.example.statspos.presentation.viewmodels.main.LoginViewModel
import com.example.statspos.utils.SocketManager
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent

@Composable
fun LoginScreen(
    remember: Boolean,
    username: String?,
    password: String?,
    onLogin: () -> Unit,
    onReset: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<LoginViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)

    LaunchedEffect(Unit) {
        viewModel.onRememberCheckedChange(remember)
        viewModel.onUsernameChange(username ?: "")
        viewModel.onPasswordChange(password ?: "")

        SocketManager.init()
        SocketManager.connect()

        viewModel.login{
            SocketManager.join()
            onLogin()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
//    var currentSnackbarType by remember { mutableStateOf(SnackbarType.INFORMATION) }
    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = viewModel::onEvent,
            onError = {
                showErrorDialog = true
            },
//            changeSnackbarType = {
//                currentSnackbarType = it
//            }
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
//                currentSnackbarType = currentSnackbarType
            )
        }
    ) { innerPadding ->
        Body(
            modifier = Modifier
                .padding(innerPadding),
            username = state.username,
            password = state.password,
            remember = state.remember,
            onUsernameChange = viewModel::onUsernameChange,
            onPasswordChange = viewModel::onPasswordChange,
            onRememberCheckedChange = viewModel::onRememberCheckedChange,
            isLoading = state.isLoading,
            onLogin = {
                keyboardController?.hide()
                viewModel.login {
                    onLogin()
                }
            },
            onResetClick = onReset
        )
    }
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    username: String,
    password: String,
    remember: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberCheckedChange: (Boolean) -> Unit,
    isLoading: Boolean,
    onLogin: () -> Unit,
    onResetClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(32.dp))
            AppIcon(
                icon = R.drawable.statspos,
                modifier = Modifier
                    .size(140.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
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
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 18.sp,
                )
            )
            Spacer(Modifier.height(16.dp))
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
                height = ConstantSize.ORIGINAL_TEXTBOX_HEIGHT,
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
                height = ConstantSize.ORIGINAL_TEXTBOX_HEIGHT,
                contentPadding = ConstantPaddings.DEFAULT_TEXTBOX_INSIDE,
                imeAction = ImeAction.Go,
                keyboardActions = KeyboardActions(
                    onGo = {
                        onLogin()
                    }
                )
            )
            Spacer(Modifier.height(8.dp))
            AppCheckbox(
                modifier = Modifier.fillMaxWidth(),
                label = "Remember",
                checked = remember,
                onCheckedChange = onRememberCheckedChange
            )
            Spacer(Modifier.height(8.dp))
            if (isLoading) {
                AppCircularProgressIndicator()
            } else {
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

        TextButton(
            onClick = {
                onResetClick()
            },
        ) {
            Text(
                text = "Reset",
                modifier = Modifier,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun BodyPreview() {
    StatsPOSTheme {
        Body(
            Modifier,
            username = "",
            password = "",
            remember = true,
            {},
            {},
            {},
            isLoading = false,
            {},
            {}
        )
    }
}
