package com.example.statspos.presentation.ui.screens.accounts.vendors

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AppSwitch
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.DiscountTextbox
import com.example.statspos.presentation.ui.components.Dropdown
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ExpandableSection
import com.example.statspos.presentation.ui.components.PasswordDialog
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.components.UploadImageView
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.accounts.vendors.AddUpdateVendorViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.PasswordFor
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast
import okhttp3.MultipartBody

@Composable
fun AddUpdateVendorScreen(
    sharedViewModel: SharedViewModel,
    updateId: Long = 0,
    isUpdate: Boolean = false,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    fun goBackWithResult() {
        sharedViewModel.notifyDataChanged()
        onBack()
    }

    val viewModel = hiltViewModel<AddUpdateVendorViewModel>()
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
    var hasLoadedOnce by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.updateInitialState(isUpdate = isUpdate, updateId = updateId)

        if (isUpdate && !hasLoadedOnce) {
            hasLoadedOnce = true
            viewModel.editData(updateId)
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
            text = "Are you sure to delete this vendor",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(updateId) {
                    context.showToast("Vendor deleted successfully")
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
                    context.showToast("Vendor deleted successfully")
                    goBackWithResult()
                }
            }
        )
    }

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
                title = if (isUpdate) "Update Vendor" else "Add Vendor",
                actions = {
                    Row {
                        if (isUpdate) {
                            if (HP.userRights.deleteAnything == true) {
                                IconButton(onClick = {
                                    if (HP.passwords.useDeleteAccount == true) {
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
                        onNtnChange = viewModel::onNtnChange,
                        onStnChange = viewModel::onStnChange,
                    )

                    CategoryAndDiscount(
                        categoryName = state.categoryName,
                        disc = state.disc,
                        isDiscRsPer = state.isDiscRsPer,
                        isCredit = state.isCredit,
                        onCategoryNameChange = viewModel::onCategoryNameChange,
                        onCategoryIdChange = viewModel::onCategoryIdChange,
                        onDiscChange = viewModel::onDiscChange,
                        onIsDiscRsPerChange = viewModel::onIsDiscRsPerChange,
                        onIsCreditChange = viewModel::onIsCreditChange,
                    )

                    Others(
                        remarks = state.remarks,
                        openingBalance = state.openingBalance,
                        openingBalanceTBEnabled = state.openingBalanceTBEnabled,
                        onRemarksChange = viewModel::onRemarksChange,
                        onOpeningBalanceChange = viewModel::onOpeningBalanceChange,
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
        Textbox(
            value = accountName,
            onValueChange = onAccountNameChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("Vendor Name")
            }
        )
        Textbox(
            value = contact,
            onValueChange = onContactChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("Contact")
            }
        )
        Textbox(
            value = city,
            onValueChange = onCityChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("City")
            }
        )
        Textbox(
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

    onNtnChange: (String) -> Unit,
    onStnChange: (String) -> Unit,
) {
    ExpandableSection(
        title = "NTN & STN",
        initiallyExpanded = false,
    ) {
        Textbox(
            value = ntn,
            onValueChange = onNtnChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("NTN")
            }
        )
        Textbox(
            value = stn,
            onValueChange = onStnChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("STN")
            }
        )
    }
}

@Composable
private fun CategoryAndDiscount(
    categoryName: String,
    disc: String,
    isDiscRsPer: Boolean,
    isCredit: Boolean,
    onCategoryNameChange: (String) -> Unit,
    onCategoryIdChange: (Long) -> Unit,
    onDiscChange: (String) -> Unit,
    onIsDiscRsPerChange: (Boolean) -> Unit,
    onIsCreditChange: (Boolean) -> Unit,
) {
    ExpandableSection(
        title = "Category & Discount",
        initiallyExpanded = false,
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
            }
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
        AppSwitch(
            checked = isCredit,
            onCheckedChange = onIsCreditChange,
            label = "Is Credit"
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun Others(
    remarks: String,
    openingBalance: String,
    openingBalanceTBEnabled: Boolean,

    onRemarksChange: (String) -> Unit,
    onOpeningBalanceChange: (String) -> Unit,
) {
    ExpandableSection(
        title = "Others",
        initiallyExpanded = false,
    ) {
        Textbox(
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
        Textbox(
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
                onNtnChange = { },
                onStnChange = { },
            )

            CategoryAndDiscount(
                categoryName = "",
                disc = "",
                isDiscRsPer = false,
                isCredit = false,
                onCategoryNameChange = { },
                onCategoryIdChange = { },
                onDiscChange = { },
                onIsDiscRsPerChange = { },
                onIsCreditChange = { },
            )

            Others(
                remarks = "",
                openingBalance = "",
                openingBalanceTBEnabled = true,
                onRemarksChange = { },
                onOpeningBalanceChange = { },
            )

            ImageExpandable(
                isUploadingImage = false,
                imageUrl = "",
                onImageUrlChange = { },
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