package com.graphees.statspos.presentation.ui.screens.accounts.customers

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphees.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.graphees.statspos.presentation.ui.components.AppSnackbarHost
import com.graphees.statspos.presentation.ui.components.AppSwitch
import com.graphees.statspos.presentation.ui.components.ConfirmDialog
import com.graphees.statspos.presentation.ui.components.DiscountTextbox
import com.graphees.statspos.presentation.ui.components.Dropdown
import com.graphees.statspos.presentation.ui.components.ErrorDialog
import com.graphees.statspos.presentation.ui.components.ExpandableSection
import com.graphees.statspos.presentation.ui.components.PasswordDialog
import com.graphees.statspos.presentation.ui.components.ProgressBarLayout
import com.graphees.statspos.presentation.ui.components.SaveButton
import com.graphees.statspos.presentation.ui.components.TextboxOutlined
import com.graphees.statspos.presentation.ui.components.TopAppBar
import com.graphees.statspos.presentation.ui.components.UpgradeToPremiumBottomSheet
import com.graphees.statspos.presentation.ui.components.UploadImageView
import com.graphees.statspos.presentation.ui.utils.ConstantPaddings
import com.graphees.statspos.presentation.viewmodels.SharedViewModel
import com.graphees.statspos.presentation.viewmodels.accounts.customers.AddUpdateCustomerViewModel
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.PasswordFor
import com.graphees.statspos.utils.UiEvent
import com.graphees.statspos.utils.checkEvent
import com.graphees.statspos.utils.showToast
import okhttp3.MultipartBody

