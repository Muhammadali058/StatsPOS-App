package com.example.statspos.presentation.ui.screens.purchase.main_screen

import android.content.Context
import com.example.statspos.domain.models.purchase.PurchaseBill
import com.example.statspos.presentation.ui.components.PageXofYEventHandler
import com.example.statspos.presentation.ui.utils.REPORT_BODY_FONT_SIZE
import com.example.statspos.presentation.ui.utils.REPORT_HEADER_FONT_SIZE
import com.example.statspos.presentation.ui.utils.REPORT_HEADINGS_FONT_SIZE
import com.example.statspos.utils.HP
import com.example.statspos.utils.getDefaultImageCell
import com.example.statspos.utils.urduTextToPdfImage
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
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import kotlin.math.abs

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
    // ---------------- Top ----------------
    val shopTable =
        Table(
            UnitValue.createPercentArray(
                if (HP.printSettings.showLogo == true) floatArrayOf(
                    20f,
                    80f
                ) else floatArrayOf(100f)
            )
        ).useAllAvailableWidth()

    if (HP.printSettings.showLogo == true) {
        val imageCell = getDefaultImageCell(
            imageUrl = HP.getImageUrl(HP.printSettings.imageUrl.toString()),
            horizontalAlignment = HorizontalAlignment.LEFT,
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
                        Text(HP.printSettings.shopName).setBold().setFontSize(20f)
                            .setTextAlignment(TextAlignment.CENTER)
                    )
                    .add(
                        Text("\n${HP.printSettings.address}").setFontSize(12f)
                            .setTextAlignment(TextAlignment.CENTER)
                    )
                    .add(
                        Text("\n${HP.printSettings.contact}").setFontSize(12f)
                            .setTextAlignment(TextAlignment.CENTER)
                    )
            )
            .setFontSize(REPORT_HEADINGS_FONT_SIZE)
            .setBorder(null)
            .setTextAlignment(TextAlignment.CENTER)
    )

    document.add(shopTable)

    document.add(
        Paragraph("Purchase Invoice")
            .setBold()
            .setFontSize(14f)
            .setTextAlignment(TextAlignment.CENTER)
    )

    // Horizontal Line
    document.add(
        Paragraph()
            .add(
                Div()
                    .setWidth(UnitValue.createPointValue(600f))
                    .setBorderTop(SolidBorder(ColorConstants.BLACK, 1f))
            )
            .setFontSize(12f)
            .setBorder(null)
//            .setMarginTop(10f)
            .setTextAlignment(TextAlignment.CENTER)
    )

    // ---------------- Customer & Invoice ----------------
    val customerTable =
        Table(UnitValue.createPercentArray(floatArrayOf(70f, 30f))).useAllAvailableWidth()

    customerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Vendor: ").setBold())
                    .add(Text(bill[0].vendorName))
//                    .setMarginTop(-5f)
            )
            .setFontSize(REPORT_HEADINGS_FONT_SIZE)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    customerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Invoice #: ").setBold())
                    .add(Text(bill[0].id.toString()))
//                    .setMarginTop(-5f)
            )
            .setFontSize(REPORT_HEADINGS_FONT_SIZE)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    customerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Contact: ").setBold())
                    .add(Text(bill[0].vendorContact))
//                    .setMarginTop(-5f)
            )
            .setFontSize(REPORT_HEADINGS_FONT_SIZE)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    customerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("User: ").setBold())
                    .add(Text(bill[0].user))
//                    .setMarginTop(-5f)
            )
            .setFontSize(REPORT_HEADINGS_FONT_SIZE)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    customerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Address: ").setBold())
                    .add(Text(bill[0].vendorAddress))
//                    .setMarginTop(-5f)
            )
            .setFontSize(REPORT_HEADINGS_FONT_SIZE)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    customerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Date: ").setBold())
                    .add(Text("${bill[0].date}, ${bill[0].time}"))
