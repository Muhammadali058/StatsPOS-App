package com.example.statspos.presentation.ui.screens.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.R
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppDropdownMenu
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppIconButton
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AppSwitch
import com.example.statspos.presentation.ui.components.AppText
import com.example.statspos.presentation.ui.components.BarcodeScannerDialog
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.CustomDatePickerDialog
import com.example.statspos.presentation.ui.components.DateTextbox
import com.example.statspos.presentation.ui.components.DiscountTextbox
import com.example.statspos.presentation.ui.components.Dropdown
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ExpandableSection
import com.example.statspos.presentation.ui.components.PasswordDialog
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.SmallButton
import com.example.statspos.presentation.ui.components.SubDropdown
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.components.UploadImageView
import com.example.statspos.presentation.ui.screens.items.linked_items.AddUpdateLinkedItemScreen
import com.example.statspos.presentation.ui.screens.items.linked_items.LinkedItemsScreen
import com.example.statspos.presentation.ui.screens.items.sub_barcodes.AddUpdateSubBarcodeScreen
import com.example.statspos.presentation.ui.screens.items.sub_barcodes.SubBarcodesScreen
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.items.AddUpdateItemViewModel
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.PasswordFor
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import java.time.LocalDate

private sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data class LinkedItems(val itemId: Long, val crtnSize: Int) : Routes()

    @Serializable
    data class AddUpdateLinkedItem(
        val updateId: Long,
        val isUpdate: Boolean,
        val itemId: Long,
        val crtnSize: Int
    ) :
        Routes()

    @Serializable
    data class SubBarcodes(val itemId: Long) : Routes()

    @Serializable
    data class AddUpdateSubBarcode(val updateId: Long, val isUpdate: Boolean, val itemId: Long) :
        Routes()

    @Serializable
    data object SearchItem : Routes()

}

