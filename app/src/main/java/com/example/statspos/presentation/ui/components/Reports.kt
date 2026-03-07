package com.example.statspos.presentation.ui.components

import com.itextpdf.kernel.events.Event
import com.itextpdf.kernel.events.IEventHandler
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject
import com.itextpdf.layout.Canvas
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment

class PageNumberEventHandler : IEventHandler {
    override fun handleEvent(event: Event?) {
        val docEvent = event as PdfDocumentEvent
        val pdfDoc = docEvent.document
        val page = docEvent.page

        val pageNumber = pdfDoc.getPageNumber(page)

        val canvas = PdfCanvas(page)
        val rect = page.pageSize

        val canvasModel = Canvas(canvas, rect)

        canvasModel.showTextAligned(
            Paragraph("Page $pageNumber"),
            rect.width / 2,
            15f,
            TextAlignment.CENTER
        )

        canvasModel.close()
    }
}

class PageXofYEventHandler(private val pdf: PdfDocument) : IEventHandler {

    private val placeholder = PdfFormXObject(Rectangle(0f, 0f, 30f, 12f))

    override fun handleEvent(event: Event) {
        val docEvent = event as PdfDocumentEvent
        val page = docEvent.page
        val pageNumber = pdf.getPageNumber(page)

        val pageSize = page.pageSize
        val pdfCanvas = PdfCanvas(page)

        val canvas = Canvas(pdfCanvas, pageSize)

        val text = "Page $pageNumber of "

        val x = pageSize.width / 2 // To keep Page 1 of 2 in center
//        val x = pageSize.width - 60  // To keep Page 1 of 2 on right side
        val y = 20f

        canvas.showTextAligned(
            Paragraph(text),
            x,
            y,
            TextAlignment.RIGHT
        )

        pdfCanvas.addXObjectAt(placeholder, x + 2, y)

        canvas.close()
    }

    fun writeTotal(pdf: PdfDocument) {
        val canvas = Canvas(placeholder, pdf)
        canvas.showTextAligned(
            Paragraph(pdf.numberOfPages.toString()),
            0f,
            0f,
            TextAlignment.LEFT
        )
        canvas.close()
    }
}