package com.graphees.statspos.presentation.ui.screens.reports.accounts

import android.content.Context
import com.graphees.statspos.domain.models.reports.TotalReport
import com.graphees.statspos.domain.models.reports.accounts.AccountReport
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
import kotlin.math.abs

fun vendorsReport(
    context: Context,
    accountReport: List<AccountReport>,
    totalReport: TotalReport,
): File {
    // region Document
    val file = File(context.cacheDir, "Vendors_Report_${System.currentTimeMillis()}.pdf")
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
    val title = Paragraph("Vendors")
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
//    document.add(Paragraph("\n"))
    // endregion

    // region Details
    // ---------------- Table ----------------
    val columnWidths = floatArrayOf(3f, 1f, 1f)

    val bodyTable = Table(UnitValue.createPercentArray(columnWidths), true)
    bodyTable.setWidth(UnitValue.createPercentValue(100f))
    document.add(bodyTable)

    val headers = listOf(
        "Name",
        "Contact",
        "Balance",
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


    var counter = 0
    accountReport.forEach { item ->
        bodyTable.addCell(
            Cell().add(Paragraph(item.accountName.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.LEFT)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(item.contact).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        bodyTable.addCell(
            Cell().add(Paragraph("${HP.formatDecimal(abs(item.balance!!))} ${if(item.balance!! > 0) "R" else "P"}").setFontSize(REPORT_BODY_FONT_SIZE))
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
        .add(Text("Total Vendors: ").setFont(boldFont))
        .add(
            Div()
                .setWidth(UnitValue.createPointValue(100f))
                .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                .add(
                    Paragraph(counter.toString())
                        .setTextAlignment(TextAlignment.CENTER)
                )
        )

    val totalQtyCell = Cell()
        .add(paragraph)
        .setFontSize(12f)
        .setBorder(null)
        .setTextAlignment(TextAlignment.LEFT)

    val totalCell = Cell()
        .add(
            Paragraph()
                .add(Text("Total: ").setFont(boldFont))
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(120f))
                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph("${HP.formatDecimal(abs(totalReport.grandTotal!!))} ${if(totalReport.grandTotal!! > 0) "R" else "P"}")
                                .setTextAlignment(TextAlignment.CENTER)
                        )
                )
        )
        .setFontSize(REPORT_HEADINGS_FONT_SIZE)
        .setBorder(null)
        .setTextAlignment(TextAlignment.RIGHT)

    row1.addCell(totalQtyCell)
    row1.addCell(totalCell)
    document.add(row1)
    // endregion

    pageHandler.writeTotal(pdf)
    document.close()
    return file
}