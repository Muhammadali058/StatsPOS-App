package com.example.statspos.presentation.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.statspos.R
import com.example.statspos.utils.HP
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.time.LocalDate

@Composable
fun ImageView(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    showIfNull: Boolean = false,
    error: Painter? = painterResource(R.drawable.item),
    contentScale: ContentScale = ContentScale.Fit,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    if (showIfNull) {
        Image(
            painter = rememberAsyncImagePainter(
                model = HP.getImageUrl(imageUrl ?: ""),
                error = error,
            ),
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale,
        )
        Column(content = content)
    } else {
        imageUrl?.let {
            if (it.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = HP.getImageUrl(imageUrl),
                    ),
                    contentDescription = null,
                    modifier = modifier,
                    contentScale = contentScale,
                )
                Column(content = content)
            }
        }
    }
}

@Composable
fun UploadImageView1(
    imageUrl: String,
    onImageUrlChange: (MultipartBody.Part) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    shape: Shape = RectangleShape,
) {
    var selectedImageUri by rememberSaveable { mutableStateOf<Any?>(HP.getImageUrl(imageUrl)) }
    LaunchedEffect(imageUrl) {
        selectedImageUri = HP.getImageUrl(imageUrl) + "?${System.currentTimeMillis()}"
    }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.run {
            selectedImageUri = uri
            val multipart = uriToMultipart(context, uri)
            onImageUrlChange(multipart)
        }
    }

    AsyncImage(
        model = selectedImageUri,
        error = painterResource(R.drawable.select_image),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .clip(shape)
            .clickable {
                launcher.launch("image/*")
            },
        contentScale = ContentScale.Crop
    )
}


@Composable
fun ListImageView(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    showIfNull: Boolean = true,
    error: Painter? = painterResource(R.drawable.select_image),
    contentScale: ContentScale = ContentScale.Fit,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    if (showIfNull) {
        Image(
            painter = rememberAsyncImagePainter(
                model = HP.getImageUrl(imageUrl ?: ""),
                error = error,
            ),
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale,
        )
        Column(content = content)
    } else {
        imageUrl?.let {
            if (it.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = HP.getImageUrl(imageUrl),
                    ),
                    contentDescription = null,
                    modifier = modifier,
                    contentScale = contentScale,
                )
                Column(content = content)
            }
        }
    }
}

fun uriToMultipart(
    context: Context,
    uri: Uri,
    partName: String = "image"
): MultipartBody.Part {

    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(uri) ?: "image/*"

    val inputStream = contentResolver.openInputStream(uri)!!
    val bytes = inputStream.readBytes()

    val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(
        name = partName,
        filename = "upload.${mimeType.substringAfter("/")}",
        body = requestBody
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun UploadImageView(
    imageUrl: String,
    onImageUrlChange: (MultipartBody.Part) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    shape: Shape = RectangleShape,
) {
    var selectedImageUri by rememberSaveable { mutableStateOf<Any?>(HP.getImageUrl(imageUrl)) }
    var showSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(imageUrl) {
        selectedImageUri = HP.getImageUrl(imageUrl) + "?${System.currentTimeMillis()}"
    }

    // 🔹 Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            val multipart = uriToMultipart(context, it)
            onImageUrlChange(multipart)
        }
    }

    // 🔹 Create temp URI for camera
    val cameraImageUri = remember {
        val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    // 🔹 Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = cameraImageUri
            val multipart = uriToMultipart(context, cameraImageUri)
            onImageUrlChange(multipart)
        }
    }

    // 🔹 Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(cameraImageUri)
        } else {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    // 🔹 Function to handle camera click
    fun openCamera() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                cameraLauncher.launch(cameraImageUri)
            }

            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // 🔹 Image UI
    AsyncImage(
        model = selectedImageUri,
        error = painterResource(R.drawable.select_image),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .clip(shape)
            .clickable { showSheet = true },
        contentScale = ContentScale.Crop
    )

    // 🔹 Bottom Sheet
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            tonalElevation = 0.dp,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        ) {
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setStatusBarColor(
                    color = Color.Transparent,
                    darkIcons = false
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = "Select Image Source",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                ListItem(
                    headlineContent = { Text("Take Photo") },
                    modifier = Modifier.clickable {
                        showSheet = false
                        openCamera()
                    },
                    colors = ListItemDefaults.colors(
                        headlineColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                )

                ListItem(
                    headlineContent = { Text("Choose from Gallery") },
                    modifier = Modifier.clickable {
                        showSheet = false
                        galleryLauncher.launch("image/*")
                    },
                    colors = ListItemDefaults.colors(
                        headlineColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                )
            }
        }
    }
}