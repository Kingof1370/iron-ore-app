package ir.alibahmani.ironorecalc.data.repository

import ir.alibahmani.ironorecalc.domain.model.SavedProject
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAllProjects(): Flow<List<SavedProject>>
    suspend fun saveProject(project: SavedProject): Long
    suspend fun deleteProject(projectId: Long)
    fun getProjectCount(): Flow<Int>
    fun getTodayCount(): Flow<Int>
}
