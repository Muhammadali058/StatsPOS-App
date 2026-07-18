package com.graphees.statspos.presentation.ui.screens.reports.accounts

import android.content.Context
import com.graphees.statspos.domain.models.reports.TotalReport
import com.graphees.statspos.domain.models.reports.accounts.ShiftReport
import com.graphees.statspos.presentation.ui.utils.REPORT_HEADER_FONT_SIZE
import com.graphees.statspos.utils.HP
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFontFactory
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

fun shiftReport(
    context: Context,
    shiftReport: List<ShiftReport>,
    totalReport: TotalReport,
): File {
    // region Document
    val file = File(context.cacheDir, "Shift_Report_${System.currentTimeMillis()}.pdf")
    if (file.exists()) {
        file.delete()
    }

    val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)

    val writer = PdfWriter(file)
    val pdf = PdfDocument(writer)
    val pageWidth = 226.77f
    val pageHeight = 500f
    val pageSize = PageSize(pageWidth, pageHeight)
    val document = Document(pdf, pageSize)
    document.setMargins(5f, 5f, 5f, 5f)
    // endregion

    // region Header
    val title = Paragraph("Shift Report")
        .setFont(boldFont)
        .setFontSize(14f)
        .setTextAlignment(TextAlignment.CENTER)

    document.add(title)

    val topFontSize = 10f
    val bodyFontSize = 8f

    val topTable = Table(UnitValue.createPercentArray(floatArrayOf(100f))).useAllAvailableWidth()

    topTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("User: ").setFont(boldFont))
                    .add(Text(totalReport.username.toString()))
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    topTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Status: ").setFont(boldFont))
                    .add(Text(totalReport.status.toString()))
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    topTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Open: ").setFont(boldFont))
                    .add(Text(totalReport.openDate.toString()))
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    topTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Close: ").setFont(boldFont))
                    .add(Text(totalReport.closeDate.toString()))
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    document.add(topTable)
    // endregion

    // region Details
    // ---------------- Table ----------------
    val columnWidths = floatArrayOf(3f, 1f, 1f)

    val bodyTable = Table(UnitValue.createPercentArray(columnWidths), true)
    bodyTable.setWidth(UnitValue.createPercentValue(100f))
    document.add(bodyTable)

    val headers = listOf(
        "Details",
        "In",
        "Out",
    )

    headers.forEachIndexed { index, item ->
        val cell = Cell().add(
            Paragraph(item)
                .setFont(boldFont)
                .setFontSize(REPORT_HEADER_FONT_SIZE)
        )

        cell.setTextAlignment(TextAlignment.CENTER)
        bodyTable.addHeaderCell(cell)
    }

    bodyTable.addCell(
        Cell().add(Paragraph("Opening Balance").setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.LEFT).setFont(boldFont)
    )
    bodyTable.addCell(
        Cell().add(Paragraph(HP.formatDecimal(totalReport.openingBalance)).setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.CENTER).setFont(boldFont)
    )
    bodyTable.addCell(
        Cell().add(Paragraph("").setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.CENTER)
    )

    // Sales
    bodyTable.addCell(
        Cell().add(Paragraph("Sales").setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.LEFT)
    )
    bodyTable.addCell(
        Cell().add(Paragraph(HP.formatDecimal(totalReport.sales)).setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.CENTER)
    )
    bodyTable.addCell(
        Cell().add(Paragraph("").setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.CENTER)
    )

    // Purchase
    bodyTable.addCell(
        Cell().add(Paragraph("Purchase").setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.LEFT)
    )
    bodyTable.addCell(
        Cell().add(Paragraph("").setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.CENTER)
    )
    bodyTable.addCell(
        Cell().add(Paragraph(HP.formatDecimal(totalReport.purchase)).setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.CENTER)
    )

    // Receipts
    bodyTable.addCell(
        Cell().add(Paragraph("Receipts").setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.LEFT)
    )
    bodyTable.addCell(
        Cell().add(Paragraph(HP.formatDecimal(totalReport.receipts)).setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.CENTER)
    )
    bodyTable.addCell(
        Cell().add(Paragraph("").setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.CENTER)
    )

    // Payments
    bodyTable.addCell(
        Cell().add(Paragraph("Payments").setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.LEFT)
    )
    bodyTable.addCell(
        Cell().add(Paragraph("").setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.CENTER)
    )
    bodyTable.addCell(
        Cell().add(Paragraph(HP.formatDecimal(totalReport.payments)).setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.CENTER)
    )

    // Expenses
    bodyTable.addCell(
        Cell().add(Paragraph("Expenses").setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.LEFT)
    )
    bodyTable.addCell(
        Cell().add(Paragraph("").setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.CENTER)
    )
    bodyTable.addCell(
        Cell().add(Paragraph(HP.formatDecimal(totalReport.expenses)).setFontSize(bodyFontSize))
            .setTextAlignment(TextAlignment.CENTER)
    )


    var counter = 0
    shiftReport.forEach { item ->
        bodyTable.addCell(
            Cell().add(Paragraph(item.naration.toString()).setFontSize(bodyFontSize))
                .setTextAlignment(TextAlignment.LEFT)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(if(item.amount!! > 0) HP.formatDecimal(item.amount) else "0").setFontSize(bodyFontSize))
                .setTextAlignment(TextAlignment.CENTER)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(if(item.amount!! > 0) "0" else HP.formatDecimal(item.amount)).setFontSize(bodyFontSize))
                .setTextAlignment(TextAlignment.CENTER)
        )

        counter++
        if (counter % 100 == 0) {
            bodyTable.flush()
        }
    }

    bodyTable.complete()
    // endregion

    // region Footer
    // ---------------- Row1 ----------------
    val row1 =
        Table(UnitValue.createPercentArray(floatArrayOf(100f)))
            .useAllAvailableWidth()
    val totalCell = Cell()
        .add(
            Paragraph()
                .add(Text("Expected Cash: ").setFont(boldFont))
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(70f))
//                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph(HP.formatDecimal(totalReport.expectedCash))
                                .setTextAlignment(TextAlignment.RIGHT)
                        )
                )
        )
        .setFontSize(topFontSize)
        .setBorder(null)
        .setTextAlignment(TextAlignment.RIGHT)

    row1.addCell(totalCell)
    document.add(row1)

    // ---------------- Row2 ----------------
    val row2 =
        Table(UnitValue.createPercentArray(floatArrayOf(100f)))
            .useAllAvailableWidth()
            .setMarginTop(-5f)
    val discTableCell = Cell()
        .add(
            Paragraph()
                .add(Text("Cash in Hand: ").setFont(boldFont))
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(70f))
                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph(HP.formatDecimal(totalReport.cashInHand))
                                .setTextAlignment(TextAlignment.RIGHT)
                        )
                )
        )
        .setFontSize(topFontSize)
        .setBorder(null)
        .setTextAlignment(TextAlignment.RIGHT)

    row2.addCell(discTableCell)
    document.add(row2)

    // ---------------- Row3 ----------------
    val row3 =
        Table(UnitValue.createPercentArray(floatArrayOf(100f)))
            .useAllAvailableWidth()
            .setMarginTop(-5f)
    val grandTotalTableCell = Cell()
        .add(
            Paragraph()
                .add(Text("Closing Balance: ").setFont(boldFont))
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(70f))
                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph(HP.formatDecimal(totalReport.closingBalance))
                                .setTextAlignment(TextAlignment.RIGHT)
                        )
                )
        )
        .setFontSize(topFontSize)
        .setBorder(null)
        .setTextAlignment(TextAlignment.RIGHT)

    row3.addCell(grandTotalTableCell)
    document.add(row3)

    // Bank Sales
    val bottomTable = Table(UnitValue.createPercentArray(floatArrayOf(100f))).useAllAvailableWidth()
    bottomTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Bank Sales: ").setFont(boldFont))
                    .add(Text(HP.formatDecimal(totalReport.bankSales)))
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )
    document.add(bottomTable)

    // endregion

    document.close()
    return file
}