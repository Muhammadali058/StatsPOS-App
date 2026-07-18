package com.graphees.statspos.presentation.ui.screens.reports.profit

import android.content.Context
import com.graphees.statspos.domain.models.reports.TotalReport
import com.graphees.statspos.domain.models.reports.profit.ProfitBillWiseReport
import com.graphees.statspos.presentation.ui.components.PageXofYEventHandler
import com.graphees.statspos.presentation.ui.utils.REPORT_BODY_FONT_SIZE
import com.graphees.statspos.presentation.ui.utils.REPORT_HEADER_FONT_SIZE
import com.graphees.statspos.presentation.ui.utils.REPORT_HEADINGS_FONT_SIZE
import com.graphees.statspos.utils.HP
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.font.PdfFontFactory
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

fun profitBillWiseReport(
    context: Context,
    fromDate: String,
    toDate: String,
    billWiseReport: List<ProfitBillWiseReport>,
    totalReport: TotalReport,
): File {
    // region Document
    val file = File(context.cacheDir, "Profit_Report_${System.currentTimeMillis()}.pdf")
    if (file.exists()) {
        file.delete()
    }

    val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)

    val writer = PdfWriter(file)
    val pdf = PdfDocument(writer)
    val pageHandler = PageXofYEventHandler(pdf)
    pdf.addEventHandler(PdfDocumentEvent.END_PAGE, pageHandler)
    val document = Document(pdf)

    document.setMargins(20f, 20f, 20f, 20f)
    // endregion

    // region Header
    // ---------------- Report Title ----------------
    val title = Paragraph("Profit Report")
        .setFont(boldFont)
        .setFontSize(24f)
        .setTextAlignment(TextAlignment.CENTER)

    document.add(title)

    // ---------------- Date ----------------
    val dateTable =
        Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
    val fromDateCell = Cell()
        .add(
            Paragraph()
                .add(Text("From Date: ").setFont(boldFont))
                .add(Text(fromDate))
        )
        .setFontSize(REPORT_HEADINGS_FONT_SIZE)
        .setBorder(null)
        .setTextAlignment(TextAlignment.LEFT)

    val toDateCell = Cell()
        .add(
            Paragraph()
                .add(Text("To Date: ").setFont(boldFont))
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
    val columnWidths = floatArrayOf(1f, 0.5f, 2f, 1f, 1f, 0.5f, 1f)
    val bodyTable = Table(UnitValue.createPercentArray(columnWidths), true)
    bodyTable.setWidth(UnitValue.createPercentValue(100f))
    document.add(bodyTable)

    val headers = listOf(
        "Date",
        "Sr.",
        "Customer",
        "Sale",
        "Cost",
        "(%)",
        "Profit",
    )

    headers.forEachIndexed { index, item ->
        val cell = Cell().add(
            Paragraph(item)
                .setFont(boldFont)
                .setFontSize(REPORT_HEADER_FONT_SIZE)
        )

//        if (index == 2 || index == 3) {
//            cell.setTextAlignment(TextAlignment.LEFT)
//        } else {
//            cell.setTextAlignment(TextAlignment.CENTER)
//        }

        cell.setTextAlignment(TextAlignment.CENTER)
        bodyTable.addHeaderCell(cell)
    }

    var counter = 0
    billWiseReport.forEach { item ->
        bodyTable.addCell(
            Cell().add(Paragraph(item.date.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(item.salesId.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(item.customerName).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.LEFT)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(HP.formatDecimal(item.total)).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(HP.formatDecimal(item.cost)).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(HP.formatDecimal(item.margin)).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(HP.formatDecimal(item.profit)).setFontSize(REPORT_BODY_FONT_SIZE))
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
        Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
            .useAllAvailableWidth()
    val totalBillsCell = Cell()
        .add(
            Paragraph()
                .add(Text("Total Bills: ").setFont(boldFont))
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(100f))
                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph(totalReport.totalBills.toString())
                                .setTextAlignment(TextAlignment.CENTER)
                        )
                )
        )
        .setFontSize(12f)
        .setBorder(null)
        .setTextAlignment(TextAlignment.LEFT)

    val totalCell = Cell()
        .add(
            Paragraph()
                .add(Text("Total Sales: ").setFont(boldFont))
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(100f))
//                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph(HP.formatDecimal(totalReport.grandTotal))
                                .setTextAlignment(TextAlignment.RIGHT)
                        )
                )
        )
        .setFontSize(REPORT_HEADINGS_FONT_SIZE)
        .setBorder(null)
        .setTextAlignment(TextAlignment.RIGHT)

    row1.addCell(totalBillsCell)
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
                .add(Text("Total Cost: ").setFont(boldFont))
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(100f))
//                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph(HP.formatDecimal(totalReport.totalCost))
                                .setTextAlignment(TextAlignment.RIGHT)
                        )
                )
        )
        .setFontSize(REPORT_HEADINGS_FONT_SIZE)
        .setBorder(null)
        .setTextAlignment(TextAlignment.RIGHT)

    row2.addCell(discTableCell)
    document.add(row2)

    // ---------------- Row3 ----------------
    val row3 =
        Table(UnitValue.createPercentArray(floatArrayOf(100f)))
            .useAllAvailableWidth()
            .setMarginTop(-5f)
    val marginTableCell = Cell()
        .add(
            Paragraph()
                .add(Text("Margin: ").setFont(boldFont))
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(100f))
                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph("${HP.formatDecimal(totalReport.totalMargin)}%")
                                .setTextAlignment(TextAlignment.RIGHT)
                        )
                )
        )
        .setFontSize(REPORT_HEADINGS_FONT_SIZE)
        .setBorder(null)
        .setTextAlignment(TextAlignment.RIGHT)

    row3.addCell(marginTableCell)
    document.add(row3)

    // ---------------- Row4 ----------------
    val row4 =
        Table(UnitValue.createPercentArray(floatArrayOf(100f)))
            .useAllAvailableWidth()
            .setMarginTop(-5f)
    val totalProfitTableCell = Cell()
        .add(
            Paragraph()
                .add(Text("Total Profit: ").setFont(boldFont))
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(100f))
                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph(HP.formatDecimal(totalReport.totalProfit))
                                .setTextAlignment(TextAlignment.RIGHT)
                        )
                )
        )
        .setFontSize(REPORT_HEADINGS_FONT_SIZE)
        .setBorder(null)
        .setTextAlignment(TextAlignment.RIGHT)

    row4.addCell(totalProfitTableCell)
    document.add(row4)
    // endregion

    pageHandler.writeTotal(pdf)
    document.close()
    return file
}