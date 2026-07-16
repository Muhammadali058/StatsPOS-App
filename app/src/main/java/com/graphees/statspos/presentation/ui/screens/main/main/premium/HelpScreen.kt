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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.graphees.statspos.presentation.ui.components.AppCard
import com.graphees.statspos.presentation.ui.components.AppOutlinedButton
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.utils.HP

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBack: () -> Unit,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onEmailClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                onNavigationClick = {
                    onBack()
                },
                title = "Help & Support",
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                AppCard{

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                Icons.Default.SupportAgent,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(18.dp)
                                    .size(40.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "We're Here to Help",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Need assistance with your subscription, payments, or the app? Our support team is ready to help you.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                ContactCard(
                    icon = Icons.Default.Phone,
                    title = "Phone",
                    value = HP.graphees.contact!!
                )
            }

            item {
                ContactCard(
                    icon = Icons.Default.Email,
                    title = "Email",
                    value = HP.graphees.email!!
                )
            }

            item {
                Button(
                    onClick = onCallClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.Call, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Call Support")
                }

                Spacer(Modifier.height(12.dp))

                AppOutlinedButton(
                    onClick = onWhatsAppClick,
                ) {
                    Icon(Icons.Default.Chat, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Chat on WhatsApp")
                }

                Spacer(Modifier.height(12.dp))

                AppOutlinedButton(
                    onClick = onEmailClick,
                ) {
                    Icon(Icons.Default.Email, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Send Email")
                }
            }

        }
    }
}

@Composable
private fun ContactCard(
    icon: ImageVector,
    title: String,
    value: String
) {
    AppCard{
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
