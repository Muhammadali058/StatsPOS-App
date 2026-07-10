package com.graphees.statspos.presentation.ui.screens.accounts.entries.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.DateTextbox
import com.graphees.statspos.presentation.ui.components.Dropdown
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.SaveButton
import com.graphees.statspos.presentation.ui.components.Textbox
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.accounts.entries.journal.NewJournalEntryViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
import java.time.LocalDate

@Composable
fun NewJournalEntryBody(
    sharedViewModel: SharedViewModel,
    snackbarHostState: SnackbarHostState
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val viewModel = hiltViewModel<NewJournalEntryViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    var showErrorDialog by remember { mutableStateOf(false) }
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

    if (showErrorDialog) {
        ErrorDialog(
            error = state.error,
            onDismiss = {
                showErrorDialog = false
            },
        )
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Body(
                    debitAccountName = state.debitAccountName,
                    creditAccountName = state.creditAccountName,
                    amount = state.amount,
                    date = state.date,
                    naration = state.naration,
                    onDebitAccountNameChange = viewModel::onDebitAccountNameChange,
                    onCreditAccountNameChange = viewModel::onCreditAccountNameChange,
                    onDebitAccountSelected = { account ->
                        viewModel.onDebitAccountIdChange(account.id)
                    },
                    onCreditAccountSelected = { account ->
                        viewModel.onCreditAccountIdChange(account.id)
                    },
                    onAmountChanged = viewModel::onAmountChange,
                    onDateChanged = viewModel::onDateChange,
                    onNarationChanged = viewModel::onNarationChange,
                )
            }

            // Post Button
            Box(
                modifier = Modifier
                    .padding(ConstantPaddings.BODY_HORIZONTAL)
                    .windowInsetsPadding(
                        WindowInsets.navigationBars
                            .union(WindowInsets.ime)
                    )
            ) {
                if (state.isSaving) {
                    AppCircularProgressIndicator()
                } else {
                    SaveButton(
                        text = "Post"
                    ) {
                        viewModel.passEntry {
                            context.showToast("Enter posted successfully")
                            sharedViewModel.notifyDataChanged()
//                            keyboardController?.hide()
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

@Composable
private fun Body(
    debitAccountName: String,
    creditAccountName: String,
    amount: String,
    date: LocalDate,
    naration: String,
    onDebitAccountNameChange: (String) -> Unit,
    onCreditAccountNameChange: (String) -> Unit,
    onDebitAccountSelected: (DropdownItem) -> Unit,
    onCreditAccountSelected: (DropdownItem) -> Unit,
    onAmountChanged: (String) -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onNarationChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ConstantPaddings.BODY_HORIZONTAL)
    ) {
        Dropdown(
            value = debitAccountName,
            onValueChange = onDebitAccountNameChange,
            items = HP.accounts,
            onItemSelected = onDebitAccountSelected,
            label = {
                Text("Debit Account")
            },
            addType = true,
        )
        Dropdown(
            value = creditAccountName,
            onValueChange = onCreditAccountNameChange,
            items = HP.accounts,
            onItemSelected = onCreditAccountSelected,
            label = {
                Text("Credit Account")
            },
            addType = true,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Textbox(
                modifier = Modifier
                    .weight(1f),
                value = amount,
                onValueChange = onAmountChanged,
                label = {
                    Text("Amount")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
            )
            if (HP.userRights.dateWiseEntry == true) {
                Spacer(Modifier.width(8.dp))
                DateTextbox(
                    modifier = Modifier
                        .weight(1f),
                    date = date,
                    onDateChange = onDateChanged,
                )
            }
        }
        Textbox(
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

@Preview(showBackground = true)
@Composable
private fun Prev() {
    Column(
        Modifier
            .fillMaxSize(),
    ) {
        Body(
            debitAccountName = "",
            creditAccountName = "",
            amount = "",
            date = LocalDate.now(),
            naration = "",
            {},
            {},
            {},
            {},
            {},
            {},
            {},
        )
    }
}