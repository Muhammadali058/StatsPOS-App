package com.example.statspos.presentation.ui.screens.accounts.entries.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.DateTextbox
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ExpandableSection
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.SubComboBox
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.accounts.entries.expense.NewExpenseEntryViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast
import java.time.LocalDate

@Composable
fun NewExpenseEntryBody(
    sharedViewModel: SharedViewModel,
    snackbarHostState: SnackbarHostState
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val viewModel = hiltViewModel<NewExpenseEntryViewModel>()
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
                    expense = state.expense,
                    subExpense = state.subExpense,
                    amount = state.amount,
                    date = state.date,
                    naration = state.naration,
                    onExpenseSelected = viewModel::onExpenseSelected,
                    onSubExpenseSelected = viewModel::onSubExpenseSelected,
                    onAmountChanged = viewModel::onAmountChange,
                    onDateChanged = viewModel::onDateChange,
                    onNarationChanged = viewModel::onNarationChange,
                )
                MOP(
                    mop = state.mop,
                    bank = state.bank,
                    subBank = state.subBank,
                    onMOPChange = viewModel::onMOPChange,
                    onBankSelected = viewModel::onBankSelected,
                    onSubBankSelected = viewModel::onSubBankSelected,
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
    expense: DropdownItem?,
    subExpense: DropdownItem?,
    amount: String,
    date: LocalDate,
    naration: String,
    onExpenseSelected: (DropdownItem) -> Unit,
    onSubExpenseSelected: (DropdownItem) -> Unit,
    onAmountChanged: (String) -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onNarationChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ConstantPaddings.BODY_HORIZONTAL)
    ) {
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.expenses,
            selectedItem = expense,
            onItemSelected = onExpenseSelected,
            label = {
                Text("Expense")
            },
            addNone = true,
        )
        SubComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.subExpenses,
            selectedItem = subExpense,
            onItemSelected = onSubExpenseSelected,
            label = {
                Text("Sub-Expense")
            },
            addNone = true,
            mainId = expense?.id ?: 0L
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

@Composable
private fun MOP(
    mop: DropdownItem,
    bank: DropdownItem,
    subBank: DropdownItem,
    onMOPChange: (DropdownItem) -> Unit,
    onBankSelected: (DropdownItem) -> Unit,
    onSubBankSelected: (DropdownItem) -> Unit,
) {
    ExpandableSection(
        title = "M.O.P Bank",
        initiallyExpanded = false,
    ) {
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.mop,
            selectedItem = mop,
            onItemSelected = onMOPChange,
            label = {
                Text("M.O.P")
            }
        )
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.banks,
            selectedItem = bank,
            onItemSelected = onBankSelected,
            label = {
                Text("Bank")
            },
            addNone = true,
            enabled = mop.id == 2L,
        )
        SubComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.subBanks,
            selectedItem = subBank,
            onItemSelected = onSubBankSelected,
            label = {
                Text("Bank Account")
            },
            addNone = true,
            enabled = mop.id == 2L,
            mainId = bank.id
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
            null,
            null,
            "",
            LocalDate.now(),
            "",
            { },
            { },
            { },
            { },
            { },
        )
        MOP(
            mop = HP.mop[0],
            bank = HP.getNoneDropdownItem(),
            subBank = HP.getNoneDropdownItem(),
            onMOPChange = {},
            onBankSelected = {},
            onSubBankSelected = {},
        )
        Spacer(Modifier.height(8.dp))
    }
}