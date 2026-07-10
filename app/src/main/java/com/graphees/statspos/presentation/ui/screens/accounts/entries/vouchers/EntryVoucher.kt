package com.graphees.statspos.presentation.ui.screens.accounts.entries.vouchers

import android.content.Context
import com.graphees.statspos.domain.models.reports.accounts.AccountReport
import com.graphees.statspos.domain.models.accounts.EntryVoucher
import com.graphees.statspos.utils.EntryType
import com.graphees.statspos.utils.HP
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Div
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import kotlin.math.abs

fun entryVoucher(
    context: Context,
    entryType: EntryType,
    entry: EntryVoucher,
    ledger: List<AccountReport>?,
): File {
    // region Document
    val file = File(context.cacheDir, "${if (entryType == EntryType.RECEIPT) "Receipt" else "Payment"}_Voucher_${System.currentTimeMillis()}.pdf")
    if (file.exists()) {
        file.delete()
    }

    val writer = PdfWriter(file)
    val pdf = PdfDocument(writer)
    val pageWidth = 226.77f
    val pageHeight = if(HP.settings.showLedgerInVoucher == true) 470f else 320f
    val pageSize = PageSize(pageWidth, pageHeight)
    val document = Document(pdf, pageSize)
    document.setMargins(5f, 5f, 5f, 5f)
    // endregion

    // region Header
    // ---------------- Report Title ----------------
    if(HP.printSettings.shopName.toString().isNotEmpty()) {
        document.add(
            Paragraph(HP.printSettings.shopName.toString())
                .setBold()
                .setFontSize(18f)
                .setTextAlignment(TextAlignment.CENTER)
        )
    }

    val title = Paragraph("${if (entryType == EntryType.RECEIPT) "Receipt" else "Payment"} Voucher")
        .setBold()
        .setFontSize(14f)
        .setTextAlignment(TextAlignment.CENTER)

    document.add(title)

    // ---------------- Top ----------------
    val topFontSize = 10f
    val dateTable = Table(UnitValue.createPercentArray(floatArrayOf(100f))).useAllAvailableWidth()

    dateTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text(if (entryType == EntryType.RECEIPT) "Received From: " else "Paid To: ").setBold())
                    .add(Text(entry.accountName.toString()))
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    dateTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("MOP: ").setBold())
                    .add(Text(entry.mop.toString()))
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    dateTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Date: ").setBold())
                    .add(Text(entry.date.toString()))
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    dateTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(
                        Div()
                            .setWidth(UnitValue.createPointValue(210f))
                            .setBorderTop(SolidBorder(ColorConstants.BLACK, 1f))
                    ).setMarginTop(-5f)

            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.CENTER)
    )

    document.add(dateTable)
    // endregion

    // region Footer
    // ---------------- Row1 ----------------
    val row1 =
        Table(UnitValue.createPercentArray(floatArrayOf(100f)))
            .useAllAvailableWidth()

    row1.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Old Balance: ").setBold())
                    .add(
                        Div()
                            .setWidth(UnitValue.createPointValue(100f))
//                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                            .add(
                                Paragraph("${HP.formatDecimal(abs(entry.oldBalance!!))} ${if (entry.oldBalance!! > 0) "R" else "P"}")
                                    .setTextAlignment(TextAlignment.CENTER)
                            )
                    )
            )
            .setFontSize(10f)
            .setBorder(null)
            .setTextAlignment(TextAlignment.RIGHT)
    )
    row1.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text(if (entryType == EntryType.RECEIPT) "Received: " else "Paid: ").setBold())
                    .add(
                        Div()
                            .setWidth(UnitValue.createPointValue(100f))
                            .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                            .add(
                                Paragraph(HP.formatDecimal(entry.amount))
                                    .setTextAlignment(TextAlignment.CENTER)
                            )
                    )
            )
            .setFontSize(10f)
            .setBorder(null)
            .setTextAlignment(TextAlignment.RIGHT)
    )
    row1.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("New Balance: ").setBold())
                    .add(
                        Div()
                            .setWidth(UnitValue.createPointValue(100f))
                            .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                            .add(
                                Paragraph("${HP.formatDecimal(abs(entry.newBalance!!))} ${if (entry.newBalance!! > 0) "R" else "P"}")
                                    .setTextAlignment(TextAlignment.CENTER)
                            )
                    )
            )
            .setFontSize(10f)
            .setBorder(null)
            .setTextAlignment(TextAlignment.RIGHT)
    )
    document.add(row1)

    document.add(
        Paragraph("Naration: ")
            .setBold()
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.LEFT)
    )

    document.add(
        Paragraph(entry.naration)
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.LEFT)
    )

    // endregion

    // region Ledger
    if (HP.settings.showLedgerInVoucher == true) {

        document.add(
            Paragraph("Last Five Entries")
                .setBold()
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.CENTER)
        )

        // ---------------- Table ----------------
        val columnWidths = floatArrayOf(0.7f, 3f, 1f, 1f, 1f)

        val bodyTable = Table(UnitValue.createPercentArray(columnWidths), true)
        bodyTable.setWidth(UnitValue.createPercentValue(100f))
        document.add(bodyTable)

        val fontSize = 6f

        val headers = listOf(
            "Date",
            "Details",
            "Debit",
            "Credit",
            "Balance",
        )

        headers.forEachIndexed { index, item ->
            val cell = Cell().add(
                Paragraph(item)
                    .setBold()
                    .setFontSize(fontSize)
            )

            cell.setTextAlignment(TextAlignment.CENTER)
            bodyTable.addHeaderCell(cell)
        }

        var counter = 0

        ledger?.forEach { item ->
            bodyTable.addCell(
                Cell().add(Paragraph(item.date.toString()).setFontSize(fontSize))
                    .setTextAlignment(TextAlignment.CENTER)
            )

            bodyTable.addCell(
                Cell().add(Paragraph(item.naration.toString()).setFontSize(fontSize))
                    .setTextAlignment(TextAlignment.LEFT)
            )

            bodyTable.addCell(
                Cell().add(Paragraph(HP.formatDecimal(item.debit)).setFontSize(fontSize))
                    .setTextAlignment(TextAlignment.CENTER)
            )

            bodyTable.addCell(
                Cell().add(
                    Paragraph(HP.formatDecimal(item.credit)).setFontSize(
                        fontSize
                    )
                )
                    .setTextAlignment(TextAlignment.CENTER)
            )

            bodyTable.addCell(
                Cell().add(
                    Paragraph(HP.formatDecimal(abs(item.balance!!))).setFontSize(
                        fontSize
                    )
                )
                    .setTextAlignment(TextAlignment.CENTER)
            )

            counter++
            if (counter % 100 == 0) {
                bodyTable.flush()
            }
        }

        bodyTable.complete()
    }
    // endregion

    document.close()
    return file
}