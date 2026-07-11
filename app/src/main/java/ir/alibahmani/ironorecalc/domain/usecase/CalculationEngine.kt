package ir.alibahmani.ironorecalc.domain.usecase

import ir.alibahmani.ironorecalc.domain.model.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Pure-Kotlin port of lib/calculations.ts.
 * DO NOT change any mathematical logic, formulas, or numerical precision.
 */
object CalculationEngine {

    // --- Molar masses for stoichiometry ---
    private const val MOLAR_FE3O4 = 231.53
    private const val MOLAR_FEO = 71.845
    private const val MOLAR_FE2O3 = 159.69
    private const val RATIO_FE2O3_FROM_FETOTAL = 0.7773
    private const val RATIO_FE2O3_TO_FE = 1.4297

    // --- Mineral classification thresholds (% magnetite share) ---
    private const val MINERAL_TH_PURE_MAGNETITE = 95.0
    private const val MINERAL_TH_DOMINANT_MAGNETITE = 70.0
    private const val MINERAL_TH_MIXED = 30.0
    private const val MINERAL_TH_DOMINANT_HEMATITE = 5.0

    // --- Multi-criteria scoring reference ranges ---
    private const val SCORE_RTOTAL_MIN = 40.0
    private const val SCORE_RTOTAL_MAX = 90.0
    private const val SCORE_C3_TARGET = 65.0
    private const val SCORE_CT2_MAX_LOSS = 15.0
    private const val SCORE_R1_MIN = 50.0
    private const val SCORE_R1_MAX = 95.0
    private const val SCORE_R2_MIN = 50.0
    private const val SCORE_R2_MAX = 95.0
    private const val SCORE_ENRICH_MIN = 1.0
    private const val SCORE_ENRICH_MAX = 2.5
    private const val SCORE_LIBERATION_MIN = 50.0
    private const val SCORE_LIBERATION_MAX = 95.0
    private const val SCORE_D80_FINE = 75.0
    private const val SCORE_D80_COARSE = 300.0
    private const val SCORE_MOIST_MAX = 3.0
    private const val SCORE_IBE_MAX = 5.0

    // --- Automatic warning thresholds ---
    private const val WARN_CT2_MAX = 10.0
    private const val WARN_RTOTAL_MIN = 65.0
    private const val WARN_LIBERATION_MIN = 70.0
    private const val WARN_D80_MAX = 212.0
    private const val WARN_MOIST_MAX = 1.5
    private const val WARN_IBE_MAX = 3.0
    private const val WARN_H1_MIN_STAGE1 = 1000.0
    private const val WARN_H2_STAGE2_LOW = 1200.0
    private const val WARN_R2_LOW = 75.0

    // --- Suggested-adjustment thresholds ---
    private const val SUGG_RTOTAL_MIN = 75.0
    private const val SUGG_CT2_MAX = 8.0
    private const val SUGG_H1_MIN = 3500.0

    // --- Multi-criteria weights (expert-judgment based) ---
    private val WEIGHTS = mapOf(
        "rTotal" to 20, "c3" to 15, "ct2" to 15, "r1" to 10, "r2" to 10,
        "enrich" to 5, "liberation" to 10, "d80" to 5, "moist" to 5, "ibe" to 5
    )

    // --- Field intensity reference ranges ---
    private val FIELD_RANGE_MAGNETITE = 800.0 to 3000.0
    private val FIELD_RANGE_MIXED = 3000.0 to 8000.0
    private val FIELD_RANGE_HEMATITE = 8000.0 to 20000.0

    private fun bound(x: Double): Double = when {
        !x.isFinite() -> 0.0
        x < 0.0 -> 0.0
        x > 100.0 -> 100.0
        else -> x
    }

    fun clampPercent(value: Double): Double = when {
        !value.isFinite() -> 0.0
        value < 0.0 -> 0.0
        value > 100.0 -> 100.0
        else -> value
    }

