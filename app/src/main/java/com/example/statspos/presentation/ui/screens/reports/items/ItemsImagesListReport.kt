package com.example.statspos.presentation.ui.screens.reports.items

import android.content.Context
import android.util.Log
import com.example.statspos.domain.models.reports.TotalReport
import com.example.statspos.domain.models.reports.items.ItemsReport
import com.example.statspos.presentation.ui.components.PageXofYEventHandler
import com.example.statspos.presentation.ui.utils.REPORT_BODY_FONT_SIZE
import com.example.statspos.presentation.ui.utils.REPORT_HEADER_FONT_SIZE
import com.example.statspos.presentation.ui.utils.REPORT_HEADINGS_FONT_SIZE
import com.example.statspos.utils.HP
import com.example.statspos.utils.getDefaultImageCell
import com.example.statspos.utils.urduTextToPdfImage
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.font.PdfFont
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
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.time.LocalDate


fun itemsImagesListReport(
    context: Context,
    itemsReport: List<ItemsReport>,
    totalReport: TotalReport,
): File {
    // region Document
    val file = File(context.cacheDir, "Items_List_${System.currentTimeMillis()}.pdf")
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
    val title = Paragraph("Items List")
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
                .add(Text("Date: ").setBold())
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
    val columnWidths = floatArrayOf(3f, 1f, 2f)

    val bodyTable = Table(UnitValue.createPercentArray(columnWidths), true)
    bodyTable.setWidth(UnitValue.createPercentValue(100f))
    document.add(bodyTable)

    val headers = listOf(
        "Itemname",
        "Rate",
        "Image",
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

    var counter = 0
    itemsReport.forEach { item ->
        // Itemname & Urduname
//        val image = urduTextToPdfImage(context, item.urduname.toString())
//        image.setHorizontalAlignment(HorizontalAlignment.RIGHT)
//
//        val innerTable = Table(floatArrayOf(1f, 1f))
//        innerTable.setWidth(UnitValue.createPercentValue(100f))
//
//        innerTable.addCell(
//            Cell().add(Paragraph(item.itemname.toString()))
//                .setTextAlignment(TextAlignment.LEFT)
//                .setBorder(null)
//        )
//
//        innerTable.addCell(
//            Cell().add(image)
//                .setTextAlignment(TextAlignment.RIGHT)
//                .setBorder(null)
//        )
//
//        bodyTable.addCell(
//            Cell().add(innerTable).setFontSize(REPORT_BODY_FONT_SIZE)
//        )


        // Itemname
        bodyTable.addCell(
            Cell().add(
                Paragraph(item.itemname)
                    .setFontSize(REPORT_BODY_FONT_SIZE)
            ).setTextAlignment(TextAlignment.LEFT)
        )

        // Retail
        bodyTable.addCell(
            Cell().add(Paragraph(HP.formatDecimal(item.retail)).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        // Image
        val imageCell = getDefaultImageCell(item.imageUrl.toString())
        bodyTable.addCell(imageCell)


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
        .add(Text("Total Items: ").setBold())
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
