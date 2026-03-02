package com.example.statspos.presentation.ui.screens.reports.sales

import android.content.Context
import com.example.statspos.domain.models.reports.TotalReport
import com.example.statspos.domain.models.reports.sales.SalesItemsReport
import com.example.statspos.presentation.ui.utils.REPORT_BODY_FONT_SIZE
import com.example.statspos.presentation.ui.utils.REPORT_HEADER_FONT_SIZE
import com.example.statspos.presentation.ui.utils.REPORT_HEADINGS_FONT_SIZE
import com.example.statspos.utils.HP
import com.itextpdf.kernel.colors.ColorConstants
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

fun salesItemsReport(
    context: Context,
    fromDate: String,
    toDate: String,
    itemsReport: List<SalesItemsReport>,
    totalReport: TotalReport,
): File {
    // region Document
    val file = File(context.cacheDir, "Sales_Report_${System.currentTimeMillis()}.pdf")
    if (file.exists()) {
        file.delete()
    }

    val writer = PdfWriter(file)
    val pdf = PdfDocument(writer)
    val document = Document(pdf)

    document.setMargins(20f, 20f, 20f, 20f)
    // endregion

    // region Header
    // ---------------- Report Title ----------------
    val title = Paragraph("Sales Report")
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
    val columnWidths = if (HP.settings.saleCartons == true)
        floatArrayOf(1f, 0.5f, 2f, 2f, 0.5f, 0.5f, 0.5f, 0.5f, 1f)
    else
        floatArrayOf(1f, 0.5f, 2f, 2f, 0.5f, 0.5f, 0.5f, 1f)

    val bodyTable = Table(UnitValue.createPercentArray(columnWidths))
    bodyTable.setWidth(UnitValue.createPercentValue(100f))

    val headers = if (HP.settings.saleCartons == true)
        listOf(
            "Date",
            "Sr.",
            "Customer",
            "Itemname",
            "Qty",
            "Crtn",
            "Rate",
            "C.Rate",
            "Total",
        )
    else
        listOf(
            "Date",
            "Sr.",
            "Customer",
            "Itemname",
            "Qty",
            "Rate",
            "Disc",
            "Total",
        )

    headers.forEachIndexed { index, item ->
        val cell = Cell().add(
            Paragraph(item)
                .setBold()
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

    itemsReport.forEach { item ->
        bodyTable.addCell(
            Cell().add(Paragraph(item.date.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(item.salesId.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(item.customerName.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.LEFT)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(item.itemname.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.LEFT)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(item.qty.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        if (HP.settings.saleCartons == true) {
            bodyTable.addCell(
                Cell().add(Paragraph(item.crtn.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        bodyTable.addCell(
            Cell().add(Paragraph(item.rate.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        if (HP.settings.saleCartons == true) {
            bodyTable.addCell(
                Cell().add(Paragraph(item.crtnRate.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        if (HP.settings.saleCartons == false) {
            bodyTable.addCell(
                Cell().add(Paragraph(item.totalDisc.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        bodyTable.addCell(
            Cell().add(Paragraph(item.total.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )
    }

    document.add(bodyTable)
//    document.add(Paragraph("\n"))
    // endregion

    // region Footer
    // ---------------- Row1 ----------------
    val row1 =
        Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .useAllAvailableWidth()
    val totalQtyCell = Cell()
        .add(
            Paragraph()
                .add(Text("Total Qty: ").setBold())
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(100f))
                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph(totalReport.totalQty.toString())
                                .setTextAlignment(TextAlignment.CENTER)
                        )
                )
                .add(Text("Total Crtn: ").setBold())
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(100f))
                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph(totalReport.totalCrtn.toString())
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
                .add(Text("Total: ").setBold())
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(100f))
//                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph(totalReport.total.toString())
                                .setTextAlignment(TextAlignment.RIGHT)
                        )
                )
        )
        .setFontSize(REPORT_HEADINGS_FONT_SIZE)
        .setBorder(null)
        .setTextAlignment(TextAlignment.RIGHT)

    row1.addCell(totalQtyCell)
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
                .add(Text("Discount: ").setBold())
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(100f))
                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph(totalReport.totalDisc.toString())
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
    val grandTotalTableCell = Cell()
        .add(
            Paragraph()
                .add(Text("Grand Total: ").setBold())
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(100f))
                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph(totalReport.grandTotal.toString())
                                .setTextAlignment(TextAlignment.RIGHT)
                        )
                )
        )
        .setFontSize(REPORT_HEADINGS_FONT_SIZE)
        .setBorder(null)
        .setTextAlignment(TextAlignment.RIGHT)

    row3.addCell(grandTotalTableCell)
    document.add(row3)
    // endregion

    document.close()
    return file
}