    fun validateInputs(inputs: CalculationInputs): List<String> {
        val errors = mutableListOf<String>()

        val percentFields = listOf(
            "عیار کل آهن خوراک" to inputs.feedGrade,
            "عیار محصول میانی" to inputs.midGrade,
            "عیار باطله مرحله اول" to inputs.tailGrade1,
            "رطوبت خوراک" to inputs.moisture,
            "عیار کنسانتره نهایی" to inputs.finalConcGrade,
            "عیار باطله نهایی" to inputs.finalTailGrade
        )
        for ((label, value) in percentFields) {
            if (!value.isFinite() || value < 0 || value > 100) {
                errors.add("$label باید عددی بین ۰ تا ۱۰۰ باشد.")
            }
        }

        val optionalPercentFields = listOf(
            "درصد FeO" to inputs.feO,
            "درصد Fe2O3" to inputs.fe2o3,
            "شاخص آزادشدگی" to inputs.liberation
        )
        for ((label, value) in optionalPercentFields) {
            if (value == null) continue
            if (!value.isFinite() || value < 0 || value > 100) {
                errors.add("$label باید عددی بین ۰ تا ۱۰۰ باشد.")
            }
        }

        if (!inputs.feedRate.isFinite() || inputs.feedRate <= 0)
            errors.add("نرخ خوراک ورودی باید عددی بزرگ‌تر از صفر باشد.")
        if (!inputs.h1.isFinite() || inputs.h1 <= 0)
            errors.add("شدت میدان مغناطیسی درام اول باید بزرگ‌تر از صفر باشد.")
        if (!inputs.h2.isFinite() || inputs.h2 <= 0)
            errors.add("شدت میدان مغناطیسی درام دوم باید بزرگ‌تر از صفر باشد.")
        if (!inputs.d80.isFinite() || inputs.d80 <= 0)
            errors.add("دانه‌بندی D80 باید بزرگ‌تر از صفر باشد.")
        if (inputs.midGrade == inputs.tailGrade1)
            errors.add("عیار محصول میانی و عیار باطله مرحله اول نمی‌توانند برابر باشند.")
        if (inputs.finalConcGrade == inputs.finalTailGrade)
            errors.add("عیار کنسانتره نهایی و عیار باطله نهایی نمی‌توانند برابر باشند.")

        val positiveOptional = listOf(
            "چگالی کانی باارزش" to inputs.rhoVal,
            "چگالی گانگ" to inputs.rhoGang,
            "چگالی سیال جداسازی" to inputs.rhoFluid,
            "قطر درام مرحله اول" to inputs.drumDiameter1,
            "عرض نوار مرحله اول" to inputs.beltWidth1,
            "ضخامت بستر مرحله اول" to inputs.bedThickness1,
            "قطر درام مرحله دوم" to inputs.drumDiameter2,
            "عرض نوار مرحله دوم" to inputs.beltWidth2,
            "ضخامت بستر مرحله دوم" to inputs.bedThickness2,
            "چگالی توده خوراک" to inputs.bulkDensity
        )
        for ((label, value) in positiveOptional) {
            if (value == null) continue
            if (!value.isFinite() || value <= 0)
                errors.add("$label باید عددی بزرگ‌تر از صفر باشد.")
        }

        return errors
    }

    fun classifyMineralApprox(
        feTotal: Double,
        feO: Double,
        fe2o3Total: Double? = null
    ): MineralClassification {
        val fe2o3T = if (fe2o3Total != null && fe2o3Total >= 0) fe2o3Total
        else (feTotal - RATIO_FE2O3_FROM_FETOTAL * feO) * RATIO_FE2O3_TO_FE

        val magnetitePct = feO * (MOLAR_FE3O4 / MOLAR_FEO)
        val fe2o3InMag = feO * (MOLAR_FE2O3 / MOLAR_FEO)
        val fe2o3InHem = max(0.0, fe2o3T - fe2o3InMag)
        val hematitePct = fe2o3InHem

        if (magnetitePct + hematitePct <= 0)
            return MineralClassification("نامشخص (ورودی FeO/Fe2O3 نامعتبر)", false)

        val magRatio = (magnetitePct / (magnetitePct + hematitePct)) * 100

        val label = when {
            magRatio > MINERAL_TH_PURE_MAGNETITE -> "برآورد تقریبی: مگنتیت خالص"
            magRatio > MINERAL_TH_DOMINANT_MAGNETITE -> "برآورد تقریبی: مگنتیت غالب"
            magRatio > MINERAL_TH_MIXED -> "برآورد تقریبی: مخلوط مگنتیت-هماتیت"
            magRatio > MINERAL_TH_DOMINANT_HEMATITE -> "برآورد تقریبی: هماتیت غالب"
            else -> "برآورد تقریبی: هماتیت خالص"
        }
        return MineralClassification(label, true)
    }

    fun computeMassBalance(inputs: CalculationInputs): MassBalanceResult {
        val f1 = inputs.feedRate; val c1 = inputs.feedGrade
        val c2 = inputs.midGrade; val ct1 = inputs.tailGrade1
        val c3 = inputs.finalConcGrade; val ct2 = inputs.finalTailGrade

        val f2 = (f1 * (c1 - ct1)) / (c2 - ct1)
        val t1 = f1 - f2
        val r1 = ((f2 * c2) / (f1 * c1)) * 100

        val f3 = (f2 * (c2 - ct2)) / (c3 - ct2)
        val t2 = f2 - f3
        val r2 = ((f3 * c3) / (f2 * c2)) * 100

        val rTotal = (r1 * r2) / 100

        return MassBalanceResult(f2, t1, r1, f3, t2, r2, rTotal)
    }

