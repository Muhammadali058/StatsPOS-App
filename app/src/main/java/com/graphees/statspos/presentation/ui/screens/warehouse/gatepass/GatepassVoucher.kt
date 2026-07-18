package com.graphees.statspos.presentation.ui.screens.warehouse.gatepass

import android.content.Context
import com.graphees.statspos.domain.models.warehouse.GatepassVoucher
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

fun gatepassVoucher(
    context: Context,
    gatepass: List<GatepassVoucher>,
): File {

    // region Document
    val file = File(context.cacheDir, "Gatepass_${System.currentTimeMillis()}.pdf")
    if (file.exists()) {
        file.delete()
    }

    val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)

    val writer = PdfWriter(file)
    val pdf = PdfDocument(writer)
    val pageWidth = 226.77f
    val pageHeight = 2000f
    val pageSize = PageSize(pageWidth, pageHeight)
    val document = Document(pdf, pageSize)
    document.setMargins(5f, 5f, 5f, 5f)
    // endregion

    // region Header
    // ---------------- Report Title ----------------
    document.add(
        Paragraph("Gatepass")
            .setFont(boldFont)
            .setFontSize(18f)
            .setTextAlignment(TextAlignment.CENTER)
    )

    // ---------------- Top ----------------
    val topFontSize = 10f
    val dateTable = Table(UnitValue.createPercentArray(floatArrayOf(100f))).useAllAvailableWidth()

    dateTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Name: ").setFont(boldFont))
                    .add(Text(gatepass[0].gatepassName.toString()))
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    dateTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Warehouse: ").setFont(boldFont))
                    .add(Text(gatepass[0].warehouseName.toString()))
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    dateTable.addCell(
        Cell()
            .add(
                Paragraph()
                    .add(Text("Date: ").setFont(boldFont))
                    .add(Text(gatepass[0].date.toString()))
            )
            .setFontSize(topFontSize)
            .setBorder(null)
            .setTextAlignment(TextAlignment.LEFT)
    )

    document.add(dateTable)
    // endregion

    // region Details
    // ---------------- Table ----------------
    val columnWidths = if (HP.settings.saleCartons == true)
        floatArrayOf(3f, 1f, 1f)
    else
        floatArrayOf(3f, 1f)

    val bodyTable = Table(UnitValue.createPercentArray(columnWidths), true)
    bodyTable.setWidth(UnitValue.createPercentValue(100f))
    document.add(bodyTable)

    val fontSize = 8f

    val headers = if (HP.settings.saleCartons == true)
        listOf(
            "Itemname",
            "Qty",
            "Crtn",
        )
    else
        listOf(
            "Itemname",
            "Qty",
        )

    headers.forEachIndexed { index, item ->
        val cell = Cell().add(
            Paragraph(item)
                .setFont(boldFont)
                .setFontSize(fontSize)
        )

        cell.setTextAlignment(TextAlignment.CENTER)
        bodyTable.addHeaderCell(cell)
    }

    var counter = 0
    var totalQty = 0.0
    var totalCrtn = 0
    gatepass.forEach { item ->
        bodyTable.addCell(
            Cell().add(Paragraph(item.itemname.toString()).setFontSize(fontSize))
                .setTextAlignment(TextAlignment.LEFT)
        )

        bodyTable.addCell(
            Cell().add(Paragraph(HP.formatDecimal(item.qty)).setFontSize(fontSize))
                .setTextAlignment(TextAlignment.CENTER)
        )

        if (HP.settings.saleCartons == true) {
            bodyTable.addCell(
                Cell().add(Paragraph(item.crtn.toString()).setFontSize(fontSize))
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }


        totalQty += item.qty!!
        totalCrtn += item.crtn!!


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

    val paragraph = Paragraph()
        .add(Text("Total Qty: ").setFont(boldFont))
        .add(
            Div()
                .setWidth(UnitValue.createPointValue(50f))
                .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                .add(
                    Paragraph(HP.formatDecimal(totalQty))
                        .setTextAlignment(TextAlignment.CENTER)
                )
        ).setFontSize(fontSize)

    if (HP.settings.saleCartons == true) {
        paragraph
            .add(Text("Total Crtn: ").setFont(boldFont))
            .add(
                Div()
                    .setWidth(UnitValue.createPointValue(50f))
                    .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
                    .add(
                        Paragraph(totalCrtn.toString())
                            .setTextAlignment(TextAlignment.CENTER)
                    )
            ).setFontSize(fontSize)
    }

    val totalQtyCell = Cell()
        .add(paragraph)
        .setFontSize(12f)
        .setBorder(null)
        .setTextAlignment(TextAlignment.LEFT)

    row1.addCell(totalQtyCell)
    document.add(row1)

    document.add(
        Paragraph("Remarks: ")
            .setFont(boldFont)
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.LEFT)
    )

    document.add(
        Paragraph(gatepass[0].remarks)
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.LEFT)
    )

    // endregion

    document.close()
    return file
}