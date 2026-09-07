package com.graphees.statspos.presentation.ui.screens.sales.invoices

import android.content.Context
import com.graphees.statspos.domain.models.reports.accounts.AccountReport
import com.graphees.statspos.domain.models.sales.SalesBill
import com.graphees.statspos.presentation.ui.utils.REPORT_HEADER_FONT_SIZE
import com.graphees.statspos.presentation.ui.utils.REPORT_HEADINGS_FONT_SIZE
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.getDefaultImageCell
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
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import kotlin.math.abs

fun salesBillThermal(
    context: Context,
    bill: List<SalesBill>,
    ledger: List<AccountReport>?,
): File {
    // region Document
    val file = File(context.cacheDir, "Sales_Bill_${System.currentTimeMillis()}.pdf")
    if (file.exists()) {
        file.delete()
    }

    val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
    val topFontSize = 9f
    val detailsFontSize = 8f

    val writer = PdfWriter(file)
    val pdf = PdfDocument(writer)
    val pageWidth = 226.77f
    val pageHeight = 3200f
    val pageSize = PageSize(pageWidth, pageHeight)
    val document = Document(pdf, pageSize)
    document.setMargins(3f, 3f, 3f, 3f)

    // endregion

    // region Header
    // ---------------- Top ----------------
    val shopTable =
        Table(UnitValue.createPercentArray(floatArrayOf(100f))).useAllAvailableWidth()

    if (HP.printSettings.showLogo == true) {
        val imageCell = getDefaultImageCell(
            imageUrl = HP.getImageUrl(HP.printSettings.imageUrl.toString()),
            horizontalAlignment = HorizontalAlignment.CENTER,
            width = 80f,
            height = 80f,
        )
        imageCell.setBorder(null)
        shopTable.addCell(imageCell)
    }

    shopTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(
                        Text(HP.printSettings.shopName).setFont(boldFont).setFontSize(12f)
                            .setTextAlignment(TextAlignment.CENTER)
                    )
                    .add(
                        Text("\n${HP.printSettings.address}").setFontSize(10f)
                            .setTextAlignment(TextAlignment.CENTER)
                    )
                    .add(
                        Text("\n${HP.printSettings.contact}").setFontSize(10f)
                            .setTextAlignment(TextAlignment.CENTER)
                    )
            )
            .setFontSize(REPORT_HEADINGS_FONT_SIZE)
            .setBorder(null)
            .setTextAlignment(TextAlignment.CENTER)
    )

    document.add(shopTable)

    // ---------------- Customer & Invoice ----------------
    val customerTable =
        Table(UnitValue.createPercentArray(floatArrayOf(100f))).useAllAvailableWidth()
    if (bill[0].billType.equals("pending bill", ignoreCase = true)) {
        customerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                        .add(Text(bill[0].billType)).setFont(boldFont)
//                    .setMarginTop(-5f)
                )
                .setFontSize(topFontSize)
                .setBorder(null)
                .setTextAlignment(TextAlignment.CENTER)
        )
    }
    customerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text(bill[0].customerName))
                    .setMarginTop(-5f)
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.CENTER)
    )
    document.add(customerTable)

    // Date
    val dateTable =
        Table(UnitValue.createPercentArray(floatArrayOf(40f, 20f, 40f))).useAllAvailableWidth()

    dateTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Date: ").setFont(boldFont))
                    .add(Text(bill[0].date))
//                    .setMarginTop(-5f)
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    dateTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("").setFont(boldFont))
//                    .setMarginTop(-5f)
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    dateTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("User: ").setFont(boldFont))
                    .add(Text(bill[0].user))
