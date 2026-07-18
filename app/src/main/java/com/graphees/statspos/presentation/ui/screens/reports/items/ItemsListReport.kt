package com.graphees.statspos.presentation.ui.screens.reports.items

import android.content.Context
import com.graphees.statspos.domain.models.reports.TotalReport
import com.graphees.statspos.domain.models.reports.items.ItemsReport
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
import java.time.LocalDate

fun itemsListReport(
    context: Context,
    itemsReport: List<ItemsReport>,
    totalReport: TotalReport,
): File {
    // region Document
    val file = File(context.cacheDir, "Items_List_${System.currentTimeMillis()}.pdf")
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
    val title = Paragraph("Items List")
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
                .add(Text("Date: ").setFont(boldFont))
                .add(Text(HP.getFormatedDate(LocalDate.now())))
        )
        .setFontSize(REPORT_HEADINGS_FONT_SIZE)
        .setBorder(null)
        .setTextAlignment(TextAlignment.LEFT)

    dateTable.addCell(fromDateCell)
    document.add(dateTable)
    // endregion

    // region Details
    // ---------------- Table ----------------
    val columnWidths = if (HP.settings.saleCartons == true)
        floatArrayOf(1f, 2f, 0.5f, 0.5f, 0.5f, 0.5f, 1f)
    else
        floatArrayOf(1f, 2f, 0.5f, 0.5f, 0.5f, 1f)

    val bodyTable = Table(UnitValue.createPercentArray(columnWidths), true)
    bodyTable.setWidth(UnitValue.createPercentValue(100f))
    document.add(bodyTable)

    val headers = if (HP.settings.saleCartons == true)
        listOf(
            "Barcode",
            "Itemname",
            "Cost",
            "Retail",
            "W.Sale",
            "C.Rate",
            "Ref. Code",
        )
    else
        listOf(
            "Barcode",
            "Itemname",
            "Cost",
            "Retail",
            "W.Sale",
            "Ref. Code",
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
    itemsReport.forEach { item ->
        bodyTable.addCell(
            Cell().add(Paragraph(item.barcode.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(item.itemname.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.LEFT)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(HP.formatDecimal(item.cost)).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(HP.formatDecimal(item.retail)).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(HP.formatDecimal(item.wholesale)).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        if (HP.settings.saleCartons == true) {
            bodyTable.addCell(
                Cell().add(Paragraph(HP.formatDecimal(item.crtnRate)).setFontSize(REPORT_BODY_FONT_SIZE))
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        bodyTable.addCell(
            Cell().add(Paragraph(item.refCode.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
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
        Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .useAllAvailableWidth()

    val paragraph = Paragraph()
        .add(Text("Total Items: ").setFont(boldFont))
        .add(
            Div()
                .setWidth(UnitValue.createPointValue(100f))
                .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                .add(
                    Paragraph(totalReport.totalItems.toString())
                        .setTextAlignment(TextAlignment.CENTER)
                )
        )

    val totalItemsCell = Cell()
        .add(paragraph)
        .setFontSize(12f)
        .setBorder(null)
        .setTextAlignment(TextAlignment.LEFT)

    row1.addCell(totalItemsCell)
    document.add(row1)
    // endregion

    pageHandler.writeTotal(pdf)
    document.close()
    return file
}