package com.graphees.statspos.presentation.ui.screens.reports.accounts

import android.content.Context
import com.graphees.statspos.domain.models.reports.TotalReport
import com.graphees.statspos.domain.models.reports.accounts.AccountReport
import com.graphees.statspos.presentation.ui.components.PageXofYEventHandler
import com.graphees.statspos.presentation.ui.utils.REPORT_BODY_FONT_SIZE
import com.graphees.statspos.presentation.ui.utils.REPORT_HEADER_FONT_SIZE
import com.graphees.statspos.presentation.ui.utils.REPORT_HEADINGS_FONT_SIZE
import com.graphees.statspos.utils.HP
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.events.PdfDocumentEvent
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

fun incomeStatementReport(
    context: Context,
    fromDate: String,
    toDate: String,
    accountReport: List<AccountReport>,
    totalReport: TotalReport,
): File {
    // region Document
    val file = File(context.cacheDir, "Income_Statement_${System.currentTimeMillis()}.pdf")
    if (file.exists()) {
        file.delete()
    }

    val writer = PdfWriter(file)
    val pdf = PdfDocument(writer)
    val pageHandler = PageXofYEventHandler(pdf)
    pdf.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler)
    val document = Document(pdf)

    document.setMargins(20f, 20f, 20f, 20f)
    // endregion

    // region Header
    // ---------------- Report Title ----------------
    val title = Paragraph("Income Statement")
        .setBold()
        .setFontSize(24f)
        .setTextAlignment(TextAlignment.CENTER)

    document.add(title)

    // ---------------- Date ----------------
    val dateTable =
        Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
    val fromDateCell = Cell()
        .add(
            Paragraph()
                .add(Text("From Date: ").setBold())
                .add(Text(fromDate))
        )
        .setFontSize(REPORT_HEADINGS_FONT_SIZE)
        .setBorder(null)
        .setTextAlignment(TextAlignment.LEFT)

    val toDateCell = Cell()
        .add(
            Paragraph()
                .add(Text("To Date: ").setBold())
                .add(Text(toDate))
        )
        .setFontSize(REPORT_HEADINGS_FONT_SIZE)
        .setBorder(null)
        .setTextAlignment(TextAlignment.RIGHT)

    dateTable.addCell(fromDateCell)
    dateTable.addCell(toDateCell)
    document.add(dateTable)
//    document.add(Paragraph("\n"))
    // endregion

    // region Details
    // ---------------- Table ----------------
    val columnWidths = floatArrayOf(4f, 1f)

    val bodyTable = Table(UnitValue.createPercentArray(columnWidths), true)
    bodyTable.setWidth(UnitValue.createPercentValue(100f))
    document.add(bodyTable)

    val headers = listOf(
        "Details",
        "Amount",
    )

    headers.forEachIndexed { index, item ->
        val cell = Cell().add(
            Paragraph(item)
                .setBold()
                .setFontSize(REPORT_HEADER_FONT_SIZE)
        )

        cell.setTextAlignment(TextAlignment.CENTER)
        bodyTable.addHeaderCell(cell)
    }


    // ---------------- Row1 ----------------
    bodyTable.addCell(
        Cell().add(
            Paragraph()
                .add(Text("Sales: "))
        )
            .setBorderBottom(null)
            .setFontSize(REPORT_BODY_FONT_SIZE)
            .setTextAlignment(TextAlignment.LEFT)
    )
    bodyTable.addCell(
        Cell().add(Paragraph(HP.formatDecimal(totalReport.sales)).setFontSize(REPORT_BODY_FONT_SIZE))
            .setBorderBottom(null)
            .setTextAlignment(TextAlignment.CENTER)
    )

    // ---------------- Row2 ----------------
    bodyTable.addCell(
        Cell().add(
            Paragraph()
                .add(Text("Less: ").setBold())
                .add(Text("Cost of goods sold"))
        )
            .setBorderBottom(null)
            .setBorderTop(null)
            .setFontSize(REPORT_BODY_FONT_SIZE)
            .setTextAlignment(TextAlignment.LEFT)
    )
    bodyTable.addCell(
        Cell().add(Paragraph(HP.formatDecimal(totalReport.cgs)).setFontSize(REPORT_BODY_FONT_SIZE))
            .setBorderTop(null)
            .setTextAlignment(TextAlignment.CENTER)
    )

    // ---------------- Row3 ----------------
    bodyTable.addCell(
        Cell().add(Paragraph(if (totalReport.grossProfit!! > 0.0) "Gross Profit" else "Gross Loss"))
            .setBold().setFontSize(REPORT_BODY_FONT_SIZE)
            .setBorderBottom(null)
            .setBorderTop(null)
            .setTextAlignment(TextAlignment.RIGHT)
    )

    bodyTable.addCell(
        Cell().add(
            Paragraph(HP.formatDecimal(totalReport.grossProfit)).setFontSize(REPORT_BODY_FONT_SIZE)
        )
            .setBorderBottom(null)
            .setBorderTop(null)
            .setTextAlignment(TextAlignment.CENTER)
    )

    // ---------------- Row4 ----------------
    bodyTable.addCell(
        Cell().add(Paragraph("Expenses").setBold().setFontSize(REPORT_BODY_FONT_SIZE))
            .setBorderTop(null)
            .setTextAlignment(TextAlignment.CENTER)
    )
    bodyTable.addCell(
        Cell().add(Paragraph("").setFontSize(REPORT_BODY_FONT_SIZE))
            .setBorderTop(null)
            .setTextAlignment(TextAlignment.CENTER)
    )


    var counter = 0
    accountReport.forEach { item ->
        bodyTable.addCell(
            Cell().add(
                Paragraph()
                    .add(Text(item.date.toString() + " ").setBold())
                    .add(Text(item.expense.toString()))
            )
                .setFontSize(REPORT_BODY_FONT_SIZE)
                .setTextAlignment(TextAlignment.LEFT)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(HP.formatDecimal(item.amount)).setFontSize(REPORT_BODY_FONT_SIZE))
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

    val totalExpensesCell = Cell()
        .add(
            Paragraph()
                .add(Text("Total Expenses: ").setBold())
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(120f))
                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph(HP.formatDecimal(totalReport.totalExpenses))
                                .setTextAlignment(TextAlignment.CENTER)
                        )
                )
        )
        .setFontSize(REPORT_HEADINGS_FONT_SIZE)
        .setBorder(null)
        .setTextAlignment(TextAlignment.RIGHT)

    row1.addCell(totalExpensesCell)
    document.add(row1)


    // ---------------- Row2 ----------------
    val row2 =
        Table(UnitValue.createPercentArray(floatArrayOf(100f)))
            .useAllAvailableWidth()

    val netProfitCell = Cell()
        .add(
            Paragraph()
                .add(Text(if (totalReport.netProfit!! > 0.0) "Net Profit: " else "Net Loss: ").setBold())
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(120f))
                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph(HP.formatDecimal(totalReport.netProfit))
                                .setTextAlignment(TextAlignment.CENTER)
                        )
                )
        )
        .setFontSize(REPORT_HEADINGS_FONT_SIZE)
        .setBorder(null)
        .setTextAlignment(TextAlignment.RIGHT)

    row2.addCell(netProfitCell)
    document.add(row2)
    // endregion

    pageHandler.writeTotal(pdf)
    document.close()
    return file
}