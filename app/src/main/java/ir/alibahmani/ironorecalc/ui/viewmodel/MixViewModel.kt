package ir.alibahmani.ironorecalc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.alibahmani.ironorecalc.data.local.dao.MixDao
import ir.alibahmani.ironorecalc.data.local.entity.MixEntryEntity
import ir.alibahmani.ironorecalc.domain.model.MixEntry
import ir.alibahmani.ironorecalc.domain.model.MixSummary
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MixViewModel @Inject constructor(private val dao: MixDao) : ViewModel() {

    val entries: StateFlow<List<MixEntry>> = dao.getAllEntries()
        .map { list -> list.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val summary: StateFlow<MixSummary> = combine(
        dao.totalFineWeight(),
        dao.totalFineMetalContent(),
        dao.totalCoarseWeight(),
        dao.totalCoarseMetalContent()
    ) { fw, fm, cw, cm ->
        MixSummary(
            totalFineWeightTon    = fw ?: 0.0,
            totalFineMetalContent = fm ?: 0.0,
            totalCoarseWeightTon  = cw ?: 0.0,
            totalCoarseMetalContent = cm ?: 0.0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MixSummary(0.0, 0.0, 0.0, 0.0))

    fun save(entry: MixEntry) {
        viewModelScope.launch {
            dao.insert(
                MixEntryEntity(
                    id          = entry.id,
                    date        = entry.date,
                    productCode = entry.productCode,
                    weightTon   = entry.weightTon,
                    fePercent   = entry.fePercent,
                    feoPercent  = entry.feoPercent
                )
            )
        }
    }

    fun delete(id: Long) { viewModelScope.launch { dao.deleteById(id) } }

    private fun MixEntryEntity.toDomain() = MixEntry(
        id          = id,
        date        = date,
        productCode = productCode,
        weightTon   = weightTon,
        fePercent   = fePercent,
        feoPercent  = feoPercent
    )
}
