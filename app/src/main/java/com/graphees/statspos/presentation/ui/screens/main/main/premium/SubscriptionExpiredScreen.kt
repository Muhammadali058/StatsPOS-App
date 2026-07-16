package com.graphees.statspos.presentation.ui.screens.main.main.premium

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.graphees.statspos.presentation.ui.components.AppCard
import com.graphees.statspos.presentation.ui.components.AppOutlinedButton
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.utils.HP

@Composable
fun SubscriptionExpiredScreen(
    onPayNow: () -> Unit,
    onContactSupport: () -> Unit,
    onBack: () -> Unit,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = "Subscription Expired",
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            item {
                Icon(
                    imageVector = Icons.Default.Payments,
                    contentDescription = null,
                    modifier = Modifier.size(90.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    "Subscription Expired",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))

                Text(
                    "Your monthly subscription has expired.\n\nRenew your subscription to continue using all features of the app.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.height(32.dp))

            }


            item {
                AppCard{
                    Column(
                        modifier = Modifier.padding(20.dp)
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
                            "Package",
                            "Starting from Rs. 1,000 / Month"
                        )
                    }
                }
            }

            item {

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onPayNow,
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
                    onClick = onContactSupport,
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