package com.example.statspos.presentation.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.R
import com.example.statspos.presentation.ui.components.InfoDialog
import com.example.statspos.presentation.ui.components.OutlinedTextbox
import com.example.statspos.presentation.ui.components.PasswordOutlinedTextbox
import com.example.statspos.presentation.ui.theme.StatsPOSTheme
import com.example.statspos.presentation.viewmodels.main.ClientsViewModel
import com.example.statspos.presentation.viewmodels.main.UiEvent
import kotlinx.coroutines.launch

@Composable
fun ClientLoginScreen(
    modifier: Modifier = Modifier,
    onLogin: (clientId: Long) -> Unit
) {
    val viewModel = hiltViewModel<ClientsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(UiEvent.Idle)

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(event) {
        when (event) {
            is UiEvent.ShowSnackbar ->{
                snackbarHostState.showSnackbar(
                    message = (event as UiEvent.ShowSnackbar).message,
                    withDismissAction = true,
                )
            }
            else -> {}
        }
    }


    var showInfoDialog by remember { mutableStateOf(false) }
    LaunchedEffect(state.infoMessage, state.error) {
        if (state.infoMessage != null || state.error != null)
            showInfoDialog = true
    }

    if (state.error != null && showInfoDialog) {
        InfoDialog(
            title = "Error",
            text = state.error!!
        ) {
            showInfoDialog = false
        }
    } else {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.TopCenter),

                )
                { data ->
                    Snackbar(
                        snackbarData = data,
                        modifier = Modifier
                            .padding(top = 8.dp, start = 8.dp, end = 8.dp),

                    )
                }
            }
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
                    value = state.username,
                    onValueChange = viewModel::onUsernameChange,
                    labelText = "Username",
                    modifier = Modifier
                        .fillMaxWidth(),
                    leadingIcon = {
                        Icon(painterResource(R.drawable.ic_user), null)
                    }
                )
                PasswordOutlinedTextbox(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    modifier = Modifier
                        .fillMaxWidth(),
                    leadingIcon = {
                        Icon(painterResource(R.drawable.ic_password), null)
                    },
                    onKeyboardActionsDone = {
                        viewModel.clientLogin { clientId ->
                            onLogin(clientId)
                        }
                    }
                )
                Spacer(
                    Modifier.height(16.dp)
                )
                Button(
                    onClick = {
                        viewModel.onEvent(UiEvent.ShowSnackbar("Hello from UI"))
//                        scope.launch {
//                            snackbarHostState.showSnackbar(
//                                message = "Hello from UI",
//                                withDismissAction = true,
//                            )
//                        }
//                    viewModel.clientLogin { clientId ->
//                        onLogin(clientId)
//                    }
                    },
                    modifier = Modifier
                        .width(120.dp)
                ) {
                    Text("Login")
                }
            }
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Prev() {
    StatsPOSTheme() {
        Scaffold(

        ) { innerPadding ->
            InfoMessage(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun InfoMessage(
    text: String = "Username is required",
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = TextStyle(
            fontSize = 14.sp,
        )
    )
}