package ir.alibahmani.ironorecalc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mix_entries")
data class MixEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val productCode: String,
    val weightTon: Double,
    val fePercent: Double,
    val feoPercent: Double
)
