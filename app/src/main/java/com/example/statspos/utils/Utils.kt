package com.example.statspos.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import com.example.statspos.R
import com.example.statspos.presentation.ui.utils.REPORT_BODY_FONT_SIZE
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.VerticalAlignment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URL
import android.graphics.Color
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.graphics.withTranslation


enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

enum class PasswordFor {
    DELETE_ITEM,
    DELETE_ACCOUNT,
    EDIT_SALES_BILL,
    EDIT_PURCHASE_BILL,
    DELETE_SALES_BILL,
    DELETE_PURCHASE_BILL,
    DELETE_ENTRY,
    PRINT_DUPLICATES,
    AUDIT,
}

enum class EntryType {
    RECEIPT,
    PAYMENT,
    EXPENSE,
    SALES,
    SALES_RETURN,
    PURCHASE,
    PURCHASE_RETURN,
    JOURNAL,
    STOCK,
}

enum class FixedAccounts {
    CASH,
    SALES,
    SALES_RETURN,
    PURCHASE,
    PURCHASE_RETURN,
}

enum class UserTypes {
    ADMINISTRATOR,
    POS_USER,
    INVENTORY_MANAGER,
}

fun getEntryType(entryType: EntryType): Int {
    return entryType.ordinal + 1
}

fun getFixedAccount(fixedAccount: FixedAccounts): Long {
    return (fixedAccount.ordinal + 1).toLong()
}

fun getUserType(userType: UserTypes): Long {
    return (userType.ordinal + 1).toLong()
}

fun Context.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, msg, length).show()

sealed class UiEvent {
    data object Idle : UiEvent()
    data class ShowSnackbar(
        val message: String,
        val type: SnackbarType = SnackbarType.INFORMATION
    ) : UiEvent()

    data class ShowMessage(val message: String) : UiEvent()
    data class ShowError(val error: String) : UiEvent()
    data class ShowConfirmDialog(val message: String, val type: Int) : UiEvent()
}

enum class SnackbarType { INFORMATION, ERROR }

suspend fun checkEvent(
    event: UiEvent,
    snackbarHostState: SnackbarHostState,
    onError: (String) -> Unit,
    onMessage: (String) -> Unit = {},
    onConfirm: (String, Int) -> Unit = { message, type -> },
    viewModelIdleEvent: (event: UiEvent) -> Unit,
//    changeSnackbarType: (SnackbarType) -> Unit
) {
    when (event) {
        is UiEvent.ShowSnackbar -> {

//            val snackbarType = when (event.type) {
//                SnackbarType.INFORMATION -> SnackbarType.INFORMATION
//                else -> SnackbarType.ERROR
//            }
//            changeSnackbarType(snackbarType)

            snackbarHostState.showSnackbar(
                message = event.message,
                withDismissAction = true,
            )
            viewModelIdleEvent(UiEvent.Idle)
//            viewModel.onEvent(UiEvent.Idle)
        }

        is UiEvent.ShowMessage -> {
            onMessage(event.message)
            viewModelIdleEvent(UiEvent.Idle)
//            viewModel.onEvent(UiEvent.Idle)
        }

        is UiEvent.ShowError -> {
            onError(event.error)
            viewModelIdleEvent(UiEvent.Idle)
//            viewModel.onEvent(UiEvent.Idle)
        }

        is UiEvent.ShowConfirmDialog -> {
            onConfirm(event.message, event.type)
            viewModelIdleEvent(UiEvent.Idle)
//            viewModel.onEvent(UiEvent.Idle)
        }

        else -> {}
    }
}

inline fun <reified T> Gson.getListOf(jsonArray: String): List<T> =
    fromJson(jsonArray, object : TypeToken<List<T>>() {}.type)

inline fun <reified T> Gson.getListOf(jsonArray: JsonArray): List<T> =
    fromJson(jsonArray, object : TypeToken<List<T>>() {}.type)

inline fun <reified T> Gson.get(jsonObject: JsonObject): T =
    fromJson(jsonObject, object : TypeToken<T>() {}.type)

inline fun <reified T> Gson.get(jsonObject: String): T =
    fromJson(jsonObject, object : TypeToken<T>() {}.type)


object ImageCache {

    private val cache = HashMap<String, ByteArray>()

    suspend fun getImage(url: String): ByteArray? {

        cache[url]?.let { return it }

        val bytes = withContext(Dispatchers.IO) {
            try {
                URL(url).readBytes()
            } catch (e: Exception) {
                null
            }
        }

        if (bytes != null) {
            cache[url] = bytes
        }

        return bytes
    }

    fun getCached(url: String): ByteArray? {
        return cache[url]
    }
}

suspend fun preloadImages(urls: List<String>) = coroutineScope {
    urls.toSet().map { url ->
        async(Dispatchers.IO) {
            ImageCache.getImage(url)
        }
    }.awaitAll()
}

fun urduTextToPdfImage(
    context: Context,
    text: String,
    fontSize: Float = REPORT_BODY_FONT_SIZE,
    resolution: Float = 2f,
): Image {
    val textView = TextView(context)
    textView.text = text
    textView.textSize = fontSize
    textView.typeface = ResourcesCompat.getFont(context, R.font.jameel)
    textView.setTextColor(android.graphics.Color.BLACK)
    textView.includeFontPadding = false
    textView.setPadding(0, 0, 0, 0)

    textView.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )

    val width = (textView.measuredWidth * resolution).toInt()
    val height = (textView.measuredHeight * resolution).toInt()

    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    canvas.scale(resolution, resolution)

    textView.layout(0, 0, textView.measuredWidth, textView.measuredHeight)
    textView.draw(canvas)

    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

    val imageData = ImageDataFactory.create(stream.toByteArray())
    val image = Image(imageData)

    // adjust to typical font height in PDF
    image.scaleToFit(120f, 18f)

    return image
}

fun getReportImage(
    imageUrl: String,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.CENTER,
    width: Float = 50f,
    height: Float = 50f,
): Image? {
    val bytes = ImageCache.getCached(imageUrl)
    return if (bytes != null)
        Image(ImageDataFactory.create(bytes))
            .scaleToFit(width, height)
            .setHorizontalAlignment(horizontalAlignment)
    else
        null
}

fun getDefaultImageCell(
    imageUrl: String,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.CENTER,
    width: Float = 50f,
    height: Float = 50f,
    imageText: String = "No Image",
): Cell {
    val image = getReportImage(imageUrl, horizontalAlignment, width, height)

    return if (image != null) {
        Cell().add(image)
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
    } else
        Cell().add(
            Paragraph(imageText)
                .setFontSize(REPORT_BODY_FONT_SIZE)
        ).setTextAlignment(TextAlignment.CENTER)
}