    fun computeIronBalance(
        f1: Double, c1: Double, f3: Double, c3: Double,
        t1: Double, ct1: Double, t2: Double, ct2: Double
    ): IronBalanceResult {
        val feIn = (f1 * c1) / 100
        val feOut = (f3 * c3) / 100
        val feLoss = (t1 * ct1 + t2 * ct2) / 100
        val errorAbs = abs(((feIn - feOut - feLoss) / feIn) * 100)
        return IronBalanceResult(feIn, feOut, feLoss, errorAbs)
    }

    fun computeConcentrationCriterion(
        rhoVal: Double?, rhoGang: Double?, rhoFluid: Double?
    ): Double? {
        if (rhoVal == null || rhoGang == null || rhoFluid == null) return null
        if (rhoGang == rhoFluid) return null
        return (rhoVal - rhoFluid) / (rhoGang - rhoFluid)
    }

    fun computeMSR(chiVal: Double?, chiGang: Double?): Double? {
        if (chiVal == null || chiGang == null) return null
        if (chiGang == 0.0) return null
        return chiVal / chiGang
    }

    fun computeRelativeMagneticDrivingIndex(chiVal: Double?, h: Double?): Double? {
        if (chiVal == null || h == null) return null
        return chiVal * h * h
    }

    fun computeEnrichmentRatio(c3: Double, c1: Double): Double = c3 / c1

    fun computeYield(f3: Double, f1: Double): Double = (f3 / f1) * 100

    fun computeNewtonEfficiency(
        f1: Double, c1: Double, f3: Double, c3: Double, rTotal: Double
    ): Pair<Double, Double> {
        val gangueRecoveryToConc = ((f3 * (1 - c3 / 100)) / (f1 * (1 - c1 / 100))) * 100
        return (rTotal - gangueRecoveryToConc) to gangueRecoveryToConc
    }

    fun computeSelectivityIndex(c3: Double, ct2: Double): Double =
        (c3 * (100 - ct2)) / (ct2 * (100 - c3))

    fun computeDrumSpeed(diameterM: Double): DrumSpeedResult? {
        if (!diameterM.isFinite() || diameterM <= 0) return null
        val criticalSpeedRpm = 42.3 / sqrt(diameterM)
        return DrumSpeedResult(
            criticalSpeedRpm,
            criticalSpeedRpm * 0.15,
            criticalSpeedRpm * 0.35
        )
    }

    fun computeBeltSpeed(
        throughputTph: Double,
        beltWidthM: Double?,
        bedThicknessMm: Double?,
        bulkDensityTm3: Double?
    ): Double? {
        if (beltWidthM == null || bedThicknessMm == null || bulkDensityTm3 == null) return null
        if (!throughputTph.isFinite() || throughputTph <= 0 ||
            beltWidthM <= 0 || bedThicknessMm <= 0 || bulkDensityTm3 <= 0
        ) return null
        val thicknessM = bedThicknessMm / 1000.0
        return throughputTph / (60.0 * beltWidthM * thicknessM * bulkDensityTm3)
    }

    fun assessFieldAdequacy(mineralLabel: String, fieldGauss: Double): FieldAdequacyResult {
        val (range, basis) = when {
            mineralLabel.contains("مگنتیت") ->
                FIELD_RANGE_MAGNETITE to "بازه مرجع صنعتی جداکننده‌های شدت‌پایین (LIMS) برای مگنتیت"
            mineralLabel.contains("هماتیت") ->
                FIELD_RANGE_HEMATITE to "بازه مرجع صنعتی جداکننده‌های شدت‌بالا برای هماتیت (کانی ضعیف مغناطیسی)"
            else ->
                FIELD_RANGE_MIXED to "بازه میانی مرجع صنعتی (کانی‌شناسی خوراک وارد نشده یا نامشخص است)"
        }

        if (!fieldGauss.isFinite() || fieldGauss <= 0) {
            return FieldAdequacyResult("نامشخص", range.first, range.second, basis)
        }
        val status = when {
            fieldGauss < range.first -> "کم"
            fieldGauss > range.second -> "بیش‌ازحد"
            else -> "کافی"
        }
        return FieldAdequacyResult(status, range.first, range.second, basis)
    }

