package ir.alibahmani.ironorecalc.data.local.dao

import androidx.room.*
import ir.alibahmani.ironorecalc.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: Long)

    @Query("SELECT COUNT(*) FROM projects")
    fun getProjectCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM projects WHERE createdAt >= :startOfDay")
    fun getTodayCount(startOfDay: Long): Flow<Int>

    @Query("SELECT * FROM projects WHERE name LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchProjects(query: String): Flow<List<ProjectEntity>>
}
