package com.parkos.app.ui.map

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.parkos.app.R
import com.parkos.app.domain.model.IncidentReport
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

internal object IncidentReportPdfGenerator {

    fun createIncidentReportPdf(
        context: Context,
        report: IncidentReport,
        staffName: String?,
        staffEmail: String?
    ): File {
        val pdfDocument = PdfDocument()

        val pageWidth = 595
        val pageHeight = 842
        val margin = 42f
        val contentWidth = pageWidth - (margin * 2)

        var pageNumber = 0
        var currentPage: PdfDocument.Page? = null
        lateinit var canvas: Canvas
        var y = 0f

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(30, 30, 30)
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val sectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(255, 112, 31)
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(75, 75, 75)
            textSize = 10.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(35, 35, 35)
            textSize = 10.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val smallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(100, 100, 100)
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        fun finishCurrentPage() {
            currentPage?.let { pdfDocument.finishPage(it) }
            currentPage = null
        }

        fun drawHeader() {
            y = 34f

            val logoBitmap = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.parkos_logo
            )

            if (logoBitmap != null) {
                val logoRect = android.graphics.RectF(
                    margin,
                    y,
                    margin + 58f,
                    y + 58f
                )
                canvas.drawBitmap(logoBitmap, null, logoRect, null)
            }

            canvas.drawText("PARKOS", margin + 72f, y + 22f, titlePaint)
            canvas.drawText("Reporte de incidente", margin + 72f, y + 42f, sectionPaint)

            val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.rgb(255, 112, 31)
                strokeWidth = 2f
            }

