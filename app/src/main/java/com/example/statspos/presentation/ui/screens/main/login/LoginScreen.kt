package com.example.statspos.presentation.ui.screens.main.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.R
import com.example.statspos.presentation.ui.components.CustomCheckbox
import com.example.statspos.presentation.ui.components.CustomDatePickerDialog
import com.example.statspos.presentation.ui.components.CustomIcon
import com.example.statspos.presentation.ui.components.CustomSnackbarHost
import com.example.statspos.presentation.ui.components.CustomTimePickerDialog
import com.example.statspos.presentation.ui.components.OutlinedTextbox
import com.example.statspos.presentation.ui.components.PasswordOutlinedTextbox
import com.example.statspos.presentation.ui.theme.StatsPOSTheme
import com.example.statspos.presentation.viewmodels.main.LocalDataViewModel
import com.example.statspos.presentation.viewmodels.main.LoginViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.ThemeMode
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent

@Composable
fun LoginScreen(
    remember: Boolean,
    username: String?,
    password: String?,
    onLogin: (remember: Boolean, username: String, password: String) -> Unit
) {
    val viewModel = hiltViewModel<LoginViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)

    LaunchedEffect(Unit) {
        viewModel.onRememberCheckedChange(remember)
        username?.let { viewModel.onUsernameChange(it) }
        password?.let { viewModel.onPasswordChange(it) }

//        viewModel.test()
//        viewModel.login() {
//            onLogin(
//                state.remember,
//                state.username,
//                state.password
//            )
//        }
    }

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
            username = state.username,
            password = state.password,
            remember = state.remember,
            onUsernameChange = viewModel::onUsernameChange,
            onPasswordChange = viewModel::onPasswordChange,
            onRememberCheckedChange = viewModel::onRememberCheckedChange,
            isLoading = state.isLoading,
            onLogin = {
                viewModel.login {
                    onLogin(
                        state.remember,
                        state.username,
                        state.password
                    )
                }
            }
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
    onLogin: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(
            Modifier.height(32.dp)
        )
        CustomIcon(
            icon = R.drawable.statspos,
            modifier = Modifier
                .size(140.dp),
            tint = MaterialTheme.colorScheme.primary
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
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 18.sp,
            )
        )
        Spacer(
            Modifier.height(16.dp)
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
            },
            imeAction = ImeAction.Done
        )
        Spacer(
            Modifier.height(8.dp)
        )
        CustomCheckbox(
            modifier = Modifier.fillMaxWidth(),
            checked = remember,
            onCheckedChange = onRememberCheckedChange
        )
        Spacer(
            Modifier.height(8.dp)
        )
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    onLogin()
                },
                modifier = Modifier
//                .height(45.dp)
//                .fillMaxWidth()
                    .width(120.dp)
            ) {
                Text("Login")
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
            username = "",
            password = "",
            remember = true,
            {},
            {},
            {},
            isLoading = false,
            {}
        )
    }
}
