package com.graphees.statspos.presentation.ui.screens.main.main.premium

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.graphees.statspos.presentation.ui.components.AppCard
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.utils.HP

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onBack: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onPaymentSent: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = "Complete Your Payment",
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                AppCard{

                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            Icons.Default.Payments,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            "Upgrade to Premium",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Please send your monthly subscription payment to any one of the following accounts.",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item {
                PaymentAccountCard(
                    title = "JazzCash",
                    accountName = "Muhammad Ali",
                    accountNumber = "03030454625",
                    onCopy = {
                        clipboardManager.setText(
                            AnnotatedString("0303-0454625")
                        )
                        Toast.makeText(
                            context,
                            "JazzCash number copied",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }

            item {
                PaymentAccountCard(
                    title = "Meezan Bank",
                    accountName = "Muhammad Ali",
                    accountNumber = "02260109233772",
                    onCopy = {
                        clipboardManager.setText(
                            AnnotatedString("02260109233772")
                        )
                        Toast.makeText(
                            context,
                            "Bank account copied",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }

            item {

                AppCard{

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                "After Payment",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("1. Send payment to any account above.")
                        Text("2. Take a screenshot of the payment receipt.")
                        Text("3. Send the screenshot on our WhatsApp.")
                        Text("4. Tap 'Payment Sent' below.")
                    }
                }
            }

            item {

                if(HP.appSubscription.paymentRequest == true){
                    Text(
                        "Wait until payment processes.",
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }else {
                    Button(
                        onClick = onWhatsAppClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Icon(Icons.Default.Chat, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Screenshot on WhatsApp")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onPaymentSent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Payment Sent")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun PaymentAccountCard(
    title: String,
    accountName: String,
    accountNumber: String,
    onCopy: () -> Unit
) {

    AppCard{

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        "Account Name",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(accountName)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Account Number",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        accountNumber,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                IconButton(onClick = onCopy) {
                    Icon(Icons.Default.ContentCopy, null)
                }
            }
        }
    }
}