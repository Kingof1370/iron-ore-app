package ir.alibahmani.ironorecalc.domain.usecase

import ir.alibahmani.ironorecalc.data.repository.ProjectRepository
import javax.inject.Inject

class DeleteProjectUseCase @Inject constructor(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(projectId: Long) = repository.deleteProject(projectId)
}
