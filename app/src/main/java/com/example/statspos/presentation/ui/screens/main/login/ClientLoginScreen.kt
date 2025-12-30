package com.example.statspos.presentation.ui.screens.main.login

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.R
import com.example.statspos.domain.models.main.LocalBranches
import com.example.statspos.presentation.ui.components.CustomIcon
import com.example.statspos.presentation.ui.components.CustomSnackbarHost
import com.example.statspos.presentation.ui.components.OutlinedTextbox
import com.example.statspos.presentation.ui.components.PasswordOutlinedTextbox
import com.example.statspos.presentation.ui.theme.StatsPOSTheme
import com.example.statspos.presentation.viewmodels.main.ClientsViewModel
import com.example.statspos.utils.DB
import com.example.statspos.utils.SnackbarType
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent

@Composable
fun ClientLoginScreen(
    onClientLogin: (clientId: Int) -> Unit,
    onLocalClientLogin: (localBranch: LocalBranches) -> Unit,
    onSignup: () -> Unit
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        ) {
            if (state.localBranches.isEmpty()) {
                Body(
                    username = state.username,
                    password = state.password,
                    onUsernameChange = viewModel::onUsernameChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onClientLogin = {
                        viewModel.clientLogin { clientId ->
                            onClientLogin(clientId)
                        }
                    },
                    onLocalClientLogin = {
                        viewModel.localClientLogin()
                    },
                    onSignup = {
                        onSignup()
                    }
                )
            } else {
                BranchesList(state.localBranches) { localBranch ->
                    onLocalClientLogin(localBranch)
                }
            }
        }
    }
}

@Composable
private fun Body(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onClientLogin: () -> Unit,
    onLocalClientLogin: () -> Unit,
    onSignup: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
            Modifier.height(16.dp)
        )
        Button(
            onClick = {
                if (DB.IS_ONLINE_MODE) {
                    onClientLogin()
                } else {
                    onLocalClientLogin()
                }
            },
            modifier = Modifier
                .width(120.dp)
        ) {
            Text("Login")
        }
        if (DB.IS_ONLINE_MODE) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have account?",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                TextButton(
                    onClick = {
                        onSignup()
                    }
                ) {
                    Text(
                        text = "Sign up",
                        fontSize = 15.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

        }
    }
}


@Composable
private fun BranchesList(localBranches: List<LocalBranches>, onClick: (LocalBranches) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Select Branch:",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier
                    .padding(16.dp)
            )
        }
        items(localBranches) { localBranch ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        onClick(localBranch)
                    },

                ) {
                Text(
                    text = localBranch.branchName,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    maxLines = 1,
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BodyPreview() {
    StatsPOSTheme {
        Body(
            username = "",
            password = "",
            {},
            {},
            {},
            {},
            {}
        )
    }
}

//@Preview(showBackground = true)
@Composable
private fun BranchesListPreview() {
    StatsPOSTheme() {
        val branches: List<LocalBranches> = List(5) {
            LocalBranches(
                it + 1,
                localClientId = it + 1,
                branchName = "Branch ${it + 1}",
                baseUrl = "",
            )
        }

        BranchesList(
            branches
        ) { }
    }
}