package ir.alibahmani.ironorecalc.domain.model

import java.util.Date

/** A named saved calculation project stored in the Room database. */
data class SavedProject(
    val id: Long = 0,
    val name: String,
    val createdAt: Date,
    val inputs: CalculationInputs,
    val result: CalculationResult
)