//                    .setMarginTop(-5f)
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.RIGHT)
    )

    dateTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Time: ").setFont(boldFont))
                    .add(Text(bill[0].time))
                    .setMarginTop(-5f)
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    dateTable.addCell(
        Cell()
            .add(
                Paragraph()
//                    .add(Text("").setFont(boldFont))
                    .add(Text(bill[0].salesOn))
                    .setMarginTop(-5f)
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.CENTER)
    )

    dateTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Sr. ").setFont(boldFont))
                    .add(Text(bill[0].id.toString()))
                    .setMarginTop(-5f)
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.RIGHT)
    )

    document.add(dateTable)

    // endregion

    // region Details
    // ---------------- Table ----------------
    val columnWidths = if (HP.settings.saleCartons == true)
        floatArrayOf(3f, 1f, 1f, 1.5f, 1.5f, 2f)
    else
        floatArrayOf(3f, 1f, 1.5f, 1f, 2f)

    val bodyTable = Table(UnitValue.createPercentArray(columnWidths), true)
    bodyTable.setWidth(UnitValue.createPercentValue(100f))
    document.add(bodyTable)

    val headers = if (HP.settings.saleCartons == true)
        listOf(
            "Item Name",
            "Qty",
            "Crtn",
            "Rate",
            "C.Rate",
            "Total",
        )
    else
        listOf(
            "Item Name",
            "Qty",
            "Rate",
            "Disc",
            "Total",
        )

    headers.forEachIndexed { index, item ->
        val cell = Cell().add(
            Paragraph(item)
                .setFont(boldFont)
                .setFontSize(detailsFontSize)
        )

        cell.setTextAlignment(TextAlignment.CENTER)
        bodyTable.addHeaderCell(cell)
    }

    var counter = 0
    bill.forEach { item ->
        bodyTable.addCell(
            Cell().add(Paragraph(item.itemname).setFontSize(detailsFontSize))
                .setTextAlignment(TextAlignment.LEFT)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(HP.formatDecimal(item.qty)).setFontSize(detailsFontSize))
                .setTextAlignment(TextAlignment.CENTER)
        )

        if (HP.settings.saleCartons == true) {
            bodyTable.addCell(
                Cell().add(Paragraph(item.crtn.toString()).setFontSize(detailsFontSize))
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        bodyTable.addCell(
            Cell().add(
                Paragraph(if (item.qty!! == 0.0) "0" else HP.formatDecimal(item.rate)).setFontSize(
                    detailsFontSize
                )
            )
                .setTextAlignment(TextAlignment.CENTER)
        )

        if (HP.settings.saleCartons == true) {
            bodyTable.addCell(
                Cell().add(
                    Paragraph(if (item.crtn!! == 0) "0" else HP.formatDecimal(item.crtnRate)).setFontSize(
                        detailsFontSize
                    )
                )
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        if (HP.settings.saleCartons == false) {
            bodyTable.addCell(
                Cell().add(
                    Paragraph(HP.formatDecimal(item.totalDisc)).setFontSize(detailsFontSize)
                )
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        bodyTable.addCell(
            Cell().add(Paragraph(HP.formatDecimal(item.total)).setFontSize(detailsFontSize))
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
    val line = 70f
    val margin = -7f
    val footerTable =
        Table(UnitValue.createPercentArray(floatArrayOf(30f, 70f))).useAllAvailableWidth()

    footerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Items: ").setFont(boldFont))
                    .add(Text(counter.toString()))
//                    .setMarginTop(-5f)
            )
            .setFontSize(detailsFontSize)
            .setTextAlignment(TextAlignment.LEFT)
    )
    footerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Sub Total: ").setFont(boldFont))
                    .add(
                        Div()
                            .setWidth(UnitValue.createPointValue(line))
                            .add(
                                Paragraph(HP.formatDecimal((bill[0].grandTotal!! + bill[0].grandTotalDisc!!)))
                                    .setTextAlignment(TextAlignment.RIGHT)
                            )
                    )
//                    .add(Text(HP.formatDecimal(bill[0].grandTotalDisc)))
                    .setMarginTop(-5f)
            )
            .setFontSize(detailsFontSize)
            .setBorderBottom(SolidBorder(ColorConstants.WHITE, 0f))
            .setTextAlignment(TextAlignment.RIGHT)
    )
    footerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text(""))
//                    .setMarginTop(-5f)
            )
            .setFontSize(detailsFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.RIGHT)
    )
    footerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Discount: ").setFont(boldFont))
                    .add(
                        Div()
                            .setWidth(UnitValue.createPointValue(line))
                            .setBorderBottom(SolidBorder(ColorConstants.BLACK, 0.5f))
                            .add(
                                Paragraph(HP.formatDecimal(bill[0].grandTotalDisc))
                                    .setTextAlignment(TextAlignment.RIGHT)
                            )
                    )
                    .setMarginTop(margin)
            )
            .setFontSize(detailsFontSize)
            .setBorderTop(SolidBorder(ColorConstants.WHITE, 0f))
            .setBorderBottom(SolidBorder(ColorConstants.WHITE, 0f))
            .setTextAlignment(TextAlignment.RIGHT)
    )
    footerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text(""))
