package ir.alibahmani.ironorecalc.domain.model

/** Mass balance results from the two-stage separation. */
data class MassBalanceResult(
    val f2: Double,       // Mid-product flow rate, t/h
    val t1: Double,       // Stage-1 tail flow rate, t/h
    val r1: Double,       // Stage-1 iron recovery, %
    val f3: Double,       // Final concentrate flow rate, t/h
    val t2: Double,       // Final tail flow rate, t/h
    val r2: Double,       // Stage-2 iron recovery, %
    val rTotal: Double    // Overall iron recovery, %
)

/** Iron mass-balance check. */
data class IronBalanceResult(
    val feIn: Double,
    val feOut: Double,
    val feLoss: Double,
    val errorAbs: Double  // Absolute closure error, %
)

/** Approximate mineralogical classification. */
data class MineralClassification(
    val label: String,
    val reliable: Boolean
)

/** Drum critical speed and recommended operating range. */
data class DrumSpeedResult(
    val criticalSpeedRpm: Double,
    val recommendedMinRpm: Double,
    val recommendedMaxRpm: Double
)

data class FieldAdequacyResult(
    val status: String,               // "کافی" | "کم" | "بیش‌ازحد" | "نامشخص"
    val recommendedMinGauss: Double,
    val recommendedMaxGauss: Double,
    val basis: String
)

data class CriterionScore(
    val name: String,
    val score: Double,
    val weight: Int
)

data class MultiCriteriaResult(
    val overallScore: Double,
    val limitingFactor: String,
    val breakdown: List<CriterionScore>
)

/** Complete result bundle returned by [RunCalculationUseCase]. */
data class CalculationResult(
    val massBalance: MassBalanceResult,
    val ironBalance: IronBalanceResult,
    val mineral: MineralClassification,
    val enrichmentRatio: Double,
    val yieldPct: Double,
    val concentrationCriterion: Double?,
    val msr: Double?,
    val relativeMagneticDrivingIndex: Double?,
    val newton: Double,
    val gangueRecoveryToConc: Double,
    val selectivityIndex: Double,
    val multiCriteria: MultiCriteriaResult,
    val warnings: List<String>,
    val suggestions: List<String>,
    val drumSpeed1: DrumSpeedResult?,
    val drumSpeed2: DrumSpeedResult?,
    val beltSpeed1: Double?,
    val beltSpeed2: Double?,
    val fieldAdequacy1: FieldAdequacyResult,
    val fieldAdequacy2: FieldAdequacyResult
)
