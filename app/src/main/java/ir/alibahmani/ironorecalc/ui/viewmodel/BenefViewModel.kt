package ir.alibahmani.ironorecalc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.alibahmani.ironorecalc.data.local.dao.BenefDao
import ir.alibahmani.ironorecalc.data.local.entity.BenefDailyEntity
import ir.alibahmani.ironorecalc.data.local.entity.BenefSampleEntity
import ir.alibahmani.ironorecalc.domain.model.BenefCumulativeStats
import ir.alibahmani.ironorecalc.domain.model.BenefDailyReport
import ir.alibahmani.ironorecalc.domain.model.BenefSample
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BenefViewModel @Inject constructor(private val dao: BenefDao) : ViewModel() {

    val allSamples: StateFlow<List<BenefSample>> = dao.getAllSamples()
        .map { list -> list.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyReports: StateFlow<List<BenefDailyReport>> = dao.getAllDailyReports()
        .map { list -> list.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val distinctDates: StateFlow<List<String>> = dao.getDistinctSampleDates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * آمار تجمعی: عیار میانگین (Y) و بازیابی وزنی میانگین (Z) برای هر تاریخ.
     * بر اساس همان منطق اکسل: running average از ابتدا تا هر روز.
     */
    val cumulativeStats: StateFlow<List<BenefCumulativeStats>> = allSamples.map { samples ->
        val sorted = samples.sortedWith(compareBy({ it.date }, { it.rowNum }))
        val grouped = sorted.groupBy { it.date }.toSortedMap()
        var runTotalConc = 0.0
        var runTotalFeed = 0.0
        var runTotalMetalConc = 0.0
        var runTotalMetalFeed = 0.0
        grouped.map { (date, daySamples) ->
            daySamples.forEach { s ->
                if (s.feedWeight > 0) {
                    runTotalConc += s.concWeight
                    runTotalFeed += s.feedWeight
                    runTotalMetalConc += s.concWeight * s.concFe
                    runTotalMetalFeed += s.feedWeight * s.feedFe
                }
            }
            // عیار میانگین تا تاریخ = Σ(وزن محصول × Fe محصول) / Σوزن محصول
            val avgFe = if (runTotalConc > 0) runTotalMetalConc / runTotalConc else 0.0
            // بازیابی وزنی میانگین = Σوزن محصول / Σوزن خوراک
            val avgRecovery = if (runTotalFeed > 0) runTotalConc / runTotalFeed else 0.0
            BenefCumulativeStats(
                date = date,
                cumulativeAvgFe = avgFe,
                cumulativeAvgWeightRecovery = avgRecovery,
                monthToDateProdTon = runTotalConc
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveSample(s: BenefSample) {
        viewModelScope.launch {
            dao.insertSample(BenefSampleEntity(
                id = s.id, date = s.date, rowNum = s.rowNum,
                operatingHours = s.operatingHours, sampleCode = s.sampleCode,
                sampleType = s.sampleType, drumNumbers = s.drumNumbers,
                drumSpeed = s.drumSpeed, bladeAngle = s.bladeAngle,
                fieldStrength = s.fieldStrength, truckCount = s.truckCount,
                feedWeight = s.feedWeight, feedFe = s.feedFe, feedFeo = s.feedFeo,
                concWeight = s.concWeight, concFe = s.concFe, concFeo = s.concFeo,
                tailWeight = s.tailWeight, tailFe = s.tailFe, tailFeo = s.tailFeo,
                notes = s.notes
            ))
        }
    }

    fun saveDaily(r: BenefDailyReport) {
        viewModelScope.launch {
            dao.insertDaily(BenefDailyEntity(
                id = r.id, date = r.date,
                lims1 = r.lims1, lims2 = r.lims2, lims3 = r.lims3, lims4 = r.lims4,
                feedType = r.feedType,
                shiftDowntimeHr = r.shiftDowntimeHr, shiftRuntimeHr = r.shiftRuntimeHr,
                dailyProductionTon = r.dailyProductionTon,
                dailyWeighingTon = r.dailyWeighingTon,
                cumulativeProdTon = r.cumulativeProdTon,
                weighedCargoType = r.weighedCargoType,
                weighedCargoFe = r.weighedCargoFe,
                weighedCargoWeightTon = r.weighedCargoWeightTon,
                weighedCargoTruckCount = r.weighedCargoTruckCount,
                transferredWeightTon = r.transferredWeightTon,
                transferredFeo = r.transferredFeo,
                personnelOffice = r.personnelOffice,
                personnelSafetyExpert = r.personnelSafetyExpert,
                personnelExpert = r.personnelExpert,
                personnelTechnicalElec = r.personnelTechnicalElec,
                personnelTechnicalLabor = r.personnelTechnicalLabor,
                personnelMechanics = r.personnelMechanics,
                personnelServiceman = r.personnelServiceman,
                personnelLoaderDriver = r.personnelLoaderDriver,
                personnelTruckDriver = r.personnelTruckDriver,
                personnelKaraDriver = r.personnelKaraDriver,
                personnelWarehouse = r.personnelWarehouse,
                personnelSupply = r.personnelSupply,
                personnelBenefHead = r.personnelBenefHead,
                personnelCrusherWorker = r.personnelCrusherWorker,
                personnelTechnicalWorker = r.personnelTechnicalWorker,
                dieselConsumption = r.dieselConsumption,
                notes = r.notes
            ))
        }
    }

    fun deleteSample(id: Long) { viewModelScope.launch { dao.deleteSample(id) } }
    fun deleteDaily(id: Long)  { viewModelScope.launch { dao.deleteDaily(id) } }

    fun getSamplesByDate(date: String): StateFlow<List<BenefSample>> =
        dao.getSamplesByDate(date)
            .map { list -> list.map { it.toDomain() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun BenefSampleEntity.toDomain() = BenefSample(
        id = id, date = date, rowNum = rowNum, operatingHours = operatingHours,
        sampleCode = sampleCode, sampleType = sampleType, drumNumbers = drumNumbers,
        drumSpeed = drumSpeed, bladeAngle = bladeAngle, fieldStrength = fieldStrength,
        truckCount = truckCount, feedWeight = feedWeight, feedFe = feedFe, feedFeo = feedFeo,
        concWeight = concWeight, concFe = concFe, concFeo = concFeo,
        tailWeight = tailWeight, tailFe = tailFe, tailFeo = tailFeo, notes = notes
    )

    private fun BenefDailyEntity.toDomain() = BenefDailyReport(
        id = id, date = date,
        lims1 = lims1, lims2 = lims2, lims3 = lims3, lims4 = lims4,
        feedType = feedType, shiftDowntimeHr = shiftDowntimeHr, shiftRuntimeHr = shiftRuntimeHr,
        dailyProductionTon = dailyProductionTon, dailyWeighingTon = dailyWeighingTon,
        cumulativeProdTon = cumulativeProdTon,
        weighedCargoType = weighedCargoType, weighedCargoFe = weighedCargoFe,
        weighedCargoWeightTon = weighedCargoWeightTon, weighedCargoTruckCount = weighedCargoTruckCount,
        transferredWeightTon = transferredWeightTon, transferredFeo = transferredFeo,
        personnelOffice = personnelOffice, personnelSafetyExpert = personnelSafetyExpert,
        personnelExpert = personnelExpert, personnelTechnicalElec = personnelTechnicalElec,
        personnelTechnicalLabor = personnelTechnicalLabor, personnelMechanics = personnelMechanics,
        personnelServiceman = personnelServiceman, personnelLoaderDriver = personnelLoaderDriver,
        personnelTruckDriver = personnelTruckDriver, personnelKaraDriver = personnelKaraDriver,
        personnelWarehouse = personnelWarehouse, personnelSupply = personnelSupply,
        personnelBenefHead = personnelBenefHead, personnelCrusherWorker = personnelCrusherWorker,
        personnelTechnicalWorker = personnelTechnicalWorker,
        dieselConsumption = dieselConsumption, notes = notes
    )
}