//                    .setMarginTop(margin)
            )
            .setFontSize(detailsFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.RIGHT)
    )
    footerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Grand Total: ").setFont(boldFont))
                    .add(
                        Div()
                            .setWidth(UnitValue.createPointValue(line))
//                            .setBorderBottom(
//                                SolidBorder(
//                                    if (bill[0].salesOn == "Credit") ColorConstants.WHITE else ColorConstants.BLACK,
//                                    if (bill[0].salesOn == "Credit") 0f else 1f
//                                )
//                            )
                            .add(
                                Paragraph(HP.formatDecimal(bill[0].grandTotal))
                                    .setTextAlignment(TextAlignment.RIGHT)
                            )
                    )
                    .setMarginTop(margin)
            )
            .setFontSize(detailsFontSize)
            .setBorderTop(SolidBorder(ColorConstants.WHITE, 0f))
            .setBorderBottom(SolidBorder(ColorConstants.WHITE, 0f))
//            .setBorder(null)
            .setTextAlignment(TextAlignment.RIGHT)
    )


    if (bill[0].salesOn == "Cash") {
        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                        .add(Text(""))
//                    .setMarginTop(margin)
                )
                .setFontSize(detailsFontSize)
                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT)
        )
        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                        .add(Text("Payment: ").setFont(boldFont))
                        .add(
                            Div()
                                .setWidth(UnitValue.createPointValue(line))
//                            .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                                .add(
                                    Paragraph(bill[0].payment.toString())
                                        .setTextAlignment(TextAlignment.RIGHT)
                                )
                        )
                        .setMarginTop(margin)
                )
                .setFontSize(detailsFontSize)
                .setBorderTop(SolidBorder(ColorConstants.WHITE, 0f))
                .setBorderBottom(SolidBorder(ColorConstants.WHITE, 0f))
//                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT)
        )

        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                        .add(Text(""))
//                    .setMarginTop(margin)
                )
                .setFontSize(detailsFontSize)
                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT)
        )
        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                        .add(Text("Change: ").setFont(boldFont))
                        .add(
                            Div()
                                .setWidth(UnitValue.createPointValue(line))
//                            .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                                .add(
                                    Paragraph(bill[0].change.toString())
                                        .setTextAlignment(TextAlignment.RIGHT)
                                )
                        )
                        .setMarginTop(margin)
                )
                .setFontSize(detailsFontSize)
                .setBorderTop(SolidBorder(ColorConstants.WHITE, 0f))
//                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT)
        )
    } else {
        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                )
                .setFontSize(detailsFontSize)
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT)
        )

        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                        .add(Text("Old Balance: ").setFont(boldFont))
                        .add(
                            Div()
                                .setWidth(UnitValue.createPointValue(line))
                                .setBorderBottom(SolidBorder(ColorConstants.BLACK, 0.5f))
                                .add(
                                    Paragraph("${HP.formatDecimal(abs(bill[0].oldBalance!!))} ${if (bill[0].oldBalance!! > 0) "R" else "P"}")
                                        .setTextAlignment(TextAlignment.RIGHT)
                                )
                        )
                        .setMarginTop(margin)
                )
                .setFontSize(detailsFontSize)
                .setBorderTop(SolidBorder(ColorConstants.WHITE, 0f))
                .setBorderBottom(SolidBorder(ColorConstants.WHITE, 0f))
