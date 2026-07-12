package ir.alibahmani.ironorecalc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "benef_daily")
data class BenefDailyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val lims1: Boolean,
    val lims2: Boolean,
    val lims3: Boolean,
    val lims4: Boolean,
    val feedType: String,
    val shiftDowntimeHr: Double,
    val shiftRuntimeHr: Double,
    val dailyProductionTon: Double,
    val dailyWeighingTon: Double,
    val cumulativeProdTon: Double,
    val weighedCargoType: String,
    val weighedCargoFe: Double,
    val weighedCargoWeightTon: Double,
    val weighedCargoTruckCount: Int,
    val transferredWeightTon: Double,
    val transferredFeo: Double,
    val personnelOffice: Int,
    val personnelSafetyExpert: Int,
    val personnelExpert: Int,
    val personnelTechnicalElec: Int,
    val personnelTechnicalLabor: Int,
    val personnelMechanics: Int,
    val personnelServiceman: Int,
    val personnelLoaderDriver: Int,
    val personnelTruckDriver: Int,
    val personnelKaraDriver: Int,
    val personnelWarehouse: Int,
    val personnelSupply: Int,
    val personnelBenefHead: Int,
    val personnelCrusherWorker: Int,
    val personnelTechnicalWorker: Int,
    val dieselConsumption: Double,
    val notes: String
)
