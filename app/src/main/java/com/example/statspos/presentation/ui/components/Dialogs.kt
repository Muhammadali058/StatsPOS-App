package com.example.statspos.presentation.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun ErrorDialog(
    title: String = "Error",
    text: String = "",
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {

        },
        title = { Text(title) },
        text = { Text(text) },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        textContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}

@Composable
fun ConfirmDialog(
    title: String = "Confirm",
    text: String = "",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes")
            }
        },
        title = { Text(title) },
        text = { Text(text) },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        textContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}