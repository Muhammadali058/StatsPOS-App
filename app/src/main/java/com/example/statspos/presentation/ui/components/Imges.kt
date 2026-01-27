package com.example.statspos.presentation.ui.components

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.statspos.R
import com.example.statspos.utils.HP
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

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
                model = HP.getImageUrl(imageUrl!!),
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
fun UploadImageView(
    imageUrl: String,
    onImageUrlChange: (MultipartBody.Part) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    shape: Shape = RectangleShape,
) {
    var selectedImageUri by rememberSaveable { mutableStateOf<Any?>(HP.getImageUrl(imageUrl)) }
    LaunchedEffect(imageUrl) {
        selectedImageUri = HP.getImageUrl(imageUrl)
    }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri

        uri?.run {
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
