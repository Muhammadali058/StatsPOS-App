package com.example.statspos.presentation.ui.screens.purchase.main_screen

import android.content.Context
import com.example.statspos.domain.models.purchase.PurchaseBill
import com.example.statspos.domain.models.purchase.PurchaseOrderVoucher
import com.example.statspos.domain.models.reports.TotalReport
import com.example.statspos.domain.models.reports.accounts.AccountReport
import com.example.statspos.domain.models.reports.purchase.PurchaseItemsReport
import com.example.statspos.domain.models.reports.sales.SalesItemsReport
import com.example.statspos.domain.models.reports.stock.StockItemsReport
import com.example.statspos.domain.models.sales.SalesBill
import com.example.statspos.presentation.ui.components.PageXofYEventHandler
import com.example.statspos.presentation.ui.utils.REPORT_BODY_FONT_SIZE
import com.example.statspos.presentation.ui.utils.REPORT_HEADER_FONT_SIZE
import com.example.statspos.presentation.ui.utils.REPORT_HEADINGS_FONT_SIZE
import com.example.statspos.utils.HP
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
import java.time.LocalDate

fun purchaseBillVoucher(
    context: Context,
    bill: List<PurchaseBill>,
): File {
    // region Document
    val file = File(context.cacheDir, "Purchase_Bill_${System.currentTimeMillis()}.pdf")
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
    val title = Paragraph("Purchase Bill")
        .setBold()
        .setFontSize(24f)
        .setTextAlignment(TextAlignment.CENTER)

    document.add(title)

//    document.add(
//        Paragraph(purchaseOrder[0].purchaseOrderName)
//            .setBold()
//            .setFontSize(14f)
//            .setTextAlignment(TextAlignment.CENTER)
//    )

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
    val columnWidths = if (HP.settings.saleCartons == true)
        floatArrayOf(3f, 1f, 1f, 1f, 1f, 1f)
    else
        floatArrayOf(3f, 1f, 1f, 1f, 1f)

    val bodyTable = Table(UnitValue.createPercentArray(columnWidths), true)
    bodyTable.setWidth(UnitValue.createPercentValue(100f))
    document.add(bodyTable)

    val headers = if (HP.settings.saleCartons == true)
        listOf(
            "Itemname",
            "Qty",
            "Crtn",
            "Rate",
            "C.Rate",
            "Total",
        )
    else
        listOf(
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

        cell.setTextAlignment(TextAlignment.CENTER)
        bodyTable.addHeaderCell(cell)
    }

    var counter = 0
    bill.forEach { item ->
        bodyTable.addCell(
            Cell().add(Paragraph(item.itemname.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.LEFT)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(HP.formatDecimal(item.qty)).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        if (HP.settings.saleCartons == true) {
            bodyTable.addCell(
                Cell().add(Paragraph(item.crtn.toString()).setFontSize(REPORT_BODY_FONT_SIZE))
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        bodyTable.addCell(
            Cell().add(Paragraph(HP.formatDecimal(item.rate)).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        if (HP.settings.saleCartons == true) {
            bodyTable.addCell(
                Cell().add(Paragraph(HP.formatDecimal(item.crtnRate)).setFontSize(REPORT_BODY_FONT_SIZE))
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        bodyTable.addCell(
            Cell().add(Paragraph(HP.formatDecimal(item.total)).setFontSize(REPORT_BODY_FONT_SIZE))
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

    val paragraph = Paragraph()
        .add(Text("Total Items: ").setBold())
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
                .add(Text("Grand Total: ").setBold())
                .add(
                    Div()
                        .setWidth(UnitValue.createPointValue(100f))
                        .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                        .add(
                            Paragraph(HP.formatDecimal(bill[0].grandTotal))
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

    document.add(
        Paragraph("Remarks: ")
            .setBold()
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.LEFT)
    )

    // endregion

    pageHandler.writeTotal(pdf)
    document.close()
    return file
}