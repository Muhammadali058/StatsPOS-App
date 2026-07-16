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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.R
import com.graphees.statspos.domain.models.main.Clients
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppIcon
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.Textbox2
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.main.ClientsViewModel
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent

@Composable
fun ClientLoginScreen(
    viewModel:ClientsViewModel,
    onClientLogin: (client: Clients) -> Unit,
    onSignup: () -> Unit,
    onHelpClick: () -> Unit,
) {
//    val viewModel = hiltViewModel<ClientsViewModel>()
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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        ) {
            Body(
                username = state.username,
                password = state.password,
                isLoading = state.isLoading,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onClientLogin = {
                    viewModel.clientLogin { client ->
                        onClientLogin(client)
                    }
                },
                onSignup = {
                    onSignup()
                },
                onHelpClick = onHelpClick,
            )
        }
    }
}

@Composable
private fun Body(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onClientLogin: () -> Unit,
    onSignup: () -> Unit,
    onHelpClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            Spacer(
                Modifier.height(32.dp)
            )
            AppIcon(
                icon = R.drawable.statspos,
                modifier = Modifier
                    .size(140.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Title("Username")
            Textbox2(
                value = username,
                onValueChange = onUsernameChange,
                placeholder = "Username",
                leadingIcon = {
                    AppIcon(
                        icon = R.drawable.ic_user,
                        modifier = Modifier
                            .size(20.dp)
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
                    AppIcon(
                        icon = R.drawable.password,
                        modifier = Modifier
                            .size(20.dp)
                    )
                },
            )
            Spacer(
                Modifier.height(16.dp)
            )

            if (isLoading) {
                AppCircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        onClientLogin()
                    },
                    modifier = Modifier
                        .width(120.dp)
                ) {
                    Text("Login")
                }
            }

            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = {
                    onHelpClick()
                },
            ) {
                Text(
                    text = "Need help?",
                    modifier = Modifier,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                    ),
                )
            }
        }

        // region Sign Up Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ConstantPaddings.BODY_HORIZONTAL),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Don't have an account?",
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                )
                TextButton(
                    onClick = {
                        onSignup()
                    }
                ) {
                    Text(
                        text = "Sign Up",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                    )
                }
            }
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

@Preview(showBackground = true)
@Composable
private fun BodyPreview() {
    Body(
        username = "",
        password = "",
        {},
        {},
        false,
        {},
        {},
        {},
    )
}
