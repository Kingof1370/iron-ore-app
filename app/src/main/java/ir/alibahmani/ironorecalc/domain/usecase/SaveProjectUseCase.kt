package ir.alibahmani.ironorecalc.domain.usecase

import ir.alibahmani.ironorecalc.data.repository.ProjectRepository
import ir.alibahmani.ironorecalc.domain.model.CalculationInputs
import ir.alibahmani.ironorecalc.domain.model.CalculationResult
import ir.alibahmani.ironorecalc.domain.model.SavedProject
import java.util.Date
import javax.inject.Inject

class SaveProjectUseCase @Inject constructor(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(
        name: String,
        inputs: CalculationInputs,
        result: CalculationResult
    ): Long {
        val project = SavedProject(
            name = name,
            createdAt = Date(),
            inputs = inputs,
            result = result
        )
        return repository.saveProject(project)
    }
}
