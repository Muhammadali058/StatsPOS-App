package com.example.statspos.presentation.ui.screens.utilities.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.presentation.ui.components.AppCircularProgressIndicator
import com.example.statspos.presentation.ui.components.AppIcon
import com.example.statspos.presentation.ui.components.AppSnackbarHost
import com.example.statspos.presentation.ui.components.AppSwitch
import com.example.statspos.presentation.ui.components.ComboBox
import com.example.statspos.presentation.ui.components.ConfirmDialog
import com.example.statspos.presentation.ui.components.ErrorDialog
import com.example.statspos.presentation.ui.components.ExpandableSection
import com.example.statspos.presentation.ui.components.PasswordTextbox
import com.example.statspos.presentation.ui.components.ProgressBarLayout
import com.example.statspos.presentation.ui.components.SaveButton
import com.example.statspos.presentation.ui.components.Textbox
import com.example.statspos.presentation.ui.components.TopAppBar
import com.example.statspos.presentation.ui.components.UploadImageView
import com.example.statspos.presentation.ui.utils.ConstantPaddings
import com.example.statspos.presentation.viewmodels.SharedViewModel
import com.example.statspos.presentation.viewmodels.utilities.users.AddUpdateUserViewModel
import com.example.statspos.utils.HP
import com.example.statspos.utils.UiEvent
import com.example.statspos.utils.checkEvent
import com.example.statspos.utils.showToast
import okhttp3.MultipartBody

@Composable
fun AddUpdateUserScreen(
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

    val viewModel = hiltViewModel<AddUpdateUserViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(UiEvent.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
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
            text = "Are you sure to delete this user",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteData(updateId) {
                    context.showToast("User deleted successfully")
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
                title = if (isUpdate) "Update User" else "Add User",
                actions = {
                    Row {
                        if (isUpdate) {
                            if (HP.userRights.deleteAnything == true) {
                                IconButton(onClick = {
                                    showDeleteDialog = true
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
                    Spacer(Modifier.height(8.dp))
                    Basic(
                        username = state.username,
                        password = state.password,
                        confirmPassword = state.confirmPassword,
                        userType = state.userType,
                        shift = state.shift,

                        onUsernameChange = viewModel::onUsernameChange,
                        onPasswordChange = viewModel::onPasswordChange,
                        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                        onUserTypeSelected = viewModel::onUserTypeSelected,
                        onShiftSelected = viewModel::onShiftSelected,
                    )

                    ImageExpandable(
                        isUploadingImage = state.isUploadingImage,
                        imageUrl = state.imageUrl,
                        onImageUrlChange = {
                            viewModel.uploadImage(it)
                        }
                    )

                    UserRights(
                        items = state.items,
                        onItemsChange = viewModel::onItemsChange,
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
    username: String,
    password: String,
    confirmPassword: String,
    userType: DropdownItem,
    shift: DropdownItem,

    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onUserTypeSelected: (DropdownItem) -> Unit,
    onShiftSelected: (DropdownItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ConstantPaddings.BODY_HORIZONTAL),
    ) {
        Textbox(
            value = username,
            onValueChange = onUsernameChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("Username")
            }
        )
        PasswordTextbox(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("Password")
            }
        )
        PasswordTextbox(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text("Confirm Password")
            }
        )
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.userTypes,
            selectedItem = userType,
            onItemSelected = onUserTypeSelected,
            label = {
                Text("User Type")
            },
        )
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.shifts,
            selectedItem = shift,
            onItemSelected = onShiftSelected,
            label = {
                Text("Shift")
            },
        )
    }
}

@Composable
private fun UserRights(
    items: Boolean,
    onItemsChange: (Boolean) -> Unit,
) {
    ExpandableSection(
        title = "User Rights",
        initiallyExpanded = false,
    ) {
        Row {
            AppSwitch(
                modifier = Modifier.weight(1f),
                checked = items,
                onCheckedChange = onItemsChange,
                label = "Items"
            )
        }
        Spacer(Modifier.height(8.dp))
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
                "",
                "",
                "",
                DropdownItem(0L, "None"),
                DropdownItem(0L, "None"),
                {},
                {},
                {},
                {},
                {},
            )

            ImageExpandable(
                isUploadingImage = false,
                imageUrl = "",
                onImageUrlChange = { },
            )

            UserRights(
                false,
                { },
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