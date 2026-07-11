package ir.alibahmani.ironorecalc.utils

import android.content.Context
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import ir.alibahmani.ironorecalc.domain.model.CalculationResult
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Generates a professional PDF report of a calculation result.
 * Uses iText 5 (itextpdf). All text is Persian/Farsi.
 */
object PdfGenerator {

    private val DATE_FORMAT = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale("fa", "IR"))

    fun generate(context: Context, projectName: String, result: CalculationResult): File {
        val file = File(context.cacheDir, "IronOre_${System.currentTimeMillis()}.pdf")
        val doc = Document(PageSize.A4)
        PdfWriter.getInstance(doc, FileOutputStream(file))
        doc.open()

        // --- Fonts ---
        val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18f, BaseColor.WHITE)
        val headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13f, BaseColor(30, 60, 90))
        val bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10f, BaseColor.DARK_GRAY)
        val boldBody = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f, BaseColor.BLACK)

        // --- Header Banner ---
        val headerTable = PdfPTable(1).apply { widthPercentage = 100f; spacingAfter = 16f }
        val headerCell = PdfPCell().apply {
            backgroundColor = BaseColor(13, 27, 42)
            border = Rectangle.NO_BORDER
            paddingTop = 16f; paddingBottom = 16f; paddingLeft = 20f; paddingRight = 20f
            horizontalAlignment = Element.ALIGN_CENTER
        }
        headerCell.addElement(Phrase("Iron Ore Industrial Calculator\n", titleFont))
        headerCell.addElement(Phrase("گزارش محاسبه جداسازی مغناطیسی سنگ آهن\n", titleFont))
        headerCell.addElement(Phrase("توسعه‌یافته توسط علی بهمنی | 09915420558",
            FontFactory.getFont(FontFactory.HELVETICA, 9f, BaseColor(200, 200, 200))))
        headerTable.addCell(headerCell)
        doc.add(headerTable)

        // --- Meta ---
        doc.add(Paragraph("نام پروژه: $projectName", boldBody))
        doc.add(Paragraph("تاریخ تولید گزارش: ${DATE_FORMAT.format(Date())}", bodyFont))
        doc.add(Chunk.NEWLINE)

        // --- Mass Balance ---
        addSectionTitle(doc, "موازنه جرم", headFont)
        val mb = result.massBalance
        addResultTable(doc, listOf(
            "ظرفیت محصول میانی (F2)" to "%.2f تن/ساعت".format(mb.f2),
            "باطله مرحله اول (T1)" to "%.2f تن/ساعت".format(mb.t1),
            "بازیابی مرحله اول (R1)" to "%.2f٪".format(mb.r1),
            "ظرفیت کنسانتره نهایی (F3)" to "%.2f تن/ساعت".format(mb.f3),
            "باطله نهایی (T2)" to "%.2f تن/ساعت".format(mb.t2),
            "بازیابی مرحله دوم (R2)" to "%.2f٪".format(mb.r2),
            "بازیابی کلی آهن" to "%.2f٪".format(mb.rTotal),
            "خطای موازنه آهن" to "%.2f٪".format(result.ironBalance.errorAbs)
        ), bodyFont, boldBody)

        // --- Process Indicators ---
        addSectionTitle(doc, "شاخص‌های فرآوری", headFont)
        addResultTable(doc, listOf(
            "نوع کانی خوراک" to result.mineral.label,
            "نسبت غنی‌سازی" to "%.2f".format(result.enrichmentRatio),
            "بازده جرمی کلی" to "%.2f٪".format(result.yieldPct),
            "کارایی جداسازی Newton" to "%.2f٪".format(result.newton),
            "شاخص گزینش‌پذیری (Gaudin–Schulz)" to "%.2f".format(result.selectivityIndex),
            "نسبت پذیرفتاری مغناطیسی (MSR)" to (result.msr?.let { "%.4f".format(it) } ?: "وارد نشده"),
            "کفایت میدان درام اول" to result.fieldAdequacy1.status,
            "کفایت میدان درام دوم" to result.fieldAdequacy2.status
        ), bodyFont, boldBody)

        // --- Multi-Criteria ---
        addSectionTitle(doc, "تحلیل چندمعیاره عملکرد", headFont)
        addResultTable(doc, listOf(
            "امتیاز کلی اطمینان" to "%.1f / 100".format(result.multiCriteria.overallScore),
            "مهم‌ترین عامل محدودکننده" to result.multiCriteria.limitingFactor
        ) + result.multiCriteria.breakdown.map { c ->
            "${c.name} (وزن ${c.weight})" to "%.0f / 100".format(c.score)
        }, bodyFont, boldBody)

        // --- Warnings & Suggestions ---
        if (result.warnings.isNotEmpty()) {
            addSectionTitle(doc, "هشدارها", headFont)
            result.warnings.forEach { doc.add(Paragraph("• $it", bodyFont)) }
            doc.add(Chunk.NEWLINE)
        }
        if (result.suggestions.isNotEmpty()) {
            addSectionTitle(doc, "پیشنهادهای بهبود", headFont)
            result.suggestions.forEach { doc.add(Paragraph("• $it", bodyFont)) }
            doc.add(Chunk.NEWLINE)
        }

        // --- Footer ---
        doc.add(LineSeparator())
        doc.add(Paragraph(
            "\n© 1404 علی بهمنی | 09915420558 | Iron Ore Industrial Calculator v1.0",
            FontFactory.getFont(FontFactory.HELVETICA, 8f, BaseColor.GRAY)
        ))

        doc.close()
        return file
    }

    private fun addSectionTitle(doc: Document, title: String, font: Font) {
        val cell = PdfPCell(Phrase(title, font)).apply {
            backgroundColor = BaseColor(230, 240, 255)
            border = Rectangle.NO_BORDER
            paddingTop = 6f; paddingBottom = 6f; paddingLeft = 10f
        }
        val t = PdfPTable(1).apply { widthPercentage = 100f; spacingBefore = 12f; spacingAfter = 4f }
        t.addCell(cell); doc.add(t)
    }

    private fun addResultTable(doc: Document, rows: List<Pair<String, String>>, bodyFont: Font, boldFont: Font) {
        val t = PdfPTable(2).apply {
            widthPercentage = 100f
            spacingAfter = 6f
            setWidths(floatArrayOf(60f, 40f))
        }
        rows.forEachIndexed { idx, (label, value) ->
            val bg = if (idx % 2 == 0) BaseColor.WHITE else BaseColor(245, 248, 255)
            t.addCell(PdfPCell(Phrase(label, bodyFont)).apply {
                backgroundColor = bg; border = Rectangle.BOX; borderColor = BaseColor(220, 220, 220)
                paddingTop = 4f; paddingBottom = 4f; paddingLeft = 8f
            })
            t.addCell(PdfPCell(Phrase(value, boldFont)).apply {
                backgroundColor = bg; border = Rectangle.BOX; borderColor = BaseColor(220, 220, 220)
                paddingTop = 4f; paddingBottom = 4f; paddingLeft = 8f
                horizontalAlignment = Element.ALIGN_LEFT
            })
        }
        doc.add(t)
    }
}
