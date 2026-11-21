package com.graphees.statspos.presentation.ui.screens.accounts.entries

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.domain.models.accounts.Accounts
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppSaveButton
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.DateTextbox
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.MOPSection
import com.graphees.statspos.presentation.ui.components.TextboxOutlined
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.reports.LedgerViewModel
import com.graphees.statspos.utils.EntryType
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
import java.time.LocalDate

@Composable
fun EntryScreen(
    sharedViewModel: SharedViewModel,
    account: Accounts,
    entryType: EntryType,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<LedgerViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    val amountFocusRequester = remember { FocusRequester() }

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
    LaunchedEffect(Unit) {
        if (!state.hasLoadedOnce) {
            viewModel.updateInitialStateForEntry(
                account = account,
                entryType = entryType,
            )

            viewModel.loadLedger()
            viewModel.setHasLoadedOnce(true)
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

    LaunchedEffect(Unit) {
        amountFocusRequester.requestFocus()
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
                title = if(entryType == EntryType.RECEIPT) "Receive" else "Pay",
            )
        },
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    Spacer(Modifier.height(8.dp))
                    Body(
                        amountFocusRequester = amountFocusRequester,
                        amount = state.amount,
                        date = state.date,
                        naration = state.naration,
                        onAmountChanged = viewModel::onAmountChange,
                        onDateChanged = viewModel::onDateChange,
                        onNarationChanged = viewModel::onNarationChange,
                    )
                    MOPSection(
                        mop = state.mop,
                        bank = state.bank,
                        subBank = state.subBank,
                        onMOPChange = viewModel::onMOPChange,
                        onBankSelected = viewModel::onBankSelected,
                        onSubBankSelected = viewModel::onSubBankSelected,
                    )
                }

                // region Post Button
                Box(
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.navigationBars
                                .union(WindowInsets.ime)
                        )
                        .padding(ConstantPaddings.BODY_HORIZONTAL)
                        .padding(bottom = 8.dp)
                ) {
                    if (state.isLoading) {
                        AppCircularProgressIndicator()
                    } else {
                        AppSaveButton(
                            text = "Post"
                        ) {
                            viewModel.passEntry {
                                sharedViewModel.notifyDataChanged()
                                context.showToast("Enter posted successfully")
                                onBack()
                            }
                        }
                    }
                }
                // endregion
            }
        }
    }
}


@Composable
private fun Body(
    amountFocusRequester: FocusRequester?,
    amount: String,
    date: LocalDate,
    naration: String,
    onAmountChanged: (String) -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onNarationChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ConstantPaddings.BODY_HORIZONTAL)
    ) {
        TextboxOutlined(
            modifier = Modifier
                .fillMaxWidth(),
            value = amount,
            onValueChange = onAmountChanged,
            label = {
                Text("Amount")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            focusRequester = amountFocusRequester,
        )
        if (HP.userRights.dateWiseEntry == true) {
            DateTextbox(
                modifier = Modifier
                    .fillMaxWidth(),
                date = date,
                onDateChange = onDateChanged,
            )
        }
        TextboxOutlined(
            modifier = Modifier
                .fillMaxWidth(),
            value = naration,
            onValueChange = onNarationChanged,
            label = {
                Text("Naration")
            },
        )
    }
}
