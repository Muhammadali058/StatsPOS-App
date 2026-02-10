package com.example.statspos.presentation.ui.screens.accounts.suppliers

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
import com.example.statspos.presentation.viewmodels.accounts.suppliers.AddUpdateSupplierViewModel
import com.example.statspos.presentation.viewmodels.accounts.suppliers.SuppliersViewModel
import com.example.statspos.presentation.viewmodels.accounts.vendors.AddUpdateVendorViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.PasswordFor
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast
import okhttp3.MultipartBody

@Composable
fun AddUpdateSupplierScreen(
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

    val viewModel = hiltViewModel<AddUpdateSupplierViewModel>()
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
            text = "Are you sure to delete this supplier",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(updateId) {
                    context.showToast("Supplier deleted successfully")
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
                    context.showToast("Supplier deleted successfully")
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
                title = if (isUpdate) "Update Supplier" else "Add Supplier",
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
                .padding(ConstantPaddings.BODY_HORIZONTAL)
                .padding(vertical = 16.dp)
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
                        email = state.email,
                        address = state.address,

                        onAccountNameChange = viewModel::onAccountNameChange,
                        onContactChange = viewModel::onContactChange,
                        onCityChange = viewModel::onCityChange,
                        onEmailChange = viewModel::onEmailChange,
                        onAddressChange = viewModel::onAddressChange,
                    )

                    ImageExpandable(
                        isUploadingImage = state.isUploadingImage,
                        imageUrl = state.imageUrl,
                        onImageUrlChange = {
                            viewModel.uploadImage(it)
                        }
                    )
                }

                Box{
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
    email: String,
    address: String,

    onAccountNameChange: (String) -> Unit,
    onContactChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
) {
    Textbox(
        value = accountName,
        onValueChange = onAccountNameChange,
        modifier = Modifier
            .fillMaxWidth(),
        label = {
            Text("Supplier Name")
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
        value = email,
        onValueChange = onEmailChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text("Email")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        )
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

@Composable
private fun ImageExpandable(
    isUploadingImage: Boolean,
    imageUrl: String,
    onImageUrlChange: (MultipartBody.Part) -> Unit,
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
                email = "",
                address = "",

                onAccountNameChange = { },
                onContactChange = { },
                onCityChange = { },
                onEmailChange = { },
                onAddressChange = { },
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