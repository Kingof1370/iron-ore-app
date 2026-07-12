package ir.alibahmani.ironorecalc.domain.model

/**
 * یک ردیف ورود توزین روزانه (ریز دانه یا درشت دانه).
 * فرمول فلز محتوا:  metalContent = weightTon × fePercent
 */
data class MixEntry(
    val id: Long = 0,
    val date: String,            // مثال: 1404.07.12
    val productCode: String,     // "ریز دانه" | "درشت دانه"
    val weightTon: Double,       // وزن (تن)
    val fePercent: Double,       // Fe%
    val feoPercent: Double = 0.0 // FeO%
) {
    /** فلز محتوا = وزن × Fe% */
    val metalContent: Double get() = weightTon * fePercent
}

/** خلاصه میکس شده محاسبه‌شده از مجموع ردیف‌های توزین. */
data class MixSummary(
    val totalFineWeightTon: Double,
    val totalFineMetalContent: Double,
    val totalCoarseWeightTon: Double,
    val totalCoarseMetalContent: Double,
    val plannedFineCommitted: Double = 0.0,    // تعهد شده ریز
    val plannedCoarseCommitted: Double = 0.0   // تعهد شده درشت
) {
    /** مجموع وزن */
    val totalWeight: Double get() = totalFineWeightTon + totalCoarseWeightTon

    /** مجموع فلز محتوا */
    val totalMetalContent: Double get() = totalFineMetalContent + totalCoarseMetalContent

    /** عیار میکس شده = Σ(وزن × Fe%) / Σوزن */
    val mixedGrade: Double
        get() = if (totalWeight > 0) totalMetalContent / totalWeight else 0.0

    /** باقی‌مانده ریز = تعهد شده - توزین ریز */
    val remainingFine: Double get() = plannedFineCommitted - totalFineWeightTon

    /** باقی‌مانده درشت = تعهد شده - توزین درشت */
    val remainingCoarse: Double get() = plannedCoarseCommitted - totalCoarseWeightTon
}
