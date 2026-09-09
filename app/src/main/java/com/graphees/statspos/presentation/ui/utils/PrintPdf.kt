package com.graphees.statspos.presentation.ui.utils

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.scale
import java.io.File

suspend fun printPdf(
    context: Context,
    pdfFile: File,
    printer: BluetoothDevice
): Boolean {

    val printerManager = BluetoothPrinterManager()

    return try {

        // Connect to printer
        printerManager.connect(printer)

        val fileDescriptor =
            ParcelFileDescriptor.open(
                pdfFile,
                ParcelFileDescriptor.MODE_READ_ONLY
            )

        val pdfRenderer = PdfRenderer(fileDescriptor)

        for (pageIndex in 0 until pdfRenderer.pageCount) {

            val page = pdfRenderer.openPage(pageIndex)

            try {

                // ---------------------------------------------------------
                // 1. Render PDF at 1152px width
                // ---------------------------------------------------------

                val renderWidth = 1152

                val scale =
                    renderWidth.toFloat() / page.width

                val renderHeight =
                    (page.height * scale).toInt()

                val highResolutionBitmap =
                    createBitmap(
                        renderWidth,
                        renderHeight
                    )

                highResolutionBitmap.eraseColor(Color.WHITE)

                page.render(
                    highResolutionBitmap,
                    null,
                    null,
                    PdfRenderer.Page.RENDER_MODE_FOR_PRINT
                )

                // ---------------------------------------------------------
                // 2. Crop bottom white area
                // ---------------------------------------------------------

//                val croppedBitmap = highResolutionBitmap
                val croppedBitmap =
                    cropBottomWhiteSpace(
                        highResolutionBitmap,
                        bottomPadding = 8,
                    )

                // ---------------------------------------------------------
                // 3. Downscale to printer width
                //    SP-90A ≈ 576 dots
                // ---------------------------------------------------------

                val printerWidth = 576

                val finalHeight =
                    (croppedBitmap.height *
                            printerWidth.toFloat() /
                            croppedBitmap.width)
                        .toInt()

                val finalBitmap =
                    if (croppedBitmap.width != printerWidth) {

                        croppedBitmap.scale(
                            printerWidth,
                            finalHeight
                        )

                    } else {
                        croppedBitmap
                    }

                // ---------------------------------------------------------
                // 4. Convert bitmap to ESC/POS
                // ---------------------------------------------------------

                val escPosData =
                    bitmapToEscPos(finalBitmap)

                // ---------------------------------------------------------
                // 5. Send to printer
                // ---------------------------------------------------------

                printerManager.print(escPosData)

                // ---------------------------------------------------------
                // 6. Release bitmap memory
                // ---------------------------------------------------------

                finalBitmap.recycle()

            } finally {

                page.close()
            }
        }

        pdfRenderer.close()
        fileDescriptor.close()

        // ---------------------------------------------------------
        // Feed paper
        // ---------------------------------------------------------

        printerManager.print(
            byteArrayOf(
                0x1B,
                0x64,
                0x03
            )
        )

        // ---------------------------------------------------------
        // Cut paper
        // ---------------------------------------------------------

        printerManager.print(
            byteArrayOf(
                0x1D,
                0x56,
                0x00
            )
        )

        true

    } catch (e: Exception) {

        e.printStackTrace()

        false

    } finally {

        printerManager.disconnect()
    }
}

