package ir.alibahmani.ironorecalc.utils

import android.content.Context
import ir.alibahmani.ironorecalc.domain.model.CalculationInputs
import ir.alibahmani.ironorecalc.domain.model.CalculationResult
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object CsvExporter {

    private val DATE_FORMAT = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    fun export(context: Context, projectName: String, inputs: CalculationInputs, result: CalculationResult): File {
        val file = File(context.cacheDir, "IronOre_${DATE_FORMAT.format(Date())}.csv")
        file.bufferedWriter(Charsets.UTF_8).use { w ->
            w.write("Iron Ore Industrial Calculator — Ali Bahmani — 09915420558\n")
            w.write("Project,$projectName\n")
            w.write("Date,${SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US).format(Date())}\n\n")

            w.write("INPUTS\n")
            w.write("Field,Value,Unit\n")
            w.write("نرخ خوراک ورودی,${inputs.feedRate},t/h\n")
            w.write("عیار کل آهن خوراک,${inputs.feedGrade},%\n")
            w.write("عیار محصول میانی,${inputs.midGrade},%\n")
            w.write("عیار باطله مرحله اول,${inputs.tailGrade1},%\n")
            w.write("شدت میدان درام اول,${inputs.h1},Gauss\n")
            w.write("دانه‌بندی D80,${inputs.d80},micron\n")
            w.write("رطوبت خوراک,${inputs.moisture},%\n")
            w.write("عیار کنسانتره نهایی,${inputs.finalConcGrade},%\n")
            w.write("عیار باطله نهایی,${inputs.finalTailGrade},%\n")
            w.write("شدت میدان درام دوم,${inputs.h2},Gauss\n")
            inputs.feO?.let { w.write("FeO,${it},%\n") }
            inputs.liberation?.let { w.write("Liberation,${it},%\n") }

            w.write("\nMASS BALANCE RESULTS\n")
            val mb = result.massBalance
            w.write("F2,%.2f,t/h\n".format(mb.f2))
            w.write("T1,%.2f,t/h\n".format(mb.t1))
            w.write("R1,%.2f,%\n".format(mb.r1))
            w.write("F3,%.2f,t/h\n".format(mb.f3))
            w.write("T2,%.2f,t/h\n".format(mb.t2))
            w.write("R2,%.2f,%\n".format(mb.r2))
            w.write("R_total,%.2f,%\n".format(mb.rTotal))
            w.write("Iron Balance Error,%.2f,%\n".format(result.ironBalance.errorAbs))

            w.write("\nPROCESS INDICATORS\n")
            w.write("Enrichment Ratio,%.2f,—\n".format(result.enrichmentRatio))
            w.write("Yield,%.2f,%\n".format(result.yieldPct))
            w.write("Newton Efficiency,%.2f,%\n".format(result.newton))
            w.write("Selectivity Index,%.2f,—\n".format(result.selectivityIndex))

            w.write("\nMULTI-CRITERIA\n")
            w.write("Overall Score,%.1f,/100\n".format(result.multiCriteria.overallScore))
            result.multiCriteria.breakdown.forEach { c ->
                w.write("${c.name},%.0f,/100\n".format(c.score))
            }

            w.write("\nWARNINGS\n")
            result.warnings.forEach { w.write("$it\n") }
            w.write("\nSUGGESTIONS\n")
            result.suggestions.forEach { w.write("$it\n") }
        }
        return file
    }
}
