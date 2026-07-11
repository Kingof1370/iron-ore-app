package ir.alibahmani.ironorecalc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.alibahmani.ironorecalc.domain.model.CalculationInputs
import ir.alibahmani.ironorecalc.domain.model.CalculationResult
import ir.alibahmani.ironorecalc.domain.usecase.RunCalculationUseCase
import ir.alibahmani.ironorecalc.domain.usecase.SaveProjectUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CalculationUiState(
    val form: Map<String, String> = emptyMap(),
    val errors: List<String> = emptyList(),
    val result: CalculationResult? = null,
    val isCalculating: Boolean = false,
    val saveSuccess: Boolean = false,
    val saveError: String? = null
)

@HiltViewModel
class CalculationViewModel @Inject constructor(
    private val runCalculation: RunCalculationUseCase,
    private val saveProject: SaveProjectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculationUiState())
    val uiState: StateFlow<CalculationUiState> = _uiState.asStateFlow()

    fun setField(key: String, value: String) {
        _uiState.value = _uiState.value.copy(
            form = _uiState.value.form + (key to value)
        )
    }

    fun calculate() {
        val form = _uiState.value.form

        val missingCore = CORE_FIELDS.filter { form[it].isNullOrBlank() }
        if (missingCore.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                errors = listOf("لطفاً همه فیلدهای اصلی (علامت‌دار با *) را تکمیل کنید."),
                result = null
            )
            return
        }

        val inputs = buildInputs(form)
        _uiState.value = _uiState.value.copy(isCalculating = true)

        val calcResult = runCalculation(inputs)
        _uiState.value = _uiState.value.copy(
            isCalculating = false,
            errors = calcResult.errors,
            result = calcResult.result
        )
    }

    fun reset() {
        _uiState.value = CalculationUiState()
    }

    fun saveProject(projectName: String) {
        val inputs = buildInputs(_uiState.value.form)
        val result = _uiState.value.result ?: return
        viewModelScope.launch {
            try {
                saveProject(projectName, inputs, result)
                _uiState.value = _uiState.value.copy(saveSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(saveError = e.message ?: "خطای ناشناخته")
            }
        }
    }

    fun clearSaveStatus() {
        _uiState.value = _uiState.value.copy(saveSuccess = false, saveError = null)
    }

    private fun buildInputs(form: Map<String, String>): CalculationInputs {
        fun d(key: String) = form[key]?.toDoubleOrNull() ?: Double.NaN
        fun dOpt(key: String) = form[key]?.toDoubleOrNull()
        return CalculationInputs(
            feedRate = d("feedRate"),
            feedGrade = d("feedGrade"),
            midGrade = d("midGrade"),
            tailGrade1 = d("tailGrade1"),
            h1 = d("h1"),
            d80 = d("d80"),
            moisture = d("moisture"),
            finalConcGrade = d("finalConcGrade"),
            finalTailGrade = d("finalTailGrade"),
            h2 = d("h2"),
            feO = dOpt("feO"),
            fe2o3 = dOpt("fe2o3"),
            liberation = dOpt("liberation"),
            rhoVal = dOpt("rhoVal"),
            rhoGang = dOpt("rhoGang"),
            rhoFluid = dOpt("rhoFluid"),
            chiVal = dOpt("chiVal"),
            chiGang = dOpt("chiGang"),
            drumDiameter1 = dOpt("drumDiameter1"),
            beltWidth1 = dOpt("beltWidth1"),
            bedThickness1 = dOpt("bedThickness1"),
            drumDiameter2 = dOpt("drumDiameter2"),
            beltWidth2 = dOpt("beltWidth2"),
            bedThickness2 = dOpt("bedThickness2"),
            bulkDensity = dOpt("bulkDensity")
        )
    }

    companion object {
        val CORE_FIELDS = listOf(
            "feedRate", "feedGrade", "midGrade", "tailGrade1",
            "h1", "d80", "moisture", "finalConcGrade", "finalTailGrade", "h2"
        )
    }
}
