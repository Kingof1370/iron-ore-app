package ir.alibahmani.ironorecalc.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import ir.alibahmani.ironorecalc.domain.model.CalculationResult
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Generates a PDF report using Android's built-in PdfDocument API.
 * No external library required — fully compatible with Android 26+.
 */
object PdfGenerator {

    private val DATE_FORMAT = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale("fa", "IR"))

    private const val PAGE_W = 595   // A4 width in points (72 dpi)
    private const val PAGE_H = 842   // A4 height in points
    private const val MARGIN = 40f
    private const val COL_RIGHT = PAGE_W - MARGIN  // right edge

    fun generate(context: Context, projectName: String, result: CalculationResult): File {
        val doc = PdfDocument()
        val renderer = PageRenderer(doc)

        // ── Header ──────────────────────────────────────────────────────────
        renderer.fillRect(0f, 0f, PAGE_W.toFloat(), 80f, Color.rgb(13, 27, 42))
        renderer.text("Iron Ore Industrial Calculator", MARGIN, 30f, 16f, bold = true, color = Color.WHITE)
        renderer.text("گزارش محاسبه جداسازی مغناطیسی سنگ آهن", MARGIN, 50f, 12f, color = Color.WHITE)
        renderer.text("توسعه‌یافته توسط علی بهمنی | 09915420558", MARGIN, 68f, 9f, color = Color.rgb(180, 200, 220))
        renderer.y = 96f

        // ── Meta ─────────────────────────────────────────────────────────────
        renderer.text("نام پروژه: $projectName", MARGIN, renderer.y, 10f, bold = true)
        renderer.y += 16f
        renderer.text("تاریخ گزارش: ${DATE_FORMAT.format(Date())}", MARGIN, renderer.y, 9f, color = Color.DKGRAY)
        renderer.y += 20f

        // ── Mass Balance ─────────────────────────────────────────────────────
        val mb = result.massBalance
        renderer.sectionHeader("موازنه جرم")
        renderer.table(listOf(
            "ظرفیت محصول میانی (F2)" to "%.2f t/h".format(mb.f2),
            "باطله مرحله اول (T1)"  to "%.2f t/h".format(mb.t1),
            "بازیابی مرحله اول (R1)" to "%.2f%%".format(mb.r1),
            "کنسانتره نهایی (F3)"   to "%.2f t/h".format(mb.f3),
            "باطله نهایی (T2)"      to "%.2f t/h".format(mb.t2),
            "بازیابی مرحله دوم (R2)" to "%.2f%%".format(mb.r2),
            "بازیابی کلی آهن"       to "%.2f%%".format(mb.rTotal),
            "خطای موازنه آهن"       to "%.2f%%".format(result.ironBalance.errorAbs)
        ))

        // ── Process Indicators ───────────────────────────────────────────────
        renderer.sectionHeader("شاخص‌های فرآوری")
        renderer.table(listOf(
            "نوع کانی خوراک"                       to result.mineral.label,
            "نسبت غنی‌سازی"                        to "%.2f".format(result.enrichmentRatio),
            "بازده جرمی کلی"                       to "%.2f%%".format(result.yieldPct),
            "کارایی جداسازی Newton"                to "%.2f%%".format(result.newton),
            "شاخص گزینش‌پذیری"                    to "%.2f".format(result.selectivityIndex),
            "نسبت پذیرفتاری (MSR)"                 to (result.msr?.let { "%.4f".format(it) } ?: "وارد نشده"),
            "کفایت میدان درام اول"                 to result.fieldAdequacy1.status,
            "کفایت میدان درام دوم"                 to result.fieldAdequacy2.status
        ))

        // ── Multi-Criteria ───────────────────────────────────────────────────
        renderer.sectionHeader("تحلیل چندمعیاره عملکرد")
        val mcRows = mutableListOf(
            "امتیاز کلی اطمینان" to "%.1f / 100".format(result.multiCriteria.overallScore),
            "مهم‌ترین عامل محدودکننده" to result.multiCriteria.limitingFactor
        )
        result.multiCriteria.breakdown.forEach { c ->
            mcRows += "${c.name} (وزن ${c.weight})" to "%.0f / 100".format(c.score)
        }
        renderer.table(mcRows)

        // ── Warnings ─────────────────────────────────────────────────────────
        if (result.warnings.isNotEmpty()) {
            renderer.sectionHeader("هشدارها")
            result.warnings.forEach { renderer.bullet(it) }
        }

        // ── Suggestions ──────────────────────────────────────────────────────
        if (result.suggestions.isNotEmpty()) {
            renderer.sectionHeader("پیشنهادهای بهبود")
            result.suggestions.forEach { renderer.bullet(it) }
        }

        // ── Footer ───────────────────────────────────────────────────────────
        renderer.hLine()
        renderer.text(
            "© 1404 علی بهمنی | 09915420558 | Iron Ore Industrial Calculator v1.0",
            MARGIN, renderer.y, 8f, color = Color.GRAY
        )

        renderer.finish()

        val file = File(context.cacheDir, "IronOre_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { doc.writeTo(it) }
        doc.close()
        return file
    }

    // ── Internal page renderer ───────────────────────────────────────────────

    private class PageRenderer(private val doc: PdfDocument) {
        var y = 0f
        private var pageNum = 1
        private lateinit var page: PdfDocument.Page
        private lateinit var canvas: Canvas

        private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; textSize = 10f }
        private val paintFill = Paint(Paint.ANTI_ALIAS_FLAG)
        private val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.rgb(200, 200, 200)
            strokeWidth = 0.5f
        }

        init { newPage() }

        fun newPage() {
            if (::page.isInitialized) doc.finishPage(page)
            val info = PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNum++).create()
            page = doc.startPage(info)
            canvas = page.canvas
            y = MARGIN
        }

        private fun ensureSpace(needed: Float) {
            if (y + needed > PAGE_H - MARGIN) newPage()
        }

        fun fillRect(l: Float, t: Float, r: Float, b: Float, color: Int) {
            paintFill.color = color
            canvas.drawRect(l, t, r, b, paintFill)
        }

        fun text(txt: String, x: Float, y: Float, size: Float, bold: Boolean = false, color: Int = Color.BLACK) {
            paintText.textSize = size
            paintText.color = color
            paintText.typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            canvas.drawText(txt, x, y, paintText)
        }

        fun sectionHeader(title: String) {
            ensureSpace(24f)
            y += 6f
            fillRect(MARGIN, y, COL_RIGHT.toFloat(), y + 18f, Color.rgb(220, 232, 250))
            text(title, MARGIN + 6f, y + 13f, 11f, bold = true, color = Color.rgb(20, 50, 90))
            y += 22f
        }

        fun table(rows: List<Pair<String, String>>) {
            val col1W = (COL_RIGHT - MARGIN) * 0.62f
            val col2W = (COL_RIGHT - MARGIN) * 0.38f
            val rowH = 16f
            rows.forEachIndexed { i, (label, value) ->
                ensureSpace(rowH + 2f)
                val bg = if (i % 2 == 0) Color.WHITE else Color.rgb(245, 248, 255)
                fillRect(MARGIN, y, COL_RIGHT.toFloat(), y + rowH, bg)
                canvas.drawRect(MARGIN, y, COL_RIGHT.toFloat(), y + rowH, paintBorder)
                canvas.drawLine(MARGIN + col1W, y, MARGIN + col1W, y + rowH, paintBorder)
                text(label, MARGIN + 4f, y + 11f, 9f)
                text(value, MARGIN + col1W + 4f, y + 11f, 9f, bold = true)
                y += rowH
            }
            y += 8f
        }

        fun bullet(txt: String) {
            ensureSpace(16f)
            text("•  $txt", MARGIN + 4f, y + 11f, 9f, color = Color.rgb(60, 60, 60))
            y += 16f
        }

        fun hLine() {
            ensureSpace(10f)
            y += 8f
            canvas.drawLine(MARGIN, y, COL_RIGHT.toFloat(), y, paintBorder)
            y += 10f
        }

        fun finish() {
            doc.finishPage(page)
        }
    }
}
