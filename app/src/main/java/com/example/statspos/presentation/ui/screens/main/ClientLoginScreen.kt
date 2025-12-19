package com.example.statspos.presentation.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.example.statspos.domain.models.main.LocalBranches
import com.example.statspos.presentation.ui.components.CustomSnackbarHost
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.OutlinedTextbox
import com.example.statspos.presentation.ui.components.PasswordOutlinedTextbox
import com.example.statspos.presentation.ui.theme.StatsPOSTheme
import com.example.statspos.presentation.viewmodels.main.ClientsViewModel
import com.example.statspos.utils.DB
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent

@Composable
fun ClientLoginScreen(
    onClientLogin: (clientId: Long) -> Unit,
    onLocalClientLogin: (localClientId: Int, baseUrl:String) -> Unit,
) {
    val viewModel = hiltViewModel<ClientsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)

    var showErrorDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = viewModel::onEvent
        ){
            showErrorDialog = true
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
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ){
                if(state.localBranches.isEmpty()) {
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
                        }
                    )
                }else{
                    BranchesList(state.localBranches){ localBranch ->
                        onLocalClientLogin(
                            localBranch.localClientId,
                            localBranch.baseUrl
                        )
                    }
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
                if(DB.IS_ONLINE_MODE){
                    onClientLogin()
                }else{
                    onLocalClientLogin()
                }
            }
        )
        Spacer(
            Modifier.height(16.dp)
        )
        Button(
            onClick = {
                if(DB.IS_ONLINE_MODE){
                    onClientLogin()
                }else{
                    onLocalClientLogin()
                }
            },
            modifier = Modifier
                .width(120.dp)
        ) {
            Text("Login")
        }
    }
}


//@Preview(showBackground = true)
@Composable
fun BodyPreview() {
    StatsPOSTheme {
        Body(
            username = "",
            password = "",
            {},
            {},
            {},
            {}
        )
    }
}

@Composable
fun BranchesList(localBranches: List<LocalBranches>, onClick: (LocalBranches) -> Unit) {
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
        items(localBranches){ localBranch ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable{
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
fun BranchesListPreview() {
    StatsPOSTheme() {
        val branches: List<LocalBranches> = List(100){
            LocalBranches(
                it,
                localClientId = it,
                branchName = "Branch $it",
                baseUrl = "",
            )
        }

        BranchesList(
            branches
        ) { }
    }
}