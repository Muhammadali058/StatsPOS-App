package com.example.statspos.presentation.ui.screens.accounts.banks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.PasswordDialog
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.accounts.banks.AddUpdateBankViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.PasswordFor
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast

@Composable
fun AddUpdateBankScreen(
    sharedViewModel: SharedViewModel,
    updateId: Long = 0,
    isUpdate: Boolean = false,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    fun goBackWithResult() {
        sharedViewModel.notifyDataChanged()
        onBack()
    }

    val viewModel = hiltViewModel<AddUpdateBankViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(event) {
        checkEvent(
            event = event,
            snackbarHostState = snackbarHostState,
            viewModelIdleEvent = viewModel::onEvent,
            onError = {
                showErrorDialog = true
            }
        )
    }

    // Edit data when update
    var hasLoadedOnce by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.updateInitialState(
            isUpdate = isUpdate,
            updateId = updateId,
        )

        if (isUpdate && !hasLoadedOnce) {
            hasLoadedOnce = true
            viewModel.editData(updateId)
        }
    }

    if (showErrorDialog) {
        ErrorDialog(
            error = state.error,
            onDismiss = {
                showErrorDialog = false
            },
        )
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            text = "Are you sure to delete this bank",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(updateId) {
                    context.showToast("Bank deleted successfully")
                    goBackWithResult()
                }
            }
        )
    }

    if (showPasswordDialog) {
        PasswordDialog(
            passwordFor = PasswordFor.DELETE_ACCOUNT,
            onDismiss = {
                showPasswordDialog = false
            },
            onConfirm = {
                showPasswordDialog = false
                viewModel.deleteData(updateId) {
                    context.showToast("Bank deleted successfully")
                    goBackWithResult()
                }
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            AppSnackbarHost(
                snackbarHostState = snackbarHostState,
            )
        },
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = if (isUpdate) "Update Bank" else "Add Bank",
                actions = {
                    Row {
                        if (isUpdate && HP.userRights.deleteAnything == true) {
                            IconButton(onClick = {
                                if (HP.passwords.useDeleteAccount == true) {
                                    showPasswordDialog = true
                                } else {
                                    showDeleteDialog = true
                                }
                            }) {
                                AppIcon(
                                    icon = Icons.Default.Delete,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(ConstantPaddings.BODY_HORIZONTAL)
                .padding(vertical = 16.dp)
        ){
            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Body(
                        bankName = state.bankName,
                        remarks = state.remarks,
                        onBankNameChange = viewModel::onBankNameChange,
                        onRemarksChange = viewModel::onRemarksChange,
                    )
                }

                Box(
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.navigationBars
                                .union(WindowInsets.ime)
                        )
                ){
                    if (state.isSaving) {
                        AppCircularProgressIndicator()
                    } else {
                        SaveButton {
                            viewModel.insertOrUpdateData {
                                goBackWithResult()
                            }
                        }
                    }
                }
            }

            if (state.isLoading) {
                ProgressBarLayout()
            }
        }

    }
}

@Composable
private fun Body(
    bankName: String,
    remarks: String,
    onBankNameChange: (String) -> Unit,
    onRemarksChange: (String) -> Unit,
) {
    Textbox(
        value = bankName,
        onValueChange = onBankNameChange,
        modifier = Modifier
            .fillMaxWidth(),
        label = {
            Text("Bank Name")
        }
    )
    Textbox(
        value = remarks,
        onValueChange = onRemarksChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp),
        label = {
            Text("Remarks")
        },
        singleLine = false,
    )
}