//                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT)
        )

        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                )
                .setFontSize(detailsFontSize)
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT)
        )

        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                        .add(Text("Total: ").setFont(boldFont))
                        .add(
                            Div()
                                .setWidth(UnitValue.createPointValue(line))
//                            .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                                .add(
                                    Paragraph(HP.formatDecimal(abs(bill[0].grandTotal!! + bill[0].oldBalance!!)))
                                        .setTextAlignment(TextAlignment.RIGHT)
                                )
                        )
                        .setMarginTop(margin)
                )
                .setFontSize(detailsFontSize)
                .setBorderTop(SolidBorder(ColorConstants.WHITE, 0f))
                .setBorderBottom(SolidBorder(ColorConstants.WHITE, 0f))
//                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT)
        )

        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                )
                .setFontSize(detailsFontSize)
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT)
        )

        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                        .add(Text("Payment: ").setFont(boldFont))
                        .add(
                            Div()
                                .setWidth(UnitValue.createPointValue(line))
                                .setBorderBottom(SolidBorder(ColorConstants.BLACK, 0.5f))
                                .add(
                                    Paragraph(bill[0].payment.toString())
                                        .setTextAlignment(TextAlignment.RIGHT)
                                )
                        )
                        .setMarginTop(margin)
                )
                .setFontSize(detailsFontSize)
                .setBorderTop(SolidBorder(ColorConstants.WHITE, 0f))
                .setBorderBottom(SolidBorder(ColorConstants.WHITE, 0f))
//                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT)
        )

        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                )
                .setFontSize(detailsFontSize)
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT)
        )

        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                        .add(Text("New Balance: ").setFont(boldFont))
                        .add(
                            Div()
                                .setWidth(UnitValue.createPointValue(line))
                                .setBorderBottom(SolidBorder(ColorConstants.BLACK, 0.5f))
                                .add(
                                    Paragraph("${HP.formatDecimal(abs(bill[0].newBalance!!))} ${if (bill[0].newBalance!! > 0) "R" else "P"}")
                                        .setTextAlignment(TextAlignment.RIGHT)
                                )
                        )
                        .setMarginTop(margin)
                )
                .setFontSize(detailsFontSize)
                .setBorderTop(SolidBorder(ColorConstants.WHITE, 0f))
//                .setBorderBottom(SolidBorder(ColorConstants.WHITE,0f))
//                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT)
        )
    }

    document.add(footerTable)

    // endregion

    // region Ledger
    if (HP.settings.showLedgerInBill == true) {
        document.add(Paragraph("\n"))

        document.add(
            Paragraph("Last Five Entries")
                .setFont(boldFont)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
        )

        // ---------------- Table ----------------
        val columnWidths = floatArrayOf(0.7f, 3f, 1f, 1f, 1f)

        val bodyTable = Table(UnitValue.createPercentArray(columnWidths), true)
        bodyTable.setWidth(UnitValue.createPercentValue(100f))
        document.add(bodyTable)

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
                    .setFont(boldFont)
                    .setFontSize(REPORT_HEADER_FONT_SIZE)
            )

            cell.setTextAlignment(TextAlignment.CENTER)
            bodyTable.addHeaderCell(cell)
        }

        var counter = 0

        ledger?.forEach { item ->
            bodyTable.addCell(
                Cell().add(Paragraph(item.date.toString()).setFontSize(detailsFontSize))
                    .setTextAlignment(TextAlignment.CENTER)
            )

            bodyTable.addCell(
                Cell().add(Paragraph(item.naration.toString()).setFontSize(detailsFontSize))
                    .setTextAlignment(TextAlignment.LEFT)
            )

            bodyTable.addCell(
                Cell().add(Paragraph(HP.formatDecimal(item.debit)).setFontSize(detailsFontSize))
                    .setTextAlignment(TextAlignment.CENTER)
            )

            bodyTable.addCell(
                Cell().add(
                    Paragraph(HP.formatDecimal(item.credit)).setFontSize(detailsFontSize)
                )
                    .setTextAlignment(TextAlignment.CENTER)
            )

            bodyTable.addCell(
                Cell().add(
                    Paragraph(HP.formatDecimal(abs(item.balance!!))).setFontSize(detailsFontSize)
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