            y += 76f
            canvas.drawLine(margin, y, pageWidth - margin, y, linePaint)
            y += 28f
        }

        fun startPage() {
            pageNumber += 1
            currentPage = pdfDocument.startPage(
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            )
            canvas = currentPage!!.canvas
            drawHeader()
        }

        fun ensureSpace(required: Float) {
            if (y + required > pageHeight - 60f) {
                finishCurrentPage()
                startPage()
            }
        }

        fun wrapText(
            text: String,
            paint: Paint,
            maxWidth: Float
        ): List<String> {
            val result = mutableListOf<String>()

            text.split("\n").forEach { paragraph ->
                val words = paragraph.trim().split(Regex("\\s+"))

                if (paragraph.isBlank()) {
                    result.add("")
                } else {
                    var line = ""

                    words.forEach { word ->
                        val testLine = if (line.isBlank()) word else "$line $word"

                        if (paint.measureText(testLine) <= maxWidth) {
                            line = testLine
                        } else {
                            if (line.isNotBlank()) {
                                result.add(line)
                            }
                            line = word
                        }
                    }

                    if (line.isNotBlank()) {
                        result.add(line)
                    }
                }
            }

            return result
        }

        fun drawWrappedText(
            text: String,
            paint: Paint,
            startX: Float,
            maxWidth: Float,
            lineHeight: Float
        ) {
            val lines = wrapText(text, paint, maxWidth)

            lines.forEach { line ->
                ensureSpace(lineHeight + 4f)

                if (line.isBlank()) {
                    y += lineHeight / 2f
                } else {
                    canvas.drawText(line, startX, y, paint)
                    y += lineHeight
                }
            }
        }

        fun drawSection(title: String) {
            ensureSpace(28f)
            canvas.drawText(title.uppercase(), margin, y, sectionPaint)
            y += 18f
        }

        fun drawKeyValue(label: String, value: String?) {
            ensureSpace(28f)

            val safeValue = value
                ?.takeIf { it.isNotBlank() }
                ?: "No especificado"

            canvas.drawText("$label:", margin, y, labelPaint)

            val beforeY = y

            drawWrappedText(
                text = safeValue,
                paint = bodyPaint,
                startX = margin + 145f,
                maxWidth = contentWidth - 145f,
                lineHeight = 14f
            )

            if (y < beforeY + 18f) {
                y = beforeY + 18f
            }

            y += 4f
        }

        fun drawParagraph(text: String) {
            drawWrappedText(
                text = text,
                paint = bodyPaint,
                startX = margin,
                maxWidth = contentWidth,
                lineHeight = 15f
            )
            y += 8f
        }

        startPage()

        val dateTime = formatReportDateTime(report.createdAt)
        val incidentLabel = incidentTypeToSpanishForPdf(
            report.incidentType,
            report.customIncidentType
        )

        drawSection("Datos del reporte")
        drawKeyValue("Folio", report.reportNumber)
        drawKeyValue("Fecha de emisión", dateTime.first)
        drawKeyValue("Hora de emisión", dateTime.second)
        drawKeyValue("Estatus", reportStatusToSpanish(report.status))

        y += 8f

        drawSection("Datos del estacionamiento")
        drawKeyValue("Estacionamiento", report.parkingLotName)
        drawKeyValue("Dirección", report.parkingLotAddress)
        drawKeyValue("Casilla relacionada", report.spotNumber)

        y += 8f

        drawSection("Personal que reporta")
        drawKeyValue("Nombre", staffName ?: "Personal staff")
        drawKeyValue("Correo", staffEmail ?: "No disponible")

        y += 8f

        drawSection("Datos del vehículo")
        drawKeyValue("Placa", report.vehiclePlate)

        y += 8f

        drawSection("Tipo de incidente")
        drawKeyValue("Incidente", incidentLabel)

        y += 8f

        drawSection("Descripción formal")

        drawParagraph(
            "Por medio del presente documento se deja constancia de que, en la fecha y hora señaladas, " +
                    "el personal de staff identificado en este reporte observó o recibió información relacionada " +
                    "con un incidente ocurrido dentro del estacionamiento indicado."
        )

        drawParagraph(
            "De acuerdo con la información registrada, el incidente se encuentra presuntamente relacionado " +
                    "con el vehículo con placas ${report.vehiclePlate}" +
                    (report.spotNumber?.let { ", ubicado o vinculado a la casilla $it." } ?: ".")
        )

        drawParagraph(
            "El presente reporte se emite con carácter preventivo e informativo, con la finalidad de documentar " +
                    "los hechos observados y facilitar su revisión por parte del personal responsable o superior correspondiente."
        )

        y += 6f

        drawSection("Detalles adicionales")

        drawParagraph(
            report.details?.takeIf { it.isNotBlank() }
                ?: "No se agregaron detalles adicionales."
        )

        y += 6f

        drawSection("Declaración")

        drawParagraph(
            "Este documento no constituye una determinación de responsabilidad. Su finalidad es dejar registro interno " +
                    "del incidente para seguimiento, revisión y toma de acciones correspondientes."
        )

        ensureSpace(30f)

        canvas.drawText(
            "Generado desde la aplicación ParkOs.",
            margin,
            y,
            smallPaint
        )

        finishCurrentPage()

        val directory = File(context.cacheDir, "incident_reports")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val safeFileName = report.reportNumber
            .replace("/", "-")
            .replace(":", "-")
            .replace(" ", "_")

        val file = File(directory, "$safeFileName.pdf")

        FileOutputStream(file).use { output ->
            pdfDocument.writeTo(output)
        }

        pdfDocument.close()

        return file
    }

    fun shareIncidentReportPdf(
        context: Context,
        pdfFile: File
    ) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            pdfFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Reporte de incidente ParkOs")
            putExtra(
                Intent.EXTRA_TEXT,
                "Adjunto reporte de incidente generado desde ParkOs."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(
            shareIntent,
            "Compartir reporte"
        )

        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun incidentTypeToSpanishForPdf(
        type: String,
        customType: String?
    ): String {
        return when (type) {
            "robo" -> "Robo"
            "danio_vehiculo" -> "Daño a vehículo"
            "danio_infraestructura" -> "Daño a infraestructura del estacionamiento"
            "agresion" -> "Agresión a trabajador o persona"
            "actividad_sospechosa" -> "Actividad sospechosa"
            "vehiculo_mal_estacionado" -> "Vehículo mal estacionado"
            "otro" -> customType?.takeIf { it.isNotBlank() } ?: "Otro"
            else -> type
        }
    }

    private fun reportStatusToSpanish(status: String): String {
        return when (status) {
            "open" -> "Abierto"
            "reviewed" -> "Revisado"
            "closed" -> "Cerrado"
            else -> status
        }
    }

    private fun formatReportDateTime(value: String): Pair<String, String> {
        return try {
            val normalized = normalizeSupabaseTimestampForPdf(value)

            val inputFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                Locale.US
            )

            val date = inputFormat.parse(normalized)

            if (date != null) {
                val dateFormat = SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale("es", "MX")
                )

                val timeFormat = SimpleDateFormat(
                    "HH:mm",
                    Locale("es", "MX")
                )

                dateFormat.format(date) to timeFormat.format(date)
            } else {
                "Fecha no disponible" to "Hora no disponible"
            }
        } catch (e: Exception) {
            "Fecha no disponible" to "Hora no disponible"
        }
    }

    private fun normalizeSupabaseTimestampForPdf(value: String): String {
        val cleanValue = value.trim().replace("Z", "+00:00")

        val fractionalRegex = Regex("(\\.\\d{1,9})([+-]\\d{2}:\\d{2})$")

        val fixedFractional = fractionalRegex.replace(cleanValue) { match ->
            val fraction = match.groupValues[1]
                .removePrefix(".")
                .padEnd(3, '0')
                .take(3)

            ".$fraction${match.groupValues[2]}"
        }

        val hasFraction = Regex("\\.\\d{3}[+-]\\d{2}:\\d{2}$")
            .containsMatchIn(fixedFractional)

        if (hasFraction) {
            return fixedFractional
        }

        val timezoneRegex = Regex("([+-]\\d{2}:\\d{2})$")

        val withMilliseconds = timezoneRegex.replace(fixedFractional) { match ->
            ".000${match.groupValues[1]}"
        }

        return if (timezoneRegex.containsMatchIn(withMilliseconds)) {
            withMilliseconds
        } else {
            "$withMilliseconds.000+00:00"
        }
    }
}