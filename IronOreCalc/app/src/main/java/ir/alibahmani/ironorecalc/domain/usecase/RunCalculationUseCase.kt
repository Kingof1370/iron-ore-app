package ir.alibahmani.ironorecalc.domain.usecase

import ir.alibahmani.ironorecalc.domain.model.CalculationInputs
import ir.alibahmani.ironorecalc.domain.model.CalculationResult
import javax.inject.Inject

/** Use case: validate inputs and run the full calculation. */
class RunCalculationUseCase @Inject constructor() {

    data class Result(
        val errors: List<String> = emptyList(),
        val result: CalculationResult? = null
    )

    operator fun invoke(inputs: CalculationInputs): Result {
        val errors = CalculationEngine.validateInputs(inputs)
        if (errors.isNotEmpty()) return Result(errors = errors)
        val result = CalculationEngine.runFullCalculation(inputs)
        return Result(result = result)
    }
}
