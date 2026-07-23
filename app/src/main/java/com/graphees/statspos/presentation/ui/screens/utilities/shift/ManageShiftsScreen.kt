package com.graphees.statspos.presentation.ui.screens.utilities.shift

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.domain.models.reports.TotalReport
import com.graphees.statspos.domain.models.reports.accounts.ShiftReport
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.CalculatorTB
import com.graphees.statspos.presentation.ui.components.ComboBox
import com.graphees.statspos.presentation.ui.components.Dropdown
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ExpandableSection
import com.graphees.statspos.presentation.ui.components.HeadingMedium
import com.graphees.statspos.presentation.ui.components.LabelMedium
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.ReportButton
import com.graphees.statspos.presentation.ui.components.TextboxOutlined
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.screens.reports.accounts.shiftReport
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.ui.utils.openPdf
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.utilities.shift.ShiftsViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageShiftsScreen(
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel = hiltViewModel<ShiftsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
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

    fun showReport(
        shiftReport: List<ShiftReport>,
        totalReport: TotalReport
    ) {
        val file = shiftReport(
            context = context,
            shiftReport = shiftReport,
            totalReport = totalReport,
        )

        openPdf(context, file)
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
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
                title = "Shift Management",
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .imePadding(),
                ) {
                    UserShift(
                        username = state.username,
                        userShifts = state.userShifts,
                        selectedShift = state.selectedShift,
                        userId = state.userId,
                        status = state.status,
                        cashInHand = state.cashInHand,
                        onUsernameChange = viewModel::onUsernameChange,
                        onUserIdChange = viewModel::onUserIdChange,
                        onSelectedShiftChange = viewModel::onUserShiftChange,
                        onCashInHandChange = viewModel::onCashInHandChange,
                        onShowReportClick = {
                            viewModel.onShowReportClick{ shiftReport, totalReport ->
                                showReport(shiftReport, totalReport)
                            }
                        },
                        onOpenShiftClick = {
                            viewModel.onOpenShiftClick()
                        },
                        onCloseShiftClick = {
                            viewModel.onCloseShiftClick()
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    Entry(
                        status = state.status,
                        receivedAmount = state.receivedAmount,
                        paidAmount = state.paidAmount,
                        onReceivedAmountChange = viewModel::onReceivedAmountChange,
                        onPaidAmountChange = viewModel::onPaidAmountChange,
                        onReceiptEntryClick = {
                            viewModel.onReceiptEntryClick{
                                context.showToast("Entry posted successfully")
                            }
                        },
                        onPaymentEntryClick = {
                            viewModel.onPaymentEntryClick{
                                context.showToast("Entry posted successfully")
                            }
                        }
                    )
                }
            }

            if (state.isLoading) {
                ProgressBarLayout()
            }
        }
    }
}

@Composable
private fun UserShift(
    username: String,
    userShifts: List<DropdownItem>,
    selectedShift: DropdownItem,
    userId: Long,
    status: String,
    cashInHand: String,
    onUsernameChange: (String) -> Unit,
    onUserIdChange: (Long) -> Unit,
    onSelectedShiftChange: (DropdownItem) -> Unit,
    onCashInHandChange: (String) -> Unit,
    onShowReportClick: () -> Unit,
    onOpenShiftClick: () -> Unit,
    onCloseShiftClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ConstantPaddings.BODY_HORIZONTAL)
    ) {
        Dropdown(
            value = username,
            onValueChange = onUsernameChange,
            items = HP.users,
            onItemSelected = { dropdownItem ->
                onUserIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "User")
            },
            changeIdOnEmpty = true,
        )
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = userShifts,
            selectedItem = selectedShift,
            onItemSelected = onSelectedShiftChange,
            label = {
                Text("Shift")
            },
            addNone = true,
        )
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            ReportButton("Show Report") {
                onShowReportClick()
            }
        }
        Spacer(Modifier.height(8.dp))
        if(userId != 0L) {
            Row{
                HeadingMedium("Status: ")
                LabelMedium(status)
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                if(status.equals("opened", ignoreCase = true)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CalculatorTB(
                            value = cashInHand,
                            onValueChange = onCashInHandChange,
                            modifier = Modifier
                                .fillMaxWidth(),
                            label = {
                                Text("Cash in Hand")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                        )
                        ReportButton("Close Shift") {
                            onCloseShiftClick()
                        }
                    }
                }else{
                    ReportButton("Open Shift") {
                        onOpenShiftClick()
                    }
                }
            }
        }
    }
}

@Composable
private fun Entry(
    status: String,
    receivedAmount: String,
    paidAmount: String,
    onReceivedAmountChange: (String) -> Unit,
    onPaidAmountChange: (String) -> Unit,
    onReceiptEntryClick: () -> Unit,
    onPaymentEntryClick: () -> Unit,
) {
    if(status.equals("opened", ignoreCase = true)) {
        ExpandableSection(
            title = "Cash Entry",
            initiallyExpanded = true,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TextboxOutlined(
                        value = receivedAmount,
                        onValueChange = onReceivedAmountChange,
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = {
                            Text("Cash received from user")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                    )
                    ReportButton("Post Entry") {
                        onReceiptEntryClick()
                    }
                }
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TextboxOutlined(
                        value = paidAmount,
                        onValueChange = onPaidAmountChange,
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = {
                            Text("Cash given to user")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                    )
                    ReportButton("Post Entry") {
                        onPaymentEntryClick()
                    }
                }
            }
        }
    }
}
