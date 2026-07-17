package com.graphees.statspos.presentation.ui.screens.main.main.premium

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.presentation.ui.components.AppCard
import com.graphees.statspos.presentation.ui.components.AppOutlinedButton
import com.graphees.statspos.presentation.ui.components.ConfirmDialog
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.viewmodels.main.PaymentViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast

@Composable
fun SubscriptionsScreen(
    onBack: () -> Unit,
    onHelpClick: () -> Unit,
    onPayNowClick: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<PaymentViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

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

    if (showConfirmDialog) {
        ConfirmDialog(
            text = "Are you sure to cancel subscription",
            onDismiss = {
                showConfirmDialog = false
            },
            onConfirm = {
                showConfirmDialog = false
                viewModel.updateAppSubscription {
                    context.showToast("Subscription cancelled")
                    onBack()
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = "Subscriptions",
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column{
                AppCard{
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                    ) {

                        InfoRow(
                            "Last Payment",
                            HP.getFormatedDate(HP.toLocalDate(HP.appSubscription.paymentDate!!))
                        )

                        Spacer(Modifier.height(12.dp))

                        InfoRow(
                            "Expired On",
                            HP.getFormatedDate(HP.toLocalDate(HP.appSubscription.expiryDate!!))
                        )

                        Spacer(Modifier.height(12.dp))

                        InfoRow(
                            "Days Left",
                            HP.appSubscription.expiryDays.toString()
                        )

                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                AppOutlinedButton (
                    onClick = {
                        showConfirmDialog = true
                    },
                ) {
                    Icon(Icons.Default.Cancel, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel Subscription")
                }
            }

            Column {
                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onPayNowClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.CreditCard, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Pay Now")
                }

                Spacer(Modifier.height(12.dp))

                AppOutlinedButton(
                    onClick = onHelpClick,
                ) {
                    Icon(
                        Icons.Default.SupportAgent,
                        null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Contact Support",
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            title,
            fontWeight = FontWeight.Medium
        )

        Text(
            value,
            fontWeight = FontWeight.Bold
        )
    }
}