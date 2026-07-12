package ir.alibahmani.ironorecalc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "benef_samples")
data class BenefSampleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val rowNum: Int,
    val operatingHours: Double,
    val sampleCode: String,
    val sampleType: String,
    val drumNumbers: String,
    val drumSpeed: Int,
    val bladeAngle: String,
    val fieldStrength: String,
    val truckCount: Int,
    val feedWeight: Double,
    val feedFe: Double,
    val feedFeo: Double,
    val concWeight: Double,
    val concFe: Double,
    val concFeo: Double,
    val tailWeight: Double,
    val tailFe: Double,
    val tailFeo: Double,
    val notes: String
)
