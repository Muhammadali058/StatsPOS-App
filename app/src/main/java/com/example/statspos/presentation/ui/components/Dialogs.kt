@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.statspos.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.example.statspos.utils.HP
import com.example.statspos.utils.PasswordFor
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
        text = {
            Text(
                text = error ?: ""
            )
        },
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
        text = {
            Text(
                text = message ?: ""
            )
        },
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

@Composable
fun PasswordDialog(
    passwordFor: PasswordFor,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    var value by remember { mutableStateOf("") }
    var isWrongPassword by remember { mutableStateOf(false) }


    AlertDialog(
        title = { Text("Password") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                PasswordTextbox(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = value,
                    onValueChange = { value = it },
                    placeholder = {
                        Text(
                            text = "Enter Password",
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                            )
                        )
                    },
                )

                if (isWrongPassword) {
                    Text(
                        text = "Wrong Password",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.error,
                        )
                    )
                }
            }
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                when (passwordFor) {
                    PasswordFor.DELETE_ITEM -> {
                        if (value == HP.passwords.deleteItem.toString()) {
                            onConfirm()
                        } else {
                            isWrongPassword = true
                        }
                    }

                    PasswordFor.DELETE_ACCOUNT -> {
                        if (value == HP.passwords.deleteAccount.toString()) {
                            onConfirm()
                        } else {
                            isWrongPassword = true
                        }
                    }

                    PasswordFor.EDIT_SALES_BILL -> {
                        if (value == HP.passwords.editSalesBill.toString()) {
                            onConfirm()
                        } else {
                            isWrongPassword = true
                        }
                    }

                    PasswordFor.EDIT_PURCHASE_BILL -> {
                        if (value == HP.passwords.editPurchaseBill.toString()) {
                            onConfirm()
                        } else {
                            isWrongPassword = true
                        }
                    }

                    PasswordFor.DELETE_SALES_BILL -> {
                        if (value == HP.passwords.deleteSalesBill.toString()) {
                            onConfirm()
                        } else {
                            isWrongPassword = true
                        }
                    }

                    PasswordFor.DELETE_PURCHASE_BILL -> {
                        if (value == HP.passwords.deletePurchaseBill.toString()) {
                            onConfirm()
                        } else {
                            isWrongPassword = true
                        }
                    }

                    PasswordFor.DELETE_ENTRY -> {
                        if (value == HP.passwords.deleteEntry.toString()) {
                            onConfirm()
                        } else {
                            isWrongPassword = true
                        }
                    }

                    PasswordFor.PRINT_DUPLICATES -> {
                        if (value == HP.adminPasswords.printDuplicates.toString()) {
                            onConfirm()
                        } else {
                            isWrongPassword = true
                        }
                    }

                    PasswordFor.AUDIT -> {
                        if (value == HP.adminPasswords.audit.toString()) {
                            onConfirm()
                        } else {
                            isWrongPassword = true
                        }
                    }
                }
            }) {
                Text("OK")
            }
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        textContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}
