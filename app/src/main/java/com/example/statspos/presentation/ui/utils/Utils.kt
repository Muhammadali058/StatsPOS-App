package com.example.statspos.presentation.ui.utils

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File

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
    file.deleteOnExit()
}

fun sharePdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "Sales Report")
        putExtra(Intent.EXTRA_TEXT, "Please find attached sales report.")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    // Share
    context.startActivity(
        Intent.createChooser(shareIntent, "Share Report")
    )

    // Delete
    file.deleteOnExit()
}