package ir.alibahmani.ironorecalc.domain.usecase

import ir.alibahmani.ironorecalc.data.repository.ProjectRepository
import ir.alibahmani.ironorecalc.domain.model.SavedProject
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProjectsUseCase @Inject constructor(
    private val repository: ProjectRepository
) {
    operator fun invoke(): Flow<List<SavedProject>> = repository.getAllProjects()
}