fun bitmapToEscPos(
    bitmap: Bitmap
): ByteArray {

    val width = bitmap.width
    val height = bitmap.height

    // ESC/POS bitmap width must be a multiple of 8
    val widthBytes = (width + 7) / 8

    /*
     * ---------------------------------------------------------
     * 1. Convert bitmap to grayscale
     * ---------------------------------------------------------
     */

    val gray = Array(height) { FloatArray(width) }

    for (y in 0 until height) {

        for (x in 0 until width) {

            val pixel = bitmap[x, y]

            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)

            // Standard luminance calculation
            gray[y][x] =
                0.299f * r +
                        0.587f * g +
                        0.114f * b
        }
    }

    /*
     * ---------------------------------------------------------
     * 2. Floyd-Steinberg dithering
     *
     *       X   7/16
     *
     * 3/16  5/16  1/16
     * ---------------------------------------------------------
     */

    val threshold = 145f

    for (y in 0 until height) {

        for (x in 0 until width) {

            val oldPixel = gray[y][x]

            val newPixel =
                if (oldPixel < threshold) {
                    0f       // Black
                } else {
                    255f     // White
                }

            val error =
                oldPixel - newPixel

            gray[y][x] = newPixel

            // Right pixel: 7/16
            if (x + 1 < width) {

                gray[y][x + 1] +=
                    error * 7f / 16f
            }

            // Bottom-left: 3/16
            if (y + 1 < height && x - 1 >= 0) {

                gray[y + 1][x - 1] +=
                    error * 3f / 16f
            }

            // Bottom: 5/16
            if (y + 1 < height) {

                gray[y + 1][x] +=
                    error * 5f / 16f
            }

            // Bottom-right: 1/16
            if (
                y + 1 < height &&
                x + 1 < width
            ) {

                gray[y + 1][x + 1] +=
                    error * 1f / 16f
            }
        }
    }

    /*
     * ---------------------------------------------------------
     * 3. Convert dithered image to ESC/POS bitmap
     * ---------------------------------------------------------
     */

    val imageData =
        ByteArray(widthBytes * height)

    var index = 0

    for (y in 0 until height) {

        for (xByte in 0 until widthBytes) {

            var byteValue = 0

            for (bit in 0..7) {

                val x =
                    xByte * 8 + bit

                if (x < width) {

                    val pixel =
                        gray[y][x]

                    /*
                     * Black pixel = 1
                     * White pixel = 0
                     */
                    if (pixel < 128f) {

                        byteValue =
                            byteValue or
                                    (1 shl (7 - bit))
                    }
                }
            }

            imageData[index++] =
                byteValue.toByte()
        }
    }

    /*
     * ---------------------------------------------------------
     * 4. ESC/POS GS v 0 raster command
     *
     * GS v 0
     *
     * 1D 76 30 00
     * xL xH
     * yL yH
     * ---------------------------------------------------------
     */

    val header =
        byteArrayOf(

            0x1D,
            0x76,
            0x30,
            0x00,

            // Width in bytes
            (widthBytes and 0xFF).toByte(),
            ((widthBytes shr 8) and 0xFF).toByte(),

            // Height in pixels
            (height and 0xFF).toByte(),
            ((height shr 8) and 0xFF).toByte()
        )

    /*
     * Initialize printer
     */
    val init =
        byteArrayOf(
            0x1B,
            0x40
        )

    /*
     * Center alignment
     */
    val center =
        byteArrayOf(
            0x1B,
            0x61,
            0x01
        )

    /*
     * Feed after image
     */
    val feed =
        byteArrayOf(
            0x0A
        )

    return concatBytes(
        init,
        center,
        header,
        imageData,
        feed
    )
}

fun concatBytes(
    vararg arrays: ByteArray
): ByteArray {

    val totalSize =
        arrays.sumOf {
            it.size
        }

    val result =
        ByteArray(totalSize)

    var position = 0

    for (array in arrays) {

        System.arraycopy(
            array,
            0,
            result,
            position,
            array.size
        )

        position += array.size
    }

    return result
}

fun cropBottomWhiteSpace(
    bitmap: Bitmap,
    bottomPadding: Int = 8
): Bitmap {

    val width = bitmap.width
    val height = bitmap.height

    var lastContentRow = height - 1

    // Scan from bottom to top
    for (y in height - 1 downTo 0) {

        var hasContent = false

        for (x in 0 until width) {

            val pixel = bitmap[x, y]

            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)

            /*
             * Consider the pixel content if it is not
             * almost white.
             */
            if (
                red < 245 ||
                green < 245 ||
                blue < 245
            ) {
                hasContent = true
                break
            }
        }

        if (hasContent) {
            lastContentRow = y
            break
        }
    }

    // Add a small bottom margin
    val croppedHeight =
        (lastContentRow + 1 + bottomPadding)
            .coerceAtMost(height)

    return Bitmap.createBitmap(
        bitmap,
        0,
        0,
        width,
        croppedHeight
    )
}