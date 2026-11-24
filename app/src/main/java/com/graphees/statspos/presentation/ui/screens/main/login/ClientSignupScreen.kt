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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.R
import com.graphees.statspos.domain.models.main.Clients
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppSaveButton
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.Textbox2
import com.graphees.statspos.presentation.ui.theme.text2Light
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.main.login.ClientsViewModel
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent

@Composable
fun ClientSignupScreen(
    viewModel:ClientsViewModel,
    onSignup: (client: Clients) -> Unit,
    onSignIn: () -> Unit,
) {
//    val viewModel = hiltViewModel<ClientsViewModel>()
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

    Scaffold(
        snackbarHost = {
            AppSnackbarHost(
                snackbarHostState = snackbarHostState,
            )
        }
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
                        contact = state.contact,
                        username = state.username,
                        password = state.password,
                        confirmPassword = state.confirmPassword,
                        onBusinessNameChange = viewModel::onBusinessNameChange,
                        onContactChange = viewModel::onContactChange,
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
                            text = "Sign Up"
                        ) {
                            viewModel.clientSignup { client ->
                                onSignup(client)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Already have an account?",
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                        )
                        TextButton(
                            onClick = {
                                onSignIn()
                            }
                        ) {
                            Text(
                                text = "Sign In",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary,
                                ),
                            )
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
    contact: String,
    username: String,
    password: String,
    confirmPassword: String,
    onBusinessNameChange: (String) -> Unit,
    onContactChange: (String) -> Unit,
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
                    text = "Create Account",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Create a new account to get started",
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
                Title("Contact")
                Textbox2(
                    value = contact,
                    onValueChange = onContactChange,
                    placeholder = "Contact",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Contacts,
                            contentDescription = null,
                            tint = text2Light,
                            modifier = Modifier
                                .size(18.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
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

@Preview(showBackground = true)
@Composable
private fun BodyPreview() {
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
    )
}
