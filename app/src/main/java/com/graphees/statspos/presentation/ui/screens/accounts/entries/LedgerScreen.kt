package com.graphees.statspos.presentation.ui.screens.accounts.entries

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.graphees.statspos.domain.models.accounts.Accounts
import com.graphees.statspos.domain.models.reports.TotalReport
import com.graphees.statspos.domain.models.reports.accounts.AccountReport
import com.graphees.statspos.presentation.ui.components.AppButton
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.DateTextbox
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ListHeading
import com.graphees.statspos.presentation.ui.components.ListHorizontalDivider
import com.graphees.statspos.presentation.ui.components.ListLabel
import com.graphees.statspos.presentation.ui.components.ListMainHeading
import com.graphees.statspos.presentation.ui.components.ListMainLabel
import com.graphees.statspos.presentation.ui.components.ListWhatsappIcon
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.screens.reports.accounts.ledgerReport
import com.graphees.statspos.presentation.ui.theme.green
import com.graphees.statspos.presentation.ui.theme.red
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings.DEFAULT_RADIUS
import com.graphees.statspos.presentation.ui.utils.openPdf
import com.graphees.statspos.presentation.ui.utils.shareFileToWhatsApp
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.reports.LedgerViewModel
import com.graphees.statspos.utils.EntryType
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import kotlinx.serialization.Serializable
import java.time.LocalDate
import kotlin.math.abs

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data class Entry(val entryType: EntryType) : Routes()
}

@Composable
fun LedgerScreen(
    account: Accounts,
    onBack: () -> Unit,
) {
    val sharedViewModel = hiltViewModel<SharedViewModel>()

    val backStack = rememberNavBackStack(Routes.Home)
    fun navigate(key: NavKey) {
        if (backStack.lastOrNull() != key) {
            backStack.add(key)
        }
    }
    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Routes.Home> {
                Home(
                    sharedViewModel = sharedViewModel,
                    account = account,
                    onEntryClick = { entryType ->
                        navigate(Routes.Entry(entryType))
                    },
                    onBack = onBack,
                )
            }
            entry<Routes.Entry> { key->
                EntryScreen(
                    sharedViewModel = sharedViewModel,
                    account = account,
                    entryType = key.entryType,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}

@Composable
private fun Home(
    sharedViewModel: SharedViewModel,
    account: Accounts,
    onEntryClick: (EntryType) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<LedgerViewModel>()
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
            }
        )
    }

    // Edit data when update
    LaunchedEffect(Unit) {
        if (!state.hasLoadedOnce) {
            viewModel.updateInitialState(
                account = account,
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

    val sharedViewModelState by sharedViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(sharedViewModelState.dataChanged) {
        if (sharedViewModelState.dataChanged) {
            viewModel.loadLedger()
            sharedViewModel.consumeDataChanged()
        }
    }

    fun sendLedger(
        accountReport: List<AccountReport>,
        totalReport: TotalReport
    ) {
        val file = ledgerReport(
            context = context,
            fromDate = HP.getFormatedDate(state.fromDate),
            toDate = HP.getFormatedDate(state.toDate),
            accountReport = accountReport,
            totalReport = totalReport,
        )

        shareFileToWhatsApp(context, file, account.contact!!)
//        openPdf(context, file)
    }

    Scaffold(
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
                title = "Ledger",
            )
        },
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    // region Account
                    Card(
                        modifier = Modifier
                            .padding(16.dp),
                        shape = RoundedCornerShape(DEFAULT_RADIUS),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = state.account.accountName.toString(),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    state.totalReport.newBalance?.let {
                                        Text(
                                            modifier = Modifier,
                                            text = "Rs.${HP.formatDecimal(abs(it), 1)}",
                                            color = if (it >= 0) green else red,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                                state.totalReport.newBalance?.let {
                                    ListLabel(
                                        text = if (it >= 0) "Receivable" else "Payable"
                                    )
                                }
                            }

                            Spacer(Modifier.width(8.dp))
                            ListWhatsappIcon {
                                sendLedger(state.accountReport, state.totalReport)
                            }
                        }
                    }
                    // endregion

                    // region Date
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(ConstantPaddings.BODY_HORIZONTAL)
                            .padding(vertical = 8.dp)
                    ) {
                        DateBox(
                            fromDate = state.fromDate,
                            toDate = state.toDate,
                            onFromDateChange = viewModel::onFromDateChange,
                            onToDateChange = viewModel::onToDateChange,
                        )
                    }
                    // endregion

                    // region In/Out
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(ConstantPaddings.BODY_HORIZONTAL)
                            .padding(vertical = 8.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f),
                        ) {
                            ListMainHeading("Old: ")
                            if (state.totalReport.oldBalance != null) {
                                ListMainLabel(
                                    text = "${HP.formatDecimal(abs(state.totalReport.oldBalance!!))} ",
                                    color = if (state.totalReport.oldBalance!! >= 0) green else red,
                                )
                                ListMainLabel(
                                    text = if (state.totalReport.oldBalance!! >= 0) "R" else "P",
                                    color = if (state.totalReport.oldBalance!! >= 0) green else red,
                                )
                            } else {
                                ListMainLabel(
                                    text = "Rs.0 R",
                                    color = green,
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .weight(1f),
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                ListMainHeading("Out")
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                ListMainHeading("In")
                            }
                        }
                    }
                    // endregion

                    // region Body
                    if (state.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            AppCircularProgressIndicator()
                        }
                    } else {
                        BodyList(
                            modifier = Modifier
                                .weight(1f),
                            items = state.accountReport,
                        )
                    }
                    // endregion
                }

                // region Buttons
                if (HP.userRights.entry == true) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(ConstantPaddings.BODY_HORIZONTAL)
                            .padding(vertical = 8.dp),
                    ) {
                        AppButton(
                            modifier = Modifier
                                .weight(1f),
                            text = "Pay",
                            backgroundColor = red,
                            onClick = {
                                onEntryClick(EntryType.PAYMENT)
                            }
                        )
                        Spacer(Modifier.width(8.dp))
                        AppButton(
                            modifier = Modifier
                                .weight(1f),
                            text = "Receive",
                            backgroundColor = green,
                            onClick = {
                                onEntryClick(EntryType.RECEIPT)
                            }
                        )
                    }
                }
                // endregion
            }
        }
    }
}