@Composable
fun AddUpdateItemScreen(
    sharedViewModel: SharedViewModel,
    updateId: Long = 0,
    isUpdate: Boolean = false,
    onBack: () -> Unit,
) {
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
                    updateId = updateId,
                    isUpdate = isUpdate,
                    onBack = {
                        onBack()
                    },
                    onLinkedItemsClick = { crtnSize ->
                        navigate(Routes.LinkedItems(itemId = updateId, crtnSize = crtnSize))
                    },
                    onSubBarcodesClick = {
                        navigate(Routes.SubBarcodes(itemId = updateId))
                    }
                )
            }
            entry<Routes.LinkedItems> { key ->
                LinkedItemsScreen(
                    sharedViewModel = sharedViewModel,
                    itemId = key.itemId,
                    crtnSize = key.crtnSize,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onAddButtonClick = { updateId, isUpdate, itemId, crtnSize ->
                        navigate(Routes.AddUpdateLinkedItem(updateId, isUpdate, itemId, crtnSize))
                    }
                )
            }
            entry<Routes.AddUpdateLinkedItem> { key ->
                AddUpdateLinkedItemScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    itemId = key.itemId,
                    crtnSize = key.crtnSize,
                    onSearchItemClick = {
                        navigate(Routes.SearchItem)
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Routes.SubBarcodes> { key ->
                SubBarcodesScreen(
                    sharedViewModel = sharedViewModel,
                    itemId = key.itemId,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onAddButtonClick = { updateId, isUpdate, itemId ->
                        navigate(Routes.AddUpdateSubBarcode(updateId, isUpdate, itemId))
                    }
                )
            }
            entry<Routes.AddUpdateSubBarcode> { key ->
                AddUpdateSubBarcodeScreen(
                    sharedViewModel = sharedViewModel,
                    updateId = key.updateId,
                    isUpdate = key.isUpdate,
                    itemId = key.itemId,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Routes.SearchItem> { key ->
                SearchItemsScreen(
                    sharedViewModel = sharedViewModel,
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
    updateId: Long,
    isUpdate: Boolean,
    onBack: () -> Unit,
    onLinkedItemsClick: (Int) -> Unit,
    onSubBarcodesClick: () -> Unit,
) {
    val context = LocalContext.current

    fun goBackWithResult() {
        sharedViewModel.notifyDataChanged()
        onBack()
    }

    val viewModel = hiltViewModel<AddUpdateItemViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
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
            viewModel.updateInitialState(
                isUpdate = isUpdate,
                updateId = updateId
            )

            if (isUpdate) {
                viewModel.editData(updateId)
            }

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

    if (showDeleteDialog) {
        ConfirmDialog(
            text = "Are you sure to delete this item",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(updateId) {
                    context.showToast("Item deleted successfully")
                    goBackWithResult()
                }
            }
        )
    }

    if (showPasswordDialog) {
        PasswordDialog(
            passwordFor = PasswordFor.DELETE_ITEM,
            onDismiss = {
                showPasswordDialog = false
            },
            onConfirm = {
                showPasswordDialog = false
                viewModel.deleteData(updateId) {
                    context.showToast("Item deleted successfully")
                    goBackWithResult()
                }
            }
        )
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
                onNavigationClick = {
                    onBack()
                },
                title = if (isUpdate) "Update Item" else "Add Item",
                actions = {
                    Row {
                        if (isUpdate) {
                            if (HP.userRights.deleteAnything == true) {
                                IconButton(onClick = {
                                    if (HP.passwords.useDeleteItem == true) {
                                        showPasswordDialog = true
                                    } else {
                                        showDeleteDialog = true
                                    }
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

                            AppDropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                                modifier = Modifier
                                    .width(200.dp),
                            ) {
                                DropdownMenuItem(
                                    text = { AppText("Linked Items") },
                                    leadingIcon = {
                                        AppIcon(R.drawable.linked)
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onLinkedItemsClick(HP.getIntValue(state.crtnSize))
                                    }
                                )

                                DropdownMenuItem(
                                    text = { AppText("Sub Barcodes") },
                                    leadingIcon = {
                                        AppIcon(R.drawable.sub)
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onSubBarcodesClick()
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Basic(
                        barcode = state.barcode,
                        refCode = state.refCode,
                        itemname = state.itemname,
                        urduname = state.urduname,
                        cost = state.cost,
                        retail = state.retail,
                        wholesale = state.wholesale,
                        rate3 = state.rate3,
                        rate4 = state.rate4,
                        crtnRate = state.crtnRate,
                        crtnSize = state.crtnSize,
                        marketPrice = state.marketPrice,

                        onBarcodeChange = viewModel::onBarcodeChange,
                        onRefCodeChange = viewModel::onRefCodeChange,
                        onItemnameChange = viewModel::onItemnameChange,
                        onUrdunameChange = viewModel::onUrdunameChange,
                        onCostChange = viewModel::onCostChange,
                        onRetailChange = viewModel::onRetailChange,
                        onWholesaleChange = viewModel::onWholesaleChange,
                        onRate3Change = viewModel::onRate3Change,
                        onRate4Change = viewModel::onRate4Change,
                        onCrtnRateChange = viewModel::onCrtnRateChange,
                        onCrtnSizeChange = viewModel::onCrtnSizeChange,
                        onMarketPriceChange = viewModel::onMarketPriceChange,
                    )

                    CategoryAndVendor(
                        categoryName = state.categoryName,
                        subCategoryName = state.subCategoryName,
                        vendorName = state.vendorName,
                        categoryId = state.categoryId,
                        onCategoryNameChange = viewModel::onCategoryNameChange,
                        onSubCategoryNameChange = viewModel::onSubCategoryNameChange,
                        onVendorNameChange = viewModel::onVendorNameChange,
                        onCategoryIdChange = viewModel::onCategoryIdChange,
                        onSubCategoryIdChange = viewModel::onSubCategoryIdChange,
                        onVendorIdChange = viewModel::onVendorIdChange,
                    )

                    Stock(
                        stockWarningMin = state.stockWarningMin,
                        stockWarningMax = state.stockWarningMax,
                        maxSalePcs = state.maxSalePcs,
                        maxSaleCrtn = state.maxSaleCrtn,
                        openingStockPcs = state.openingStockPcs,
                        openingStockCrtn = state.openingStockCrtn,
                        currentStockPcs = state.currentStockPcs,
                        currentStockCrtn = state.currentStockCrtn,

                        onStockWarningMinChange = viewModel::onStockWarningMinChange,
                        onStockWarningMaxChange = viewModel::onStockWarningMaxChange,
                        onMaxSalePcsChange = viewModel::onMaxSalePcsChange,
                        onMaxSaleCrtnChange = viewModel::onMaxSaleCrtnChange,
                        onOpeningStockPcsChange = viewModel::onOpeningStockPcsChange,
                        onOpeningStockCrtnChange = viewModel::onOpeningStockCrtnChange,
                        onCurrentStockPcsChange = viewModel::onCurrentStockPcsChange,
                        onCurrentStockCrtnChange = viewModel::onCurrentStockCrtnChange,

                        openingStockPcsTBEnabled = state.openingStockPcsTBEnabled,
                        openingStockCrtnTBEnabled = state.openingStockCrtnTBEnabled,
                    )

                    DiscountExpiry(
                        expirable = state.expirable,
                        expiry = state.expiry,
                        disc = state.disc,
                        isDiscRsPer = state.isDiscRsPer,
                        packing = state.packing,
                        location = state.location,

                        onExpirableChange = viewModel::onExpirableChange,
                        onExpiryChange = viewModel::onExpiryChange,
                        onDiscChange = viewModel::onDiscChange,
                        onIsDiscRsPerChange = viewModel::onIsDiscRsPerChange,
                        onPackingChange = viewModel::onPackingChange,
                        onLocationChange = viewModel::onLocationChange,
                    )

                    Switches(
                        changeable = state.changeable,
                        repeatable = state.repeatable,
                        lockPcs = state.lockPcs,
                        lockCrtn = state.lockCrtn,
                        button = state.button,
                        searchable = state.searchable,
                        saleUnderStock = state.saleUnderStock,

                        onChangeableChange = viewModel::onChangeableChange,
                        onRepeatableChange = viewModel::onRepeatableChange,
                        onLockPcsChange = viewModel::onLockPcsChange,
                        onLockCrtnChange = viewModel::onLockCrtnChange,
                        onButtonChange = viewModel::onButtonChange,
                        onSearchAbleChange = viewModel::onSearchableChange,
                        onSaleUnderStockChange = viewModel::onSaleUnderStockChange,
                    )

                    ImageExpandable(
                        isUploadingImage = state.isUploadingImage,
                        imageUrl = state.imageUrl,
                        onImageUrlChange = {
                            viewModel.uploadImage(it)
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(ConstantPaddings.BODY_HORIZONTAL)
                        .padding(vertical = 16.dp)
                ) {
                    if (state.isSaving) {
                        AppCircularProgressIndicator()
                    } else {
                        SaveButton {
                            viewModel.insertOrUpdateData {
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
private fun Basic(
    barcode: String,
    refCode: String,
    itemname: String,
    urduname: String,
    cost: String,
    retail: String,
    wholesale: String,
    rate3: String,
    rate4: String,
    crtnRate: String,
    crtnSize: String,
    marketPrice: String,

    onBarcodeChange: (String) -> Unit,
    onRefCodeChange: (String) -> Unit,
    onItemnameChange: (String) -> Unit,
    onUrdunameChange: (String) -> Unit,
    onCostChange: (String) -> Unit,
    onRetailChange: (String) -> Unit,
    onWholesaleChange: (String) -> Unit,
    onRate3Change: (String) -> Unit,
    onRate4Change: (String) -> Unit,
    onCrtnRateChange: (String) -> Unit,
    onCrtnSizeChange: (String) -> Unit,
    onMarketPriceChange: (String) -> Unit,
) {
    var showBarcodeScanner by remember { mutableStateOf(false) }
    if (showBarcodeScanner) {
        BarcodeScannerDialog(
            onDismiss = {
                showBarcodeScanner = false
            },
            onScanned = { barcode ->
                onBarcodeChange(barcode)
                showBarcodeScanner = false
            }
        )
    }

    ExpandableSection(
        title = "Basic Details",
        initiallyExpanded = true,
    ) {
        // Barcode & Ref. Code
        Row {
            Textbox(
                value = barcode,
                onValueChange = onBarcodeChange,
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
                value = refCode,
                onValueChange = onRefCodeChange,
                modifier = Modifier
                    .width(150.dp),
                label = {
                    Text("Ref #")
                }
            )
        }

        // Itemname & Urduname
        Textbox(
            value = itemname,
            onValueChange = onItemnameChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Itemname")
            }
        )
        Textbox(
            value = urduname,
            onValueChange = onUrdunameChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Urduname")
            },
            textStyle = TextStyle(
                textDirection = TextDirection.Rtl
            )
        )

        // Cost & Market Price
        Row {
            Textbox(
                value = cost,
                onValueChange = onCostChange,
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    AppIconButton(
                        icon = R.drawable.calculate,
                        onClick = {
                            onCostChange(HP.evaluateExpression(cost))
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
                value = marketPrice,
                onValueChange = onMarketPriceChange,
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    AppIconButton(
                        icon = R.drawable.calculate,
                        onClick = {
                            onMarketPriceChange(HP.evaluateExpression(marketPrice))
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
                value = retail,
                onValueChange = onRetailChange,
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    AppIconButton(
                        icon = R.drawable.calculate,
                        onClick = {
                            onRetailChange(HP.evaluateExpression(retail))
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
                value = wholesale,
                onValueChange = onWholesaleChange,
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    AppIconButton(
                        icon = R.drawable.calculate,
                        onClick = {
                            onWholesaleChange(HP.evaluateExpression(wholesale))
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
                    value = rate3,
                    onValueChange = onRate3Change,
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        AppIconButton(
                            icon = R.drawable.calculate,
                            onClick = {
                                onRate3Change(HP.evaluateExpression(rate3))
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
                    value = rate4,
                    onValueChange = onRate4Change,
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        AppIconButton(
                            icon = R.drawable.calculate,
                            onClick = {
                                onRate4Change(HP.evaluateExpression(rate4))
                            },
                            size = 20.dp,
                        )
                    },
                    label = {
                        Text("Rate 3")
                    }
                )
            }
        }

        // Carton Rate & PCS in Carton
        if (HP.settings.saleCartons == true) {
            Row {
                Textbox(
                    value = crtnRate,
                    onValueChange = onCrtnRateChange,
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        AppIconButton(
                            icon = R.drawable.calculate,
                            onClick = {
                                onCrtnRateChange(HP.evaluateExpression(crtnRate))
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
                    value = crtnSize,
                    onValueChange = onCrtnSizeChange,
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("PCS in Carton")
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryAndVendor(
    categoryName: String,
    subCategoryName: String,
    vendorName: String,
    categoryId: Long,
    onCategoryNameChange: (String) -> Unit,
    onSubCategoryNameChange: (String) -> Unit,
    onVendorNameChange: (String) -> Unit,
    onCategoryIdChange: (Long) -> Unit,
    onSubCategoryIdChange: (Long) -> Unit,
    onVendorIdChange: (Long) -> Unit,
) {
    ExpandableSection(
        title = "Category & Vendor",
        initiallyExpanded = false,
    ) {
        Dropdown(
            value = categoryName,
            onValueChange = onCategoryNameChange,
            items = HP.categories,
            onItemSelected = { dropdownItem ->
                onCategoryIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "Category")
            }
        )
        SubDropdown(
            value = subCategoryName,
            onValueChange = onSubCategoryNameChange,
            items = HP.subCategories,
            mainId = categoryId,
            onItemSelected = { dropdownItem ->
                onSubCategoryIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "Sub-Category")
            }
        )
        Dropdown(
            value = vendorName,
            onValueChange = onVendorNameChange,
            items = HP.vendors,
            onItemSelected = { dropdownItem ->
                onVendorIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "Vendor")
            }
        )
    }
}

@Composable
private fun Stock(
    stockWarningMin: String,
    stockWarningMax: String,
    maxSalePcs: String,
    maxSaleCrtn: String,
    openingStockPcs: String,
    openingStockCrtn: String,
    currentStockPcs: String,
    currentStockCrtn: String,

    onStockWarningMinChange: (String) -> Unit,
    onStockWarningMaxChange: (String) -> Unit,
    onMaxSalePcsChange: (String) -> Unit,
    onMaxSaleCrtnChange: (String) -> Unit,
    onOpeningStockPcsChange: (String) -> Unit,
    onOpeningStockCrtnChange: (String) -> Unit,
    onCurrentStockPcsChange: (String) -> Unit,
    onCurrentStockCrtnChange: (String) -> Unit,

    openingStockPcsTBEnabled: Boolean,
    openingStockCrtnTBEnabled: Boolean,
) {
    ExpandableSection(
        title = "Stock Warning, Opening Stock",
        initiallyExpanded = false,
    ) {
        // Stock Warning Min & Max
        AppText(
            text = "Stock Warning",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Row {
            Textbox(
                value = stockWarningMin,
                onValueChange = onStockWarningMinChange,
                modifier = Modifier.weight(1f),
                label = {
                    Text("Min")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )
            Spacer(Modifier.width(8.dp))
            Textbox(
                value = stockWarningMax,
                onValueChange = onStockWarningMaxChange,
                modifier = Modifier.weight(1f),
                label = {
                    Text("Max")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )
        }
        // Max Sale Stock PCS & Crtn
        AppText(
            text = "Max Sale Stock",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Row {
            Textbox(
                value = maxSalePcs,
                onValueChange = onMaxSalePcsChange,
                modifier = Modifier.weight(1f),
                label = {
                    Text("PCS")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )
            if (HP.settings.saleCartons == true) {
                Spacer(Modifier.width(8.dp))
                Textbox(
                    value = maxSaleCrtn,
                    onValueChange = onMaxSaleCrtnChange,
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("Crtn")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
            }
        }
        // Opening Stock
        AppText(
            text = "Opening Stock",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Row {
            Textbox(
                value = openingStockPcs,
                onValueChange = onOpeningStockPcsChange,
                modifier = Modifier.weight(1f),
                label = {
                    Text("PCS")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                enabled = openingStockPcsTBEnabled,
            )
            if (HP.settings.saleCartons == true) {
                Spacer(Modifier.width(8.dp))
                Textbox(
                    value = openingStockCrtn,
                    onValueChange = onOpeningStockCrtnChange,
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("Crtn")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    enabled = openingStockCrtnTBEnabled,
                )
            }
        }
        // Current Stock
        AppText(
            text = "Current Stock",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Row {
            Textbox(
                value = currentStockPcs,
                onValueChange = onCurrentStockPcsChange,
                modifier = Modifier.weight(1f),
                label = {
                    Text("PCS")
                },
                readOnly = true,
            )
            if (HP.settings.saleCartons == true) {
                Spacer(Modifier.width(8.dp))
                Textbox(
                    value = currentStockCrtn,
                    onValueChange = onCurrentStockCrtnChange,
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("Crtn")
                    },
                    readOnly = true,
                )
            }
        }
    }
}

@Composable
private fun DiscountExpiry(
    expirable: Boolean,
    expiry: LocalDate,
    disc: String,
    isDiscRsPer: Boolean,
    packing: String,
    location: String,

    onExpirableChange: (Boolean) -> Unit,
    onExpiryChange: (LocalDate) -> Unit,
    onDiscChange: (String) -> Unit,
    onIsDiscRsPerChange: (Boolean) -> Unit,
    onPackingChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
) {
    ExpandableSection(
        title = "Discount, Expiry, Location",
        initiallyExpanded = false,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppSwitch(
                    modifier = Modifier,
                    checked = expirable,
                    onCheckedChange = onExpirableChange,
                    label = "Expirable"
                )
                Row {
                    SmallButton(
                        "1"
                    ) {
                        val date = LocalDate.now().plusMonths(1)
                        onExpiryChange(date)
                    }
                    Spacer(Modifier.width(8.dp))
                    SmallButton(
                        "3"
                    ) {
                        val date = LocalDate.now().plusMonths(3)
                        onExpiryChange(date)
                    }
                    Spacer(Modifier.width(8.dp))
                    SmallButton(
                        "6"
                    ) {
                        val date = LocalDate.now().plusMonths(6)
                        onExpiryChange(date)
                    }
                    Spacer(Modifier.width(8.dp))
                    SmallButton(
                        "12"
                    ) {
                        val date = LocalDate.now().plusMonths(12)
                        onExpiryChange(date)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            DateTextbox(
                modifier = Modifier
                    .fillMaxWidth(),
                date = expiry,
                onDateChange = onExpiryChange,
                label = "Expiry"
            )
            DiscountTextbox(
                value = disc,
                onValueChange = onDiscChange,
                isDiscRsPer = isDiscRsPer,
                onIsDiscRsPerChange = onIsDiscRsPerChange,
                modifier = Modifier
                    .fillMaxWidth(),
            )
            Textbox(
                value = packing,
                onValueChange = onPackingChange,
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text("Packing")
                },
            )
            Textbox(
                value = location,
                onValueChange = onLocationChange,
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text("Location")
                },
            )
        }
    }
}

@Composable
private fun Switches(
    changeable: Boolean,
    repeatable: Boolean,
    lockPcs: Boolean,
    lockCrtn: Boolean,
    button: Boolean,
    searchable: Boolean,
    saleUnderStock: Boolean,

    onChangeableChange: (Boolean) -> Unit,
    onRepeatableChange: (Boolean) -> Unit,
    onLockPcsChange: (Boolean) -> Unit,
    onLockCrtnChange: (Boolean) -> Unit,
    onButtonChange: (Boolean) -> Unit,
    onSearchAbleChange: (Boolean) -> Unit,
    onSaleUnderStockChange: (Boolean) -> Unit,
) {
    ExpandableSection(
        title = "Lock & Others",
        initiallyExpanded = false,
    ) {
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = changeable,
                onCheckedChange = onChangeableChange,
                label = "Changeable"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = repeatable,
                onCheckedChange = onRepeatableChange,
                label = "Repeatable"
            )
        }
        Spacer(Modifier.height(16.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = lockPcs,
                onCheckedChange = onLockPcsChange,
                label = "Lock PCS"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = lockCrtn,
                onCheckedChange = onLockCrtnChange,
                label = "Lock CRTN"
            )
        }
        Spacer(Modifier.height(16.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = button,
                onCheckedChange = onButtonChange,
                label = "Button"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = searchable,
                onCheckedChange = onSearchAbleChange,
                label = "Searchable"
            )
        }
        Spacer(Modifier.height(16.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = saleUnderStock,
                onCheckedChange = onSaleUnderStockChange,
                label = "Sale Under Stock"
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ImageExpandable(
    isUploadingImage: Boolean,
    imageUrl: String,
    onImageUrlChange: (MultipartBody.Part) -> Unit,
) {
    ExpandableSection(
        title = "Image",
        initiallyExpanded = false,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isUploadingImage) {
                AppCircularProgressIndicator()
            } else {
                UploadImageView(
                    imageUrl = imageUrl,
                    onImageUrlChange = onImageUrlChange
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun BodyPrev() {
    val scrollState = rememberScrollState()

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
            Basic(
                barcode = "",
                refCode = "",
                itemname = "",
                urduname = "",
                cost = "",
                retail = "",
                wholesale = "",
                rate3 = "",
                rate4 = "",
                crtnRate = "",
                crtnSize = "",
                marketPrice = "",

                onBarcodeChange = { },
                onRefCodeChange = { },
                onItemnameChange = { },
                onUrdunameChange = { },
                onCostChange = { },
                onRetailChange = { },
                onWholesaleChange = { },
                onRate3Change = { },
                onRate4Change = { },
                onCrtnRateChange = { },
                onCrtnSizeChange = { },
                onMarketPriceChange = { },
            )

            CategoryAndVendor(
                categoryName = "",
                subCategoryName = "",
                vendorName = "",
                categoryId = 1,
                onCategoryNameChange = { },
                onSubCategoryNameChange = { },
                onVendorNameChange = { },
                onCategoryIdChange = { },
                onSubCategoryIdChange = { },
                onVendorIdChange = { },
            )

            Stock(
                stockWarningMin = "",
                stockWarningMax = "",
                maxSalePcs = "",
                maxSaleCrtn = "",
                openingStockPcs = "",
                openingStockCrtn = "",
                currentStockPcs = "",
                currentStockCrtn = "",

                onStockWarningMinChange = { },
                onStockWarningMaxChange = { },
                onMaxSalePcsChange = { },
                onMaxSaleCrtnChange = { },
                onOpeningStockPcsChange = { },
                onOpeningStockCrtnChange = { },
                onCurrentStockPcsChange = { },
                onCurrentStockCrtnChange = { },

                openingStockPcsTBEnabled = false,
                openingStockCrtnTBEnabled = false,
            )

            DiscountExpiry(
                expirable = false,
                expiry = LocalDate.now(),
                disc = "",
                isDiscRsPer = true,
                packing = "",
                location = "",

                onExpirableChange = { },
                onExpiryChange = { },
                onDiscChange = { },
                onIsDiscRsPerChange = { },
                onPackingChange = { },
                onLocationChange = { },
            )

            Switches(
                changeable = false,
                repeatable = false,
                lockPcs = false,
                lockCrtn = false,
                button = false,
                searchable = false,
                saleUnderStock = false,

                onChangeableChange = {},
                onRepeatableChange = {},
                onLockPcsChange = {},
                onLockCrtnChange = {},
                onButtonChange = {},
                onSearchAbleChange = {},
                onSaleUnderStockChange = {},
            )

            ImageExpandable(
                isUploadingImage = false,
                imageUrl = "",
                onImageUrlChange = { },
            )
        }

        Box(
            modifier = Modifier
//                .background(Color.Red)
                .padding(16.dp),
        ) {
            SaveButton {}
        }

    }
}