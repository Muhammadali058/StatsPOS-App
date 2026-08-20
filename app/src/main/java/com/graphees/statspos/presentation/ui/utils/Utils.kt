package com.graphees.statspos.presentation.ui.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.showToast
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder

// region Paddings
object ConstantPaddings {
    val DEFAULT_TEXTBOX_INSIDE = PaddingValues(
        16.dp
    )
    val CUSTOM_TEXTBOX_OUTSIDE = PaddingValues(
        vertical = 5.dp
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
    val DEFAULT_RADIUS = 10.dp
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

fun sharePdfToWhatsApp(
    context: Context,
    file: File,
    contact: String
) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val phone = contact
        .replace(Regex("[^0-9]"), "")
        .let {
            when {
                it.startsWith("0") -> "92${it.substring(1)}"
                it.startsWith("92") -> it
                else -> it
            }
        }

    val whatsappPackage = "com.whatsapp"
    val businessPackage = "com.whatsapp.w4b"

    val pm = context.packageManager

    fun isInstalled(packageName: String): Boolean {
        return try {
            pm.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    val whatsappInstalled = isInstalled(whatsappPackage)
    val businessInstalled = isInstalled(businessPackage)

    if (!whatsappInstalled && !businessInstalled) {
        Toast.makeText(
            context,
            "WhatsApp is not installed",
            Toast.LENGTH_LONG
        ).show()
        return
    }

    fun createWhatsAppIntent(packageName: String): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra("jid", "$phone@s.whatsapp.net")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            `package` = packageName
        }
    }

    when {
        whatsappInstalled && businessInstalled -> {

            // Both installed → show chooser
            val whatsappIntent = createWhatsAppIntent(whatsappPackage)
            val businessIntent = createWhatsAppIntent(businessPackage)

            val chooser = Intent.createChooser(
                whatsappIntent,
                "Share with"
            ).apply {
                putExtra(
                    Intent.EXTRA_INITIAL_INTENTS,
                    arrayOf(businessIntent)
                )
            }

            context.startActivity(chooser)
        }

        whatsappInstalled -> {
            // Only normal WhatsApp
            context.startActivity(
                createWhatsAppIntent(whatsappPackage)
            )
        }

        businessInstalled -> {
            // Only WhatsApp Business
            context.startActivity(
                createWhatsAppIntent(businessPackage)
            )
        }
    }
}
private fun isAppInstalled(
    context: Context,
    packageName: String
): Boolean {
    return try {
        context.packageManager.getPackageInfo(
            packageName,
            0
        )
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun sharePdf(
    context: Context,
    file: File,
    contact: String = "",
) {
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

//    val intent = Intent(
//        Intent.ACTION_VIEW,
//        "https://wa.me/$contact?text=${if (addClient) message1 else message}".toUri()
//    )

    // Share
    context.startActivity(
        Intent.createChooser(intent, "Share Report")
    )

    // Delete
    file.deleteOnExit()
}

fun openCall(
    context: Context,
    contact: String = HP.getContact(HP.graphees.contact!!).replace("-", ""),
) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = "tel:${contact}".toUri()
    }
    context.startActivity(intent)
}

fun openWhatsapp(
    context: Context,
    contact: String = HP.getContact(HP.graphees.contact!!),
    message: String = "",
    addClient: Boolean = false
) {
    val message1 = Uri.encode(
        """
        Welcome: SP${HP.clientId} ${HP.client.clientName},
        Your msg here...
        $message
        """.trimIndent()
    )

    val intent = Intent(
        Intent.ACTION_VIEW,
        "https://wa.me/$contact?text=${if (addClient) message1 else message}".toUri()
    )
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        context.showToast("No application found to open WhatsApp.")
    }
}

fun shareLocationOnWhatsApp(
    context: Context,
    latitude: Double,
    longitude: Double
) {
    val locationUrl = "https://maps.google.com/?q=$latitude,$longitude"

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, locationUrl)
    }

    val packageManager = context.packageManager

    when {
        sendIntent.apply { `package` = "com.whatsapp" }
            .resolveActivity(packageManager) != null -> {
            context.startActivity(sendIntent)
        }

        sendIntent.apply { `package` = "com.whatsapp.w4b" }
            .resolveActivity(packageManager) != null -> {
            context.startActivity(sendIntent)
        }

        else -> {
            // Let the user choose another app
            sendIntent.`package` = null
            context.startActivity(Intent.createChooser(sendIntent, "Share location"))
        }
    }
}

fun sendEmail(
    context: Context,
    email: String = HP.graphees.email!!,
    subject: String = "StatsPOS Support",
    message:String = "",
) {
    val body = "Hello, SP${HP.clientId} ${HP.client.clientName} here.\n\n"

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body + message)
    }

    try {
        context.startActivity(
            Intent.createChooser(intent, "Choose an email app")
        )
    } catch (e: ActivityNotFoundException) {
        context.showToast("No email application found.")
    }
}

fun openGoogleMaps(
    context: Context,
    latitude: Double,
    longitude: Double
) {
    val uri = "geo:$latitude,$longitude?q=$latitude,$longitude".toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        // Fallback to browser
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude".toUri()
        )
        context.startActivity(browserIntent)
    }
}