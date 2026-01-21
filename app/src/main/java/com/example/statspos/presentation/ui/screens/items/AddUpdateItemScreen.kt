package com.example.statspos.presentation.ui.screens.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.R
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AppText
import com.example.statspos.presentation.ui.components.BarcodeScannerDialog
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.Dropdown
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ExpandableSection
import com.example.statspos.presentation.ui.components.SubDropdown
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.items.AddUpdateItemViewModel
import com.example.statspos.presentation.viewmodels.items.ItemsSharedViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast

@Composable
fun AddUpdateItemScreen(
    sharedViewModel: ItemsSharedViewModel,
    updateId: Long = 0,
    isUpdate: Boolean = false,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    fun goBackWithResult() {
        sharedViewModel.notifyItemChanged()
        onBack()
    }

    val viewModel = hiltViewModel<AddUpdateItemViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
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

    if (showDeleteDialog) {
        ConfirmDialog(
            text = "Are you sure to delete this item",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteItem(updateId) {
                    context.showToast("Item deleted successfully")
                    goBackWithResult()
                }
            }
        )
    }

    if (state.isLoading) {
        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AppCircularProgressIndicator()
        }
    }

    var menuExpanded by remember { mutableStateOf(false) }
    Scaffold(
        snackbarHost = {
            AppSnackbarHost(
                snackbarHostState = snackbarHostState,
            )
        },
        topBar = {
            TopAppBar(
                navigationIcon = Icons.Default.ArrowBack,
                title = if (isUpdate) "Update Item" else "Add Item",
                actions = {
                    Row {
                        if (isUpdate) {
                            IconButton(onClick = {
                                showDeleteDialog = true
                            }) {
                                AppIcon(
                                    icon = Icons.Default.Delete,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                menuExpanded = true
                            }
                        ) {
                            AppIcon(
                                icon = Icons.Default.MoreVert,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier
                                .width(200.dp),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ) {
                            DropdownMenuItem(
                                text = { AppText("Edit") },
                                leadingIcon = {
                                    AppIcon(Icons.Default.Edit)
                                },
                                onClick = {
                                    menuExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = { AppText("Delete") },
                                leadingIcon = {
                                    AppIcon(Icons.Default.Delete)
                                },
                                onClick = {
                                    menuExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = { AppText("Share") },
                                leadingIcon = {
                                    AppIcon(Icons.Default.Share)
                                },
                                onClick = {
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                },
                onNavigationClick = {
                    onBack()
                }
            )
        }
    ) { innerPadding ->
        Body(
            modifier = Modifier
                .padding(innerPadding),
            barcode = state.barcode,
            refCode = state.refCode,
            itemname = state.itemname,
            urduname = state.urduname,
            cost = state.cost,
        )
    }
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    barcode: String,
    refCode: String,
    itemname: String,
    urduname: String,
    cost: Double,
) {
    var value by remember { mutableStateOf("") }
    var showBarcodeScanner by remember { mutableStateOf(false) }
    if (showBarcodeScanner) {
        BarcodeScannerDialog(
            onDismiss = {
                showBarcodeScanner = false
            },
            onScanned = { barcode ->
                value = barcode
                showBarcodeScanner = false
            }
        )
    }

    Column(
        modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // region Basic Details
        ExpandableSection(
            title = "Basic Details",
            initiallyExpanded = true,
        ) {
            // Barcode & Ref. Code
            Row {
                Textbox(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier
                        .weight(1f),
                    trailingIcon = {
                        AppIconButton(
                            icon = R.drawable.ic_barcode,
                            onClick = { showBarcodeScanner = true },
                        )
                    },
                    label = {
                        Text("Barcode")
                    }
                )
                Spacer(Modifier.width(8.dp))
                Textbox(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier
                        .width(150.dp),
                    label = {
                        Text("Ref #")
                    }
                )
            }

            // Itemname
            Textbox(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Itemname")
                }
            )
            Textbox(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Urduname")
                }
            )

            // Cost & Market Price
            Row {
                Textbox(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        AppIconButton(
                            icon = R.drawable.calculate,
                            onClick = {
                                value = HP.evaluateExpression(value)
                            },
                            size = 20.dp,
                        )
                    },
                    label = {
                        Text("Cost")
                    },
                )
                Spacer(Modifier.width(8.dp))
                Textbox(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        AppIconButton(
                            icon = R.drawable.calculate,
                            onClick = {
                                value = HP.evaluateExpression(value)
                            },
                            size = 20.dp,
                        )
                    },
                    label = {
                        Text("Market Price")
                    }
                )
            }

            // Retail & Wholesale
            Row {
                Textbox(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        AppIconButton(
                            icon = R.drawable.calculate,
                            onClick = {
                                value = HP.evaluateExpression(value)
                            },
                            size = 20.dp,
                        )
                    },
                    label = {
                        Text(
                            text = if (HP.settings.fourRateSystem == true) "Rate 1" else "Retail"
                        )
                    }
                )
                Spacer(Modifier.width(8.dp))
                Textbox(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        AppIconButton(
                            icon = R.drawable.calculate,
                            onClick = {
                                value = HP.evaluateExpression(value)
                            },
                            size = 20.dp,
                        )
                    },
                    label = {
                        Text(
                            text = if (HP.settings.fourRateSystem == true) "Rate 2" else "Wholesale"
                        )
                    }
                )
            }

            // Rate3 & Rate4
            if (HP.settings.fourRateSystem == true) {
                Row {
                    Textbox(
                        value = value,
                        onValueChange = { value = it },
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            AppIconButton(
                                icon = R.drawable.calculate,
                                onClick = {
                                    value = HP.evaluateExpression(value)
                                },
                                size = 20.dp,
                            )
                        },
                        label = {
                            Text("Rate 3")
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Textbox(
                        value = value,
                        onValueChange = { value = it },
                        modifier = Modifier.weight(1f),
                        label = {
                            Text("Rate 4")
                        }
                    )
                }
            }

            // Carton Rate & PCS in Carton
            if (HP.settings.saleCartons == true) {
                Row {
                    Textbox(
                        value = value,
                        onValueChange = { value = it },
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            AppIconButton(
                                icon = R.drawable.calculate,
                                onClick = {
                                    value = HP.evaluateExpression(value)
                                },
                                size = 20.dp,
                            )
                        },
                        label = {
                            Text("Crtn Rate")
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Textbox(
                        value = value,
                        onValueChange = { value = it },
                        modifier = Modifier.weight(1f),
                        label = {
                            Text("PCS in Carton")
                        }
                    )
                }
            }
        }
        // endregion

        // region Category & Vendor
        ExpandableSection(
            title = "Category & Vendor"
        ) {
//            Dropdown(
//                value = state.categoryName,
//                onValueChange = viewModel::onCategoryNameChange,
//                items = HP.categories,
//                onItemSelected = { dropdownItem ->
//                    viewModel.onCategoryIdChange(dropdownItem.id)
//                },
//                label = {
//                    Text(text = "Category")
//                }
//            )
//            SubDropdown(
//                value = state.subCategoryName,
//                onValueChange = viewModel::onSubCategoryNameChange,
//                items = HP.subCategories,
//                mainId = state.categoryId,
//                onItemSelected = { dropdownItem ->
//                    viewModel.onSubCategoryIdChange(dropdownItem.id)
//                },
//                label = {
//                    Text(text = "Sub-Category")
//                }
//            )
//            Dropdown(
//                value = state.vendorName,
//                onValueChange = viewModel::onVendorNameChange,
//                items = HP.vendors,
//                onItemSelected = { dropdownItem ->
//                    viewModel.onVendorIdChange(dropdownItem.id)
//                },
//                label = {
//                    Text(text = "Vendor")
//                }
//            )
        }
        // endregion

        ExpandableSection(
            title = "Settings"
        ) {
            Text("Notification: Enabled")
            Text("Theme: Dark")
        }
    }
}