@Composable
private fun DateBox(
    fromDate: LocalDate,
    toDate: LocalDate,
    onFromDateChange: (LocalDate) -> Unit,
    onToDateChange: (LocalDate) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        DateTextbox(
            modifier = Modifier
                .weight(1f),
            date = fromDate,
            onDateChange = onFromDateChange,
            label = "From Date"
        )
        Spacer(Modifier.width(8.dp))
        DateTextbox(
            modifier = Modifier
                .weight(1f),
            date = toDate,
            onDateChange = onToDateChange,
            label = "To Date"
        )
    }
}

@Composable
private fun BodyList(
    modifier: Modifier = Modifier,
    items: List<AccountReport>,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        items(items.reversed()) { item ->
            ListCard(item = item)
        }
    }
}

@Composable
private fun ListCard(
    modifier: Modifier = Modifier,
    item: AccountReport,
) {
    val primaryColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
    val secondaryColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.6f)

    Card(
        modifier = modifier,
        shape = RectangleShape,
        onClick = {

        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    )
    {
        Column(
            modifier = Modifier,
        ) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ConstantPaddings.BODY_HORIZONTAL),
//                    .padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    ListHeading(item.naration.toString())
                    Spacer(Modifier.height(8.dp))
                    Row {
                        ListHeading("Date: ", color = primaryColor)
                        ListLabel(item.date.toString(), color = secondaryColor)
                    }
                }

                Row(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ListHeading(
                            text = HP.formatDecimal(item.debit, 1),
                            color = red,
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ListHeading(
                            text = HP.formatDecimal(item.credit, 1),
                            color = green,
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            ListHorizontalDivider()
        }
    }
}