@Composable
fun AddUpdateCustomerScreen(
    sharedViewModel: SharedViewModel,
    updateId: Long = 0,
    isUpdate: Boolean = false,
    onUpgradeClick: () -> Unit,
    onHelpClick: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    fun goBackWithResult() {
        sharedViewModel.notifyDataChanged()
        onBack()
    }

    val viewModel = hiltViewModel<AddUpdateCustomerViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var showUpgradeToPremiumSheet by remember { mutableStateOf(false) }

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
            viewModel.updateInitialState(isUpdate = isUpdate, updateId = updateId)

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

                if(state.error!!.contains("upgrade to premium", ignoreCase = true))
                    showUpgradeToPremiumSheet = true
            },
        )
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            text = "Are you sure to delete this customer",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(updateId) {
                    context.showToast("Customer deleted successfully")
                    goBackWithResult()
                }
            }
        )
    }

    if (showPasswordDialog) {
        PasswordDialog(
            passwordFor = PasswordFor.DELETE_ACCOUNT,
            onDismiss = {
                showPasswordDialog = false
            },
            onConfirm = {
                showPasswordDialog = false
                viewModel.deleteData(updateId) {
                    context.showToast("Customer deleted successfully")
                    goBackWithResult()
                }
            }
        )
    }

    if (showUpgradeToPremiumSheet) {
        UpgradeToPremiumBottomSheet(
            onDismiss = {
                showUpgradeToPremiumSheet = false
            },
            onUpgradeClick = {
                onUpgradeClick()
            },
            onContactClick = {
                onHelpClick()
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                title = if (isUpdate) "Update Customer" else "Add Customer",
//                actions = {
//                    Row {
//                        if (isUpdate) {
//                            if (HP.userRights.deleteAnything == true) {
//                                IconButton(onClick = {
//                                    if (HP.passwords.useDeleteAccount == true) {
//                                        showPasswordDialog = true
//                                    } else {
//                                        showDeleteDialog = true
//                                    }
//                                }) {
//                                    AppIcon(
//                                        icon = Icons.Default.Delete,
//                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .imePadding(),
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
                        accountName = state.accountName,
                        contact = state.contact,
                        city = state.city,
                        address = state.address,

                        onAccountNameChange = viewModel::onAccountNameChange,
                        onContactChange = viewModel::onContactChange,
                        onCityChange = viewModel::onCityChange,
                        onAddressChange = viewModel::onAddressChange,
                    )

                    Tax(
                        ntn = state.ntn,
                        stn = state.stn,
                        cnic = state.cnic,
                        onNtnChange = viewModel::onNtnChange,
                        onStnChange = viewModel::onStnChange,
                        onCnicChange = viewModel::onCnicChange,
                    )

                    CategoryAndDiscount(
                        categoryName = state.categoryName,
                        supplierName = state.supplierName,
                        disc = state.disc,
                        isDiscRsPer = state.isDiscRsPer,
                        isRetail = state.isRetail,
                        isCredit = state.isCredit,
                        onCategoryNameChange = viewModel::onCategoryNameChange,
                        onSupplierNameChange = viewModel::onSupplierNameChange,
                        onCategoryIdChange = viewModel::onCategoryIdChange,
                        onSupplierIdChange = viewModel::onSupplierIdChange,
                        onDiscChange = viewModel::onDiscChange,
                        onIsDiscRsPerChange = viewModel::onIsDiscRsPerChange,
                        onIsRetailChange = viewModel::onIsRetailChange,
                        onIsCreditChange = viewModel::onIsCreditChange,
                    )

                    Others(
                        email = state.email,
                        remarks = state.remarks,
                        dueDays = state.dueDays,
                        openingBalance = state.openingBalance,
                        openingBalanceTBEnabled = state.openingBalanceTBEnabled,
                        onEmailChange = viewModel::onEmailChange,
                        onRemarksChange = viewModel::onRemarksChange,
                        onDueDaysChange = viewModel::onDueDaysChange,
                        onOpeningBalanceChange = viewModel::onOpeningBalanceChange,
                    )

                    ImageExpandable(
                        isUploadingImage = state.isUploadingImage,
                        imageUrl = state.imageUrl,
                        onImageUrlChange = {
                            viewModel.uploadImage(it)
                        },
                        onClear = viewModel::deleteImage,
                    )
                }

                Box(
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.navigationBars
                                .union(WindowInsets.ime)
                        )
                        .padding(ConstantPaddings.BODY_HORIZONTAL)
                        .padding(bottom = 8.dp)
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
    accountName: String,
    contact: String,
    city: String,
    address: String,

    onAccountNameChange: (String) -> Unit,
    onContactChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
) {
    ExpandableSection(
        title = "Basic Details",
        initiallyExpanded = true,
    ) {
        TextboxOutlined(
            value = accountName,
            onValueChange = onAccountNameChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("Customer Name")
            }
        )
        TextboxOutlined(
            value = contact,
            onValueChange = onContactChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("Contact")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
        )
        TextboxOutlined(
            value = city,
            onValueChange = onCityChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("City")
            }
        )
        TextboxOutlined(
            value = address,
            onValueChange = onAddressChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp),
            label = {
                Text("Address")
            },
            singleLine = false,
        )
    }
}

@Composable
private fun Tax(
    ntn: String,
    stn: String,
    cnic: String,

    onNtnChange: (String) -> Unit,
    onStnChange: (String) -> Unit,
    onCnicChange: (String) -> Unit,
) {
    ExpandableSection(
        title = "NTN & STN",
        initiallyExpanded = true,
    ) {
        TextboxOutlined(
            value = ntn,
            onValueChange = onNtnChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("NTN")
            }
        )
        TextboxOutlined(
            value = stn,
            onValueChange = onStnChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("STN")
            }
        )
        TextboxOutlined(
            value = cnic,
            onValueChange = onCnicChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("CNIC")
            }
        )
    }
}

@Composable
private fun CategoryAndDiscount(
    categoryName: String,
    supplierName: String,
    disc: String,
    isDiscRsPer: Boolean,
    isRetail: Boolean,
    isCredit: Boolean,
    onCategoryNameChange: (String) -> Unit,
    onSupplierNameChange: (String) -> Unit,
    onCategoryIdChange: (Long) -> Unit,
    onSupplierIdChange: (Long) -> Unit,
    onDiscChange: (String) -> Unit,
    onIsDiscRsPerChange: (Boolean) -> Unit,
    onIsRetailChange: (Boolean) -> Unit,
    onIsCreditChange: (Boolean) -> Unit,
) {
    ExpandableSection(
        title = "Category & Discount",
        initiallyExpanded = true,
    ) {
        Dropdown(
            value = categoryName,
            onValueChange = onCategoryNameChange,
            items = HP.accountCategories,
            onItemSelected = { dropdownItem ->
                onCategoryIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "Category")
            },
            outlined = true,
        )
        Dropdown(
            value = supplierName,
            onValueChange = onSupplierNameChange,
            items = HP.suppliers,
            onItemSelected = { dropdownItem ->
                onSupplierIdChange(dropdownItem.id)
            },
            label = {
                Text(text = "Supplier")
            },
            outlined = true,
        )
        DiscountTextbox(
            value = disc,
            onValueChange = onDiscChange,
            isDiscRsPer = isDiscRsPer,
            onIsDiscRsPerChange = onIsDiscRsPerChange,
            modifier = Modifier
                .fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = isRetail,
                onCheckedChange = onIsRetailChange,
                label = "Is Retail"
            )
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = isCredit,
                onCheckedChange = onIsCreditChange,
                label = "Is Credit"
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun Others(
    email: String,
    remarks: String,
    dueDays: String,
    openingBalance: String,
    openingBalanceTBEnabled: Boolean,

    onEmailChange: (String) -> Unit,
    onRemarksChange: (String) -> Unit,
    onDueDaysChange: (String) -> Unit,
    onOpeningBalanceChange: (String) -> Unit,
) {
    ExpandableSection(
        title = "Others",
        initiallyExpanded = true,
    ) {
        TextboxOutlined(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("Email")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )
        TextboxOutlined(
            value = remarks,
            onValueChange = onRemarksChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp),
            label = {
                Text("Remarks")
            },
            singleLine = false,
        )
        TextboxOutlined(
            value = dueDays,
            onValueChange = onDueDaysChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Due Days")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )
        TextboxOutlined(
            value = openingBalance,
            onValueChange = onOpeningBalanceChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Opening Balance")
            },
            enabled = openingBalanceTBEnabled,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
    }
}

@Composable
private fun ImageExpandable(
    isUploadingImage: Boolean,
    imageUrl: String,
    onImageUrlChange: (MultipartBody.Part) -> Unit,
    onClear: (String) -> Unit,
) {
    ExpandableSection(
        title = "Image",
        initiallyExpanded = true,
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
                    onImageUrlChange = onImageUrlChange,
                    onClear = onClear,
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
                accountName = "",
                contact = "",
                city = "",
                address = "",

                onAccountNameChange = { },
                onContactChange = { },
                onCityChange = { },
                onAddressChange = { },
            )

            Tax(
                ntn = "",
                stn = "",
                cnic = "",
                onNtnChange = { },
                onStnChange = { },
                onCnicChange = { },
            )

            CategoryAndDiscount(
                categoryName = "",
                supplierName = "",
                disc = "",
                isDiscRsPer = false,
                isRetail = false,
                isCredit = false,
                onCategoryNameChange = { },
                onSupplierNameChange = { },
                onCategoryIdChange = { },
                onSupplierIdChange = { },
                onDiscChange = { },
                onIsDiscRsPerChange = { },
                onIsRetailChange = { },
                onIsCreditChange = { },
            )

            Others(
                email = "",
                remarks = "",
                dueDays = "",
                openingBalance = "",
                openingBalanceTBEnabled = true,
                onEmailChange = { },
                onRemarksChange = { },
                onDueDaysChange = { },
                onOpeningBalanceChange = { },
            )

            ImageExpandable(
                isUploadingImage = false,
                imageUrl = "",
                onImageUrlChange = { },
                onClear = {},
            )
        }

        Box(
            modifier = Modifier
                .padding(16.dp),
        ) {
            SaveButton {}
        }

    }
}