//                    .setMarginTop(-5f)
            )
            .setFontSize(REPORT_HEADINGS_FONT_SIZE)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    document.add(customerTable)

    // Bill Type
    if (bill[0].billType.equals("pending bill", ignoreCase = true)) {
        document.add(
            Paragraph(bill[0].billType)
                .setBold()
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
        )
    }

    // endregion

    // region Details
    // ---------------- Table ----------------
    val columnWidths = if (HP.settings.saleCartons == true)
        floatArrayOf(0.5f, 3f, 0.7f, 0.7f, 0.8f, 0.8f, 1f)
    else
        floatArrayOf(0.5f, 3f, 0.7f, 0.8f, 1f)

    val bodyTable = Table(UnitValue.createPercentArray(columnWidths), true)
    bodyTable.setWidth(UnitValue.createPercentValue(100f))
    document.add(bodyTable)

    val headers = if (HP.settings.saleCartons == true)
        listOf(
            "Sr.",
            "Itemname",
            "Qty",
            "Crtn",
            "Cost",
            "C.Cost",
            "Total",
        )
    else
        listOf(
            "Sr.",
            "Itemname",
            "Qty",
            "Cost",
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
            Cell().add(Paragraph("${counter + 1}").setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        // region Itemname & Urduname
        val itemnameTable =
            Table(if (HP.printSettings.showUrdu == true) floatArrayOf(1f, 1f) else floatArrayOf(1f))
        itemnameTable.setWidth(UnitValue.createPercentValue(100f))

        itemnameTable.addCell(
            Cell().add(Paragraph(item.itemname.toString()))
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(null)
        )

        if (HP.printSettings.showUrdu == true) {
            val image = urduTextToPdfImage(context, item.urduname.toString())
            image.setHorizontalAlignment(HorizontalAlignment.RIGHT)

            itemnameTable.addCell(
                Cell().add(image)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(null)
            )
        }

        bodyTable.addCell(
            Cell().add(itemnameTable)
                .setFontSize(REPORT_BODY_FONT_SIZE)
        )
        // endregion

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
            Cell().add(Paragraph(HP.formatDecimal(item.cost)).setFontSize(REPORT_BODY_FONT_SIZE))
                .setTextAlignment(TextAlignment.CENTER)
        )

        if (HP.settings.saleCartons == true) {
            bodyTable.addCell(
                Cell().add(Paragraph(HP.formatDecimal(item.costCrtn)).setFontSize(REPORT_BODY_FONT_SIZE))
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
    val footerTable =
        Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
            .useAllAvailableWidth()

    footerTable.addCell(
        Cell()
            .add(
                Paragraph()
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
            )
            .setFontSize(REPORT_HEADINGS_FONT_SIZE)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    footerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Sub Total: ").setBold())
                    .add(
                        Div()
                            .setWidth(UnitValue.createPointValue(100f))
//                            .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                            .add(
                                Paragraph(HP.formatDecimal((bill[0].grandTotal!! + bill[0].grandTotalDisc!!)))
                                    .setTextAlignment(TextAlignment.RIGHT)
                            )
                    )
            )
            .setFontSize(REPORT_HEADINGS_FONT_SIZE)
            .setBorder(null)
            .setTextAlignment(TextAlignment.RIGHT)
    )

    footerTable.addCell(
        Cell()
            .add(
                Paragraph()
            )
            .setFontSize(REPORT_HEADINGS_FONT_SIZE)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    footerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Discount: ").setBold())
                    .add(
                        Div()
                            .setWidth(UnitValue.createPointValue(100f))
                            .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                            .add(
                                Paragraph(HP.formatDecimal(bill[0].grandTotalDisc))
                                    .setTextAlignment(TextAlignment.RIGHT)
                            )
                    )
                    .setMarginTop(-5f)
            )
            .setFontSize(REPORT_HEADINGS_FONT_SIZE)
            .setBorder(null)
            .setTextAlignment(TextAlignment.RIGHT)
    )

    footerTable.addCell(
        Cell()
            .add(
                Paragraph()
            )
            .setFontSize(REPORT_HEADINGS_FONT_SIZE)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    footerTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Grand Total: ").setBold())
                    .add(
                        Div()
                            .setWidth(UnitValue.createPointValue(100f))
                            .setBorderBottom(
                                SolidBorder(
                                    if (bill[0].purchaseOn == "Credit") ColorConstants.WHITE else ColorConstants.BLACK,
                                    if (bill[0].purchaseOn == "Credit") 0f else 1f
                                )
                            )
                            .add(
                                Paragraph(HP.formatDecimal(bill[0].grandTotal))
                                    .setTextAlignment(TextAlignment.RIGHT)
                            )
                    )
                    .setMarginTop(-5f)
            )
            .setFontSize(REPORT_HEADINGS_FONT_SIZE)
            .setBorder(null)
            .setTextAlignment(TextAlignment.RIGHT)
    )

    if (bill[0].purchaseOn == "Credit") {
        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                )
                .setFontSize(REPORT_HEADINGS_FONT_SIZE)
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT)
        )

        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                        .add(Text("Old Balance: ").setBold())
                        .add(
                            Div()
                                .setWidth(UnitValue.createPointValue(100f))
                                .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                                .add(
                                    Paragraph("${HP.formatDecimal(abs(bill[0].oldBalance!!))} ${if (bill[0].oldBalance!! > 0) "R" else "P"}")
                                        .setTextAlignment(TextAlignment.RIGHT)
                                )
                        )
                        .setMarginTop(-5f)
                )
                .setFontSize(REPORT_HEADINGS_FONT_SIZE)
                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT)
        )

        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                )
                .setFontSize(REPORT_HEADINGS_FONT_SIZE)
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT)
        )

        footerTable.addCell(
            Cell()
                .add(
                    Paragraph()
                        .add(Text("New Balance: ").setBold())
                        .add(
                            Div()
                                .setWidth(UnitValue.createPointValue(100f))
                                .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                                .add(
                                    Paragraph("${HP.formatDecimal(abs(bill[0].newBalance!!))} ${if (bill[0].newBalance!! > 0) "R" else "P"}")
                                        .setTextAlignment(TextAlignment.RIGHT)
                                )
                        )
                        .setMarginTop(-5f)
                )
                .setFontSize(REPORT_HEADINGS_FONT_SIZE)
                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT)
        )
    }

    document.add(footerTable)
    // endregion

    pageHandler.writeTotal(pdf)
    document.close()
    return file
}