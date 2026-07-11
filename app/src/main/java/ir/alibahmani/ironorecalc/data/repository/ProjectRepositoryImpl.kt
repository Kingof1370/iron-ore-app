package ir.alibahmani.ironorecalc.data.repository

import ir.alibahmani.ironorecalc.data.local.dao.ProjectDao
import ir.alibahmani.ironorecalc.data.local.entity.ProjectEntity
import ir.alibahmani.ironorecalc.domain.model.*
import ir.alibahmani.ironorecalc.utils.JsonSerializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val dao: ProjectDao
) : ProjectRepository {

    override fun getAllProjects(): Flow<List<SavedProject>> =
        dao.getAllProjects().map { list -> list.map { it.toDomain() } }

    override suspend fun saveProject(project: SavedProject): Long {
        val entity = ProjectEntity(
            id = project.id,
            name = project.name,
            createdAt = project.createdAt.time,
            inputsJson = JsonSerializer.encodeInputs(project.inputs),
            resultJson = JsonSerializer.encodeResult(project.result)
        )
        return dao.insertProject(entity)
    }

    override suspend fun deleteProject(projectId: Long) {
        dao.deleteProject(projectId)
    }

    override fun getProjectCount(): Flow<Int> = dao.getProjectCount()

    override fun getTodayCount(): Flow<Int> {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        return dao.getTodayCount(startOfDay)
    }

    private fun ProjectEntity.toDomain(): SavedProject = SavedProject(
        id = id,
        name = name,
        createdAt = Date(createdAt),
        inputs = JsonSerializer.decodeInputs(inputsJson),
        result = JsonSerializer.decodeResult(resultJson)
    )
}
