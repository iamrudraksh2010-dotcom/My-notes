package com.example.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.example.data.Note
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    fun generatePdf(context: Context, note: Note): Uri? {
        val pdfDocument = PdfDocument()

        // Standard A4 size in PostScript points (1/72 inch). 595 x 842.
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas

        // Set up Title Paint
        val titlePaint = TextPaint().apply {
            color = Color.BLACK
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        // Set up Content Paint
        val contentPaint = TextPaint().apply {
            color = Color.DKGRAY
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val margin = 50f
        val startY = margin
        val startX = margin
        val contentWidth = pageInfo.pageWidth - (margin * 2).toInt()

        // Draw Title using StaticLayout (handles wrapping if title is very long)
        val titleLayout = StaticLayout.Builder.obtain(note.title.ifEmpty { "Untitled Note" }, 0, note.title.length, titlePaint, contentWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .build()
        
        canvas.save()
        canvas.translate(startX, startY)
        titleLayout.draw(canvas)
        canvas.restore()

        val dateText = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault()).format(Date(note.timestamp))
        val datePaint = TextPaint(contentPaint).apply { textSize = 10f; color = Color.GRAY }
        
        val dateY = startY + titleLayout.height + 10f
        canvas.drawText(dateText, startX, dateY, datePaint)

        // Draw Content
        val contentStartY = dateY + 30f
        val contentLayout = StaticLayout.Builder.obtain(note.content, 0, note.content.length, contentPaint, contentWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1.2f)
            .setIncludePad(true)
            .build()

        canvas.save()
        canvas.translate(startX, contentStartY)
        contentLayout.draw(canvas)
        canvas.restore()

        pdfDocument.finishPage(page)

        // Save PDF
        val pdfsDir = File(context.cacheDir, "pdfs")
        if (!pdfsDir.exists()) {
            pdfsDir.mkdirs()
        }

        val fileName = "Note_${note.id}.pdf"
        val file = File(pdfsDir, fileName)

        try {
            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            return null
        }
        
        pdfDocument.close()

        // Return Uri using FileProvider
        return try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
