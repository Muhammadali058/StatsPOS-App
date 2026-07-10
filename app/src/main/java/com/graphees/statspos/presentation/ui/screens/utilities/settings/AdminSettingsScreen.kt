package com.graphees.statspos.presentation.ui.screens.utilities.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.AppSwitch
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ExpandableSection
import com.graphees.statspos.presentation.ui.components.PasswordTextbox
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.SaveButton
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.utilities.settings.AdminSettingsViewModel
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent

@Composable
fun AdminSettingsScreen(
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
) {
    fun goBackWithResult() {
//        sharedViewModel.notifyDataChanged()
        onBack()
    }

    val viewModel = hiltViewModel<AdminSettingsViewModel>()
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

    // Edit data when update
    LaunchedEffect(Unit) {
        if (!state.hasLoadedOnce) {
            viewModel.editData()

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


    Scaffold(
//        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                title = "Other Settings",
            )
        },
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(bottom = 8.dp)
        ) {
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
                    Settings(
                        showSuppliersInPurchase = state.showSuppliersInPurchase,
                        onShowSuppliersInPurchaseChange = viewModel::onShowSuppliersInPurchaseChange,
                        searchInSales = state.searchInSales,
                        onSearchInSalesChange = viewModel::onSearchInSalesChange,
                        searchInPurchase = state.searchInPurchase,
                        onSearchInPurchaseChange = viewModel::onSearchInPurchaseChange,
                        estimatedBill = state.estimatedBill,
                        onEstimatedBillChange = viewModel::onEstimatedBillChange,
                        useWeightScale = state.useWeightScale,
                        onUseWeightScaleChange = viewModel::onUseWeightScaleChange,
                        showDashboard = state.showDashboard,
                        onShowDashboardChange = viewModel::onShowDashboardChange,
                        detailedSearchInPOS = state.detailedSearchInPOS,
                        onDetailedSearchInPOSChange = viewModel::onDetailedSearchInPOSChange,
                    )

                    Passwords(
                        printDuplicates = state.printDuplicates,
                        audit = state.audit,
                        onPrintDuplicatesChange = viewModel::onPrintDuplicatesChange,
                        onAuditChange = viewModel::onAuditChange,
                        usePrintDuplicates = state.usePrintDuplicates,
                        useAudit = state.useAudit,
                        onUsePrintDuplicatesChange = viewModel::onUsePrintDuplicatesChange,
                        onUseAuditChange = viewModel::onUseAuditChange,
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(ConstantPaddings.BODY_HORIZONTAL)
                ) {
                    if (state.isSaving) {
                        AppCircularProgressIndicator()
                    } else {
                        SaveButton {
                            viewModel.updateAdminSettings {
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
private fun Settings(
    showSuppliersInPurchase: Boolean,
    onShowSuppliersInPurchaseChange: (Boolean) -> Unit,
    searchInSales: Boolean,
    onSearchInSalesChange: (Boolean) -> Unit,
    searchInPurchase: Boolean,
    onSearchInPurchaseChange: (Boolean) -> Unit,
    estimatedBill: Boolean,
    onEstimatedBillChange: (Boolean) -> Unit,
    useWeightScale: Boolean,
    onUseWeightScaleChange: (Boolean) -> Unit,
    showDashboard: Boolean,
    onShowDashboardChange: (Boolean) -> Unit,
    detailedSearchInPOS: Boolean,
    onDetailedSearchInPOSChange: (Boolean) -> Unit,
) {
    ExpandableSection(
        title = "Settings",
        initiallyExpanded = true,
    ) {
        Spacer(Modifier.height(12.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = showSuppliersInPurchase,
                onCheckedChange = onShowSuppliersInPurchaseChange,
                label = "Supplier in Purchase"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = estimatedBill,
                onCheckedChange = onEstimatedBillChange,
                label = "Estimated Bill"
            )
        }
        Spacer(Modifier.height(24.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = searchInSales,
                onCheckedChange = onSearchInSalesChange,
                label = "Search in Sales"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = searchInPurchase,
                onCheckedChange = onSearchInPurchaseChange,
                label = "Search in Purchase"
            )
        }
        Spacer(Modifier.height(24.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = useWeightScale,
                onCheckedChange = onUseWeightScaleChange,
                label = "Use Weight Scale"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = showDashboard,
                onCheckedChange = onShowDashboardChange,
                label = "Show Dashboard"
            )
        }
        Spacer(Modifier.height(24.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = detailedSearchInPOS,
                onCheckedChange = onDetailedSearchInPOSChange,
                label = "Detailed Search in POS"
            )
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun Passwords(
    printDuplicates: String,
    audit: String,
    onPrintDuplicatesChange: (String) -> Unit,
    onAuditChange: (String) -> Unit,
    usePrintDuplicates: Boolean,
    useAudit: Boolean,
    onUsePrintDuplicatesChange: (Boolean) -> Unit,
    onUseAuditChange: (Boolean) -> Unit,
) {
    ExpandableSection(
        title = "Passwords",
        initiallyExpanded = true,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PasswordTextbox(
                value = printDuplicates,
                onValueChange = onPrintDuplicatesChange,
                modifier = Modifier
                    .weight(1f),
                label = {
                    Text("Print Duplicates")
                }
            )
            Spacer(Modifier.width(8.dp))
            AppSwitch(
                checked = usePrintDuplicates,
                onCheckedChange = onUsePrintDuplicatesChange,
                label = "Use"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PasswordTextbox(
                value = audit,
                onValueChange = onAuditChange,
                modifier = Modifier
                    .weight(1f),
                label = {
                    Text("Audit")
                }
            )
            Spacer(Modifier.width(8.dp))
            AppSwitch(
                checked = useAudit,
                onCheckedChange = onUseAuditChange,
                label = "Use"
            )
        }
    }
}
