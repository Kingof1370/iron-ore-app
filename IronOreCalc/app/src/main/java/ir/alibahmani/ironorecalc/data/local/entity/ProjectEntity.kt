package ir.alibahmani.ironorecalc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity that persists a named calculation project.
 * Inputs and results are serialised as JSON strings via TypeConverters.
 */
@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Long,           // epoch ms
    val inputsJson: String,        // JSON of CalculationInputs
    val resultJson: String         // JSON of CalculationResult
)
