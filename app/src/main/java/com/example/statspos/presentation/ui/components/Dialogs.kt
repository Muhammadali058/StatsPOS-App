@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.statspos.presentation.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@Composable
fun CustomDatePickerDialog(
    modifier: Modifier = Modifier,
    initialValue: LocalDate = LocalDate.now(),
    onDismiss: () -> Unit,
    onSelected: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState(
//        initialSelectedDate = LocalDate.now()
        initialSelectedDate = initialValue
    )

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val localDate =
                    Instant.ofEpochMilli(datePickerState.selectedDateMillis ?: return@TextButton)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                onSelected(localDate)
                onDismiss()
            }) {
                Text("Ok")
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
                headlineContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                dividerColor = MaterialTheme.colorScheme.primary,
                navigationContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                todayDateBorderColor = MaterialTheme.colorScheme.primary,
                yearContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            showModeToggle = false,
        )
    }
}

@Composable
fun CustomTimePickerDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onSelected: (LocalTime) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = LocalTime.now().hour,
        initialMinute = LocalTime.now().minute,
        is24Hour = false
    )

    TimePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val localTime = LocalTime.of(
                    timePickerState.hour,
                    timePickerState.minute
                )

                onSelected(localTime)
                onDismiss()
            }) {
                Text("Ok")
            }
        },
        title = {
//            Text("Select Time")
        },
    ) {
        TimePicker(
            state = timePickerState,
            colors = TimePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                clockDialColor = MaterialTheme.colorScheme.primaryContainer,
                clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                clockDialUnselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectorColor = MaterialTheme.colorScheme.primary,

                periodSelectorBorderColor = MaterialTheme.colorScheme.primary,
                periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,

                timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        )
    }
}

@Composable
fun ErrorDialog(
    title: String = "Error",
    error: String? = null,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {

        },
        title = { Text(title) },
        text = { Text(
            text = error ?: ""
        ) },
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
fun MessageDialog(
    title: String = "Info",
    message: String? = null,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {

        },
        title = { Text(title) },
        text = { Text(
            text = message ?: ""
        ) },
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
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        title = { Text(title) },
        text = { Text(text) },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("NO")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            }) {
                Text("YES")
            }
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        textContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}
