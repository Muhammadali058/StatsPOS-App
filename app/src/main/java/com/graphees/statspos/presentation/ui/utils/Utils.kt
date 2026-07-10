package com.graphees.statspos.presentation.ui.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder

// region Paddings
object ConstantPaddings {
    val DEFAULT_TEXTBOX_INSIDE = PaddingValues(
        16.dp
    )
    val CUSTOM_TEXTBOX_OUTSIDE = PaddingValues(
        vertical = 4.dp
    )
    val CUSTOM_TEXTBOX_INSIDE = PaddingValues(
        start = 15.dp,
        top = 10.dp,
        end = 10.dp,
        bottom = 10.dp,
    )
    val BODY_HORIZONTAL = PaddingValues(
        horizontal = 16.dp
    )

    val LIST_PADDING_VERTICAL = 8.dp
}

object ConstantSize {
    val DEFAULT_ICON_BUTTON = 30.dp
    val DEFAULT_ICON = 24.dp
    val DEFAULT_TEXTBOX_HEIGHT = 42.dp
    val ORIGINAL_TEXTBOX_HEIGHT = 52.dp
}
// endregion

// region Reports
const val REPORT_HEADINGS_FONT_SIZE = 12f
const val REPORT_HEADER_FONT_SIZE = 10f
const val REPORT_BODY_FONT_SIZE = 10f
// endregion

fun openPdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    // Show
    context.startActivity(intent)

    // Delete
//    Handler(Looper.getMainLooper()).postDelayed({
//        file.delete()
//    }, 10_000)
    file.deleteOnExit()
}

fun sharePdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        `package` = "com.whatsapp" // To share only with WhatsApp
    }

    // To share only with WhatsApp
//    try {
//        context.startActivity(intent)
//    } catch (e: ActivityNotFoundException) {
//        Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
//    }

    // Share
    context.startActivity(
        Intent.createChooser(intent, "Share Report")
    )

    // Delete
//    Handler(Looper.getMainLooper()).postDelayed({
//        file.delete()
//    }, 10_000)
    file.deleteOnExit()
}

fun openWhatsAppChat(context: Context, phone: String, message: String) {
//    phone must be this format 923030454625
    val url = "https://wa.me/$phone?text=${URLEncoder.encode(message, "UTF-8")}"

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = url.toUri()
    }

    context.startActivity(intent)
}

fun pdfToBitmap(file: File): Bitmap {
    val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    val renderer = PdfRenderer(fd)
    val page = renderer.openPage(0)

    val scale = 3

    val bitmap = createBitmap(page.width * scale, page.height * scale)

    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.WHITE)
    canvas.scale(scale.toFloat(), scale.toFloat())

    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

    page.close()
    renderer.close()
    fd.close()

    return bitmap
}


fun bitmapToFile(context: Context, bitmap: Bitmap): File {
    val file = File(context.cacheDir, "receipt_${System.currentTimeMillis()}.jpg")

    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
    }

    return file
}

fun getImageFromPdf(context: Context, file: File): File {
    val bitmap = pdfToBitmap(file)
    return bitmapToFile(context, bitmap)
}