    fun multiCriteriaAnalysis(
        rTotal: Double, c3: Double, ct2: Double, r1: Double, r2: Double,
        enrichmentRatio: Double, liberation: Double?, d80: Double,
        moisture: Double, ironBalanceErrorAbs: Double
    ): MultiCriteriaResult {
        val criteria = mutableListOf(
            CriterionScore(
                "بازیابی کلی آهن",
                bound(((rTotal - SCORE_RTOTAL_MIN) / (SCORE_RTOTAL_MAX - SCORE_RTOTAL_MIN)) * 100),
                WEIGHTS["rTotal"]!!
            ),
            CriterionScore(
                "عیار کنسانتره نسبت به هدف صنعتی",
                bound((c3 / SCORE_C3_TARGET) * 100),
                WEIGHTS["c3"]!!
            ),
            CriterionScore(
                "عیار باطله نهایی (کمینه اتلاف)",
                bound(100 - (ct2 / SCORE_CT2_MAX_LOSS) * 100),
                WEIGHTS["ct2"]!!
            ),
            CriterionScore(
                "بازیابی مرحله اول",
                bound(((r1 - SCORE_R1_MIN) / (SCORE_R1_MAX - SCORE_R1_MIN)) * 100),
                WEIGHTS["r1"]!!
            ),
            CriterionScore(
                "بازیابی مرحله دوم",
                bound(((r2 - SCORE_R2_MIN) / (SCORE_R2_MAX - SCORE_R2_MIN)) * 100),
                WEIGHTS["r2"]!!
            ),
            CriterionScore(
                "نسبت غنی‌سازی",
                bound(((enrichmentRatio - SCORE_ENRICH_MIN) / (SCORE_ENRICH_MAX - SCORE_ENRICH_MIN)) * 100),
                WEIGHTS["enrich"]!!
            ),
            CriterionScore(
                "دانه‌بندی D80",
                bound(100 - (max(0.0, d80 - SCORE_D80_FINE) / (SCORE_D80_COARSE - SCORE_D80_FINE)) * 100),
                WEIGHTS["d80"]!!
            ),
            CriterionScore(
                "رطوبت خوراک",
                bound(100 - (moisture / SCORE_MOIST_MAX) * 100),
                WEIGHTS["moist"]!!
            ),
            CriterionScore(
                "صحت موازنه آهن",
                bound(100 - (ironBalanceErrorAbs / SCORE_IBE_MAX) * 100),
                WEIGHTS["ibe"]!!
            )
        )

        if (liberation != null) {
            criteria.add(
                CriterionScore(
                    "شاخص آزادشدگی",
                    bound(((liberation - SCORE_LIBERATION_MIN) / (SCORE_LIBERATION_MAX - SCORE_LIBERATION_MIN)) * 100),
                    WEIGHTS["liberation"]!!
                )
            )
        }

        val totalWeight = criteria.sumOf { it.weight }
        val overallScore = criteria.sumOf { it.score * it.weight } / totalWeight

        var biggestDrop = -1.0
        var limitingFactor = ""
        for (c in criteria) {
            val drop = (c.weight.toDouble() / totalWeight) * (100 - c.score)
            if (drop > biggestDrop) {
                biggestDrop = drop
                limitingFactor = c.name
            }
        }

        return MultiCriteriaResult(overallScore, limitingFactor, criteria)
    }

    fun autoWarnings(
        ct2: Double, rTotal: Double, r2: Double, h1: Double, h2: Double,
        liberation: Double?, d80: Double, moisture: Double, ironBalanceErrorAbs: Double
    ): List<String> {
        val warnings = mutableListOf<String>()
        if (ct2 > WARN_CT2_MAX) warnings.add("آهن باطله نهایی بالاست (%.2f٪).".format(ct2))
        if (rTotal < WARN_RTOTAL_MIN) warnings.add("بازیابی کلی آهن پایین است (%.2f٪).".format(rTotal))
        if (r2 < WARN_R2_LOW && h2 <= WARN_H2_STAGE2_LOW) warnings.add("عملکرد درام دوم ضعیف است.")
        if (liberation != null && liberation < WARN_LIBERATION_MIN)
            warnings.add("شاخص آزادشدگی پایین است (%.2f٪).".format(liberation))
        if (d80 > WARN_D80_MAX) warnings.add("دانه‌بندی D80 درشت است.")
        if (moisture > WARN_MOIST_MAX) warnings.add("رطوبت خوراک بالاست (%.2f٪).".format(moisture))
        if (h1 < WARN_H1_MIN_STAGE1) warnings.add("شدت میدان درام اول برای جداسازی درشت مگنتیتی پایین است.")
        if (ironBalanceErrorAbs > WARN_IBE_MAX) warnings.add("خطای موازنه آهن بیش از %.0f٪ است.".format(WARN_IBE_MAX))
        return warnings
    }

    fun suggestAdjustments(
        rTotal: Double, ct2: Double, h1: Double, liberation: Double?, d80: Double, moisture: Double
    ): List<String> {
        val suggestions = mutableListOf<String>()
        if (rTotal < SUGG_RTOTAL_MIN)
            suggestions.add("کاهش ضخامت بستر مرحله اول، کاهش سرعت نوار یا کاهش ظرفیت خوراک")
        if (ct2 > SUGG_CT2_MAX)
            suggestions.add("کاهش ضخامت خوراک، کاهش ظرفیت خوراک یا تنظیم فاصله خوراک تا درام")
        if (h1 < SUGG_H1_MIN)
            suggestions.add("افزایش شدت میدان درام اول در صورت امکان")
        if (liberation != null && liberation < WARN_LIBERATION_MIN)
            suggestions.add("افزایش آسیاب برای بهبود شاخص آزادشدگی")
        if (d80 > WARN_D80_MAX)
            suggestions.add("تنظیم سرعت درام آسیا یا کاهش D80")
        if (moisture > WARN_MOIST_MAX)
            suggestions.add("کاهش رطوبت خوراک با خشک‌کن پیش از جداسازی")
        return suggestions
    }

    fun runFullCalculation(inputs: CalculationInputs): CalculationResult {
        val massBalance = computeMassBalance(inputs)
        val ironBalance = computeIronBalance(
            inputs.feedRate, inputs.feedGrade,
            massBalance.f3, inputs.finalConcGrade,
            massBalance.t1, inputs.tailGrade1,
            massBalance.t2, inputs.finalTailGrade
        )

        val mineral = if (inputs.feO != null)
            classifyMineralApprox(inputs.feedGrade, inputs.feO, inputs.fe2o3)
        else MineralClassification("ورودی FeO وارد نشده (برای طبقه‌بندی لازم است)", false)

        val enrichmentRatio = computeEnrichmentRatio(inputs.finalConcGrade, inputs.feedGrade)
        val yieldPct = computeYield(massBalance.f3, inputs.feedRate)
        val cc = computeConcentrationCriterion(inputs.rhoVal, inputs.rhoGang, inputs.rhoFluid)
        val msr = computeMSR(inputs.chiVal, inputs.chiGang)
        val rmdi1 = computeRelativeMagneticDrivingIndex(inputs.chiVal, inputs.h1)
        val (newton, gangueRecovery) = computeNewtonEfficiency(
            inputs.feedRate, inputs.feedGrade,
            massBalance.f3, inputs.finalConcGrade, massBalance.rTotal
        )
        val selectivity = computeSelectivityIndex(inputs.finalConcGrade, inputs.finalTailGrade)
        val multiCriteria = multiCriteriaAnalysis(
            massBalance.rTotal, inputs.finalConcGrade, inputs.finalTailGrade,
            massBalance.r1, massBalance.r2, enrichmentRatio, inputs.liberation,
            inputs.d80, inputs.moisture, ironBalance.errorAbs
        )
        val warnings = autoWarnings(
            inputs.finalTailGrade, massBalance.rTotal, massBalance.r2,
            inputs.h1, inputs.h2, inputs.liberation, inputs.d80,
            inputs.moisture, ironBalance.errorAbs
        )
        val suggestions = suggestAdjustments(
            massBalance.rTotal, inputs.finalTailGrade, inputs.h1,
            inputs.liberation, inputs.d80, inputs.moisture
        )
        val drumSpeed1 = inputs.drumDiameter1?.let { computeDrumSpeed(it) }
        val drumSpeed2 = inputs.drumDiameter2?.let { computeDrumSpeed(it) }
        val beltSpeed1 = computeBeltSpeed(massBalance.f2, inputs.beltWidth1, inputs.bedThickness1, inputs.bulkDensity)
        val beltSpeed2 = computeBeltSpeed(massBalance.f3, inputs.beltWidth2, inputs.bedThickness2, inputs.bulkDensity)
        val fieldAdequacy1 = assessFieldAdequacy(mineral.label, inputs.h1)
        val fieldAdequacy2 = assessFieldAdequacy(mineral.label, inputs.h2)

        return CalculationResult(
            massBalance, ironBalance, mineral, enrichmentRatio, yieldPct,
            cc, msr, rmdi1, newton, gangueRecovery, selectivity, multiCriteria,
            warnings, suggestions, drumSpeed1, drumSpeed2, beltSpeed1, beltSpeed2,
            fieldAdequacy1, fieldAdequacy2
        )
    }
}
