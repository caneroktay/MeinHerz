package com.kalbim.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.kalbim.data.model.Measurement
import com.kalbim.data.model.UserProfile
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.res.stringResource
import com.kalbim.R

object PdfGenerator {
    private fun drawLineChart(
        canvas: Canvas,
        x: Float, y: Float,
        width: Float, height: Float,
        points: List<Float>,
        color: Int,
        title: String,
        unit: String
    ) {
        val paintChartTitle = Paint().apply {
            this.color    = Color.parseColor("#E53935")
            textSize      = 11f
            isFakeBoldText = true
        }
        val paintAxis = Paint().apply {
            this.color  = Color.LTGRAY
            strokeWidth = 1f
        }
        val paintLine = Paint().apply {
            this.color  = color
            strokeWidth = 2.5f
            isAntiAlias = true
        }
        val paintDot = Paint().apply {
            this.color  = color
            isAntiAlias = true
        }
        val paintLabel = Paint().apply {
            this.color = Color.GRAY
            textSize   = 8f
        }
        val paintValue = Paint().apply {
            this.color     = color
            textSize       = 8f
            isFakeBoldText = true
        }

        // Başlık
        canvas.drawText("$title ($unit)", x, y, paintChartTitle)
        val chartTop    = y + 8f
        val chartBottom = y + height
        val chartLeft   = x + 30f
        val chartRight  = x + width

        // Izgara çizgileri
        val gridCount = 4
        val minVal = points.minOrNull() ?: 0f
        val maxVal = points.maxOrNull() ?: 1f
        val range  = if (maxVal - minVal < 1f) 1f else maxVal - minVal

        for (i in 0..gridCount) {
            val gy = chartBottom - (chartBottom - chartTop) * i / gridCount
            canvas.drawLine(chartLeft, gy, chartRight, gy, paintAxis)
            val labelVal = minVal + range * i / gridCount
            canvas.drawText("%.0f".format(labelVal), x, gy + 3f, paintLabel)
        }

        // Çizgi & noktalar
        if (points.size >= 2) {
            for (i in 0 until points.size - 1) {
                val x1 = chartLeft + (i.toFloat() / (points.size - 1)) * (chartRight - chartLeft)
                val y1 = chartBottom - ((points[i] - minVal) / range) * (chartBottom - chartTop)
                val x2 = chartLeft + ((i + 1).toFloat() / (points.size - 1)) * (chartRight - chartLeft)
                val y2 = chartBottom - ((points[i + 1] - minVal) / range) * (chartBottom - chartTop)
                canvas.drawLine(x1, y1, x2, y2, paintLine)
            }
        }
        points.forEachIndexed { i, v ->
            val px = chartLeft + (i.toFloat() / (points.size - 1).coerceAtLeast(1)) * (chartRight - chartLeft)
            val py = chartBottom - ((v - minVal) / range) * (chartBottom - chartTop)
            canvas.drawCircle(px, py, 4f, paintDot)
            canvas.drawCircle(px, py, 2f, Paint().apply { this.color = Color.WHITE })
            canvas.drawText("%.0f".format(v), px - 8f, py - 6f, paintValue)
        }
    }
    fun generate(
        context: Context,
        profile: UserProfile,
        measurements: List<Measurement>,
        medications: List<com.kalbim.data.model.Medication> = emptyList(),
        fromDate: String = "",
        toDate: String = ""
    ): File? {
        return try {
            val doc  = PdfDocument()
            val pageWidth  = 595   // A4 nokta genişliği
            val pageHeight = 842   // A4 nokta yüksekliği
            val margin = 50f

            // ── Renkler & Fırçalar ──────────────────────────
            val paintTitle = Paint().apply {
                color     = Color.parseColor("#E53935")
                textSize  = 24f
                isFakeBoldText = true
            }
            val paintHeader = Paint().apply {
                color    = Color.parseColor("#E53935")
                textSize = 14f
                isFakeBoldText = true
            }
            val paintNormal = Paint().apply {
                color    = Color.BLACK
                textSize = 11f
            }
            val paintSmall = Paint().apply {
                color    = Color.GRAY
                textSize = 9f
            }
            val paintBold = Paint().apply {
                color          = Color.BLACK
                textSize       = 11f
                isFakeBoldText = true
            }
            val paintLine = Paint().apply {
                color       = Color.parseColor("#DDDDDD")
                strokeWidth = 1f
            }
            val paintRedLine = Paint().apply {
                color       = Color.parseColor("#E53935")
                strokeWidth = 2f
            }

            var pageNum  = 1
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
            var page     = doc.startPage(pageInfo)
            var canvas   = page.canvas
            var y        = margin


            fun newPage() {
                doc.finishPage(page)
                pageNum++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
                page     = doc.startPage(pageInfo)
                canvas   = page.canvas
                y        = margin
            }

            fun checkPageBreak(needed: Float = 40f) {
                if (y + needed > pageHeight - margin) newPage()
            }

            // ── BAŞLIK ──────────────────────────────────────
            canvas.drawText("🫀" + " " + context.getString(R.string.app_name), margin, y, paintTitle)
            y += 8f
            canvas.drawLine(margin, y, pageWidth - margin, y, paintRedLine)
            y += 20f

            // Hasta bilgileri
            canvas.drawText(context.getString(R.string.pdf_rapor), margin, y, paintHeader)
            y += 20f

            val fmt = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val today = fmt.format(Date())

            val infoLines = listOf(
                context.getString(R.string.pdf_person_fullname) + "          : " + "${profile.firstName} ${profile.lastName}",
                context.getString(R.string.pdf_person_bday) + "   : " + "${profile.birthDate}",
                context.getString(R.string.pdf_person_rday) + "     : " + "$today",
                if (fromDate.isNotBlank()) "Tarih Aralığı: $fromDate — $toDate" else ""
            ).filter { it.isNotBlank() }

            infoLines.forEach { line ->
                canvas.drawText(line, margin, y, paintNormal)
                y += 16f
            }

            y += 8f
            canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
            y += 16f

            // ── TIBBİ GEÇMİŞ ───────────────────────────────
            if (profile.medicalHistory.isNotBlank()) {
                canvas.drawText(context.getString(R.string.pdf_medical_history), margin, y, paintHeader)
                y += 16f
                profile.medicalHistory.split("\n").forEach { line ->
                    checkPageBreak()
                    canvas.drawText(line, margin, y, paintNormal)
                    y += 14f
                }
                y += 8f
                canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
                y += 16f
            }

            // ── KULLANILAN İLAÇLAR ──────────────────────────
            if (medications.isNotEmpty()) {
                checkPageBreak(60f)
                canvas.drawText(context.getString(R.string.pdf_medicinelist), margin, y, paintHeader)
                y += 16f

                medications.forEach { med ->
                    checkPageBreak(40f)
                    val status = if (med.isActive) context.getString(R.string.pdf_true) else context.getString(R.string.pdf_false)
                    canvas.drawText(
                        "${med.name} — ${med.dosage}  [$status]",
                        margin, y, paintBold
                    )
                    y += 14f

                    val dozStr = buildString {
                        if (med.morning > 0) append(context.getString(R.string.pdf_morning) + "  ${med.morning}   ")
                        if (med.noon    > 0) append(context.getString(R.string.pdf_noon) + "  ${med.noon}  ")
                        if (med.evening > 0) append(context.getString(R.string.pdf_evening) + "  ${med.evening}  ")
                        if (med.night   > 0) append(context.getString(R.string.pdf_night) + "  ${med.night}  ")
                    }
                    if (dozStr.isNotBlank()) {
                        canvas.drawText("     " + context.getString(R.string.pdf_dose) + "  $dozStr ", margin, y, paintNormal)
                        y += 13f
                    }
                    if (med.startDate.isNotBlank()) {
                        canvas.drawText("     " + context.getString(R.string.pdf_begin) + "  ${med.startDate}", margin, y, paintNormal)
                        y += 13f
                    }
                    if (med.notes.isNotBlank()) {
                        canvas.drawText("  Not: ${med.notes}", margin, y, paintSmall)
                        y += 12f
                    }
                    y += 4f
                }
                canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
                y += 16f
            }

            // ── ÖLÇÜM TABLOSU ───────────────────────────────
            checkPageBreak(60f)
            canvas.drawText(context.getString(R.string.pdf_list_title) + " (${measurements.size}" + context.getString(R.string.pdf_list_title2) + " )", margin, y, paintHeader)
            y += 16f

            // Tablo başlığı
            val col = listOf(50f, 130f, 210f, 270f, 330f, 400f, 470f)
            val headers = listOf(
                context.getString(R.string.pdf_list_date),
                context.getString(R.string.pdf_list_pressure),
                context.getString(R.string.pdf_list_pulse),
                context.getString(R.string.pdf_list_weight),
                "NT-proBNP",
                "Troponin",
                context.getString(R.string.pdf_list_notes)
            )
            headers.forEachIndexed { i, h ->
                canvas.drawText(h, margin + col[i], y, paintBold)
            }
            y += 4f
            canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
            y += 12f

            // Tablo satırları
            measurements.forEach { m ->
                checkPageBreak(16f)
                val rowData = listOf(
                    m.dateLabel.take(10),
                    if (m.systolic != null) "${m.systolic}/${m.diastolic}" else "--",
                    m.pulse?.toString() ?: "--",
                    m.weightKg?.let { "%.1f".format(it) } ?: "--",
                    m.ntProBnp?.let { "%.0f".format(it) } ?: "--",
                    m.troponin?.let { "%.2f".format(it) } ?: "--",
                    m.notes.take(20)
                )
                rowData.forEachIndexed { i, v ->
                    canvas.drawText(v, margin + col[i], y, paintNormal)
                }
                y += 14f
                canvas.drawLine(margin, y - 2f, pageWidth - margin, y - 2f,
                    paintLine.apply { alpha = 80 })
            }
            // ── GRAFİKLER ───────────────────────────────────────────
            val bpData     = measurements.filter { it.systolic  != null }.reversed()
            val pulseData = measurements.filter { it.pulse != null }.reversed()
            val weightData = measurements.filter { it.weightKg  != null }.reversed()
            val bnpData    = measurements.filter { it.ntProBnp  != null }.reversed()
            val tropData   = measurements.filter { it.troponin  != null }.reversed()

            if (bpData.size >= 2) {
                checkPageBreak(160f)
                canvas.drawText(context.getString(R.string.pdf_graf_title), margin, y, paintHeader)
                y += 20f

                // Sistolik grafik
                drawLineChart(
                    canvas = canvas,
                    x = margin, y = y,
                    width = (pageWidth - margin * 2) * 0.48f,
                    height = 90f,
                    points = bpData.map { it.systolic!!.toFloat() },
                    color  = Color.parseColor("#E53935"),
                    title  = context.getString(R.string.systolic),
                    unit   = ""
                )
                // Diastolik grafik (sağ taraf)
                drawLineChart(
                    canvas = canvas,
                    x = margin + (pageWidth - margin * 2) * 0.52f, y = y,
                    width = (pageWidth - margin * 2) * 0.48f,
                    height = 90f,
                    points = bpData.map { it.diastolic!!.toFloat() },
                    color  = Color.parseColor("#1E88E5"),
                    title  = context.getString(R.string.diastolic),
                    unit   = ""
                )
                y += 120f
            }
            if (pulseData.size >= 2) {
                checkPageBreak(130f)
                drawLineChart(
                    canvas = canvas,
                    x = margin, y = y,
                    width = pageWidth - margin * 2,
                    height = 90f,
                    points = pulseData.map { it.pulse!!.toFloat() },
                    color  = Color.parseColor("#E53935"),
                    title  = context.getString(R.string.pulse),
                    unit   = ""
                )
                y += 120f
            }
            if (weightData.size >= 2) {
                checkPageBreak(130f)
                drawLineChart(
                    canvas = canvas,
                    x = margin, y = y,
                    width = pageWidth - margin * 2,
                    height = 90f,
                    points = weightData.map { it.weightKg!! },
                    color  = Color.parseColor("#43A047"),
                    title  = context.getString(R.string.weight),
                    unit   = ""
                )
                y += 120f
            }

            if (bnpData.size >= 2) {
                checkPageBreak(130f)
                drawLineChart(
                    canvas = canvas,
                    x = margin, y = y,
                    width = pageWidth - margin * 2,
                    height = 90f,
                    points = bnpData.map { it.ntProBnp!! },
                    color  = Color.parseColor("#FB8C00"),
                    title  = "NT-proBNP",
                    unit   = "pg/mL"
                )
                y += 120f
            }

            if (tropData.size >= 2) {
                checkPageBreak(130f)
                drawLineChart(
                    canvas = canvas,
                    x = margin, y = y,
                    width = pageWidth - margin * 2,
                    height = 90f,
                    points = tropData.map { it.troponin!! },
                    color  = Color.parseColor("#8E24AA"),
                    title  = "Troponin",
                    unit   = "ng/L"
                )
                y += 120f
            }
            // ── YASAL UYARI ─────────────────────────────────
            checkPageBreak(50f)
            y += 16f
            canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
            y += 12f
            canvas.drawText(
                context.getString(R.string.pdf_info_text1),
                margin, y, paintSmall
            )
            y += 12f
            canvas.drawText(
                context.getString(R.string.pdf_info_text2),
                margin, y, paintSmall
            )

            doc.finishPage(page)

            // ── DOSYAYA KAYDET ──────────────────────────────
            val fileName = context.getString(R.string.app_name) + "_Raport_${
                SimpleDateFormat("ddMMyyyy_HHmm", Locale.getDefault()).format(Date())
            }.pdf"

            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?: context.filesDir
            dir.mkdirs()
            val file = File(dir, fileName)
            doc.writeTo(FileOutputStream(file))
            doc.close()
            file

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}