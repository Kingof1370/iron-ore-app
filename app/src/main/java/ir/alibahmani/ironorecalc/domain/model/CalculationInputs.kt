package ir.alibahmani.ironorecalc.domain.model

/**
 * All inputs for the two-stage dry magnetic separation calculation.
 * Core fields (stage 1 + stage 2) are required; advanced and equipment
 * fields are optional.
 */
data class CalculationInputs(
    // --- Stage 1 ---
    val feedRate: Double,          // F1, t/h
    val feedGrade: Double,         // C1, %
    val midGrade: Double,          // C2, %
    val tailGrade1: Double,        // Ct1, %
    val h1: Double,                // Gauss
    val d80: Double,               // micron
    val moisture: Double,          // %

    // --- Stage 2 ---
    val finalConcGrade: Double,    // C3, %
    val finalTailGrade: Double,    // Ct2, %
    val h2: Double,                // Gauss

    // --- Advanced (optional) ---
    val feO: Double? = null,
    val fe2o3: Double? = null,
    val liberation: Double? = null,
    val rhoVal: Double? = null,
    val rhoGang: Double? = null,
    val rhoFluid: Double? = null,
    val chiVal: Double? = null,
    val chiGang: Double? = null,

    // --- Equipment (optional) ---
    val drumDiameter1: Double? = null,
    val beltWidth1: Double? = null,
    val bedThickness1: Double? = null,
    val drumDiameter2: Double? = null,
    val beltWidth2: Double? = null,
    val bedThickness2: Double? = null,
    val bulkDensity: Double? = null
)
