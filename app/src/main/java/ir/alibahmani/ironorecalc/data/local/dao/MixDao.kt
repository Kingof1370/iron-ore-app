package ir.alibahmani.ironorecalc.data.local.dao

import androidx.room.*
import ir.alibahmani.ironorecalc.data.local.entity.MixEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MixDao {
    @Query("SELECT * FROM mix_entries ORDER BY date DESC, id DESC")
    fun getAllEntries(): Flow<List<MixEntryEntity>>

    @Query("SELECT * FROM mix_entries WHERE productCode = :code ORDER BY date DESC")
    fun getByProduct(code: String): Flow<List<MixEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MixEntryEntity): Long

    @Delete
    suspend fun delete(entry: MixEntryEntity)

    @Query("DELETE FROM mix_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT SUM(weightTon) FROM mix_entries WHERE productCode = 'ریز دانه'")
    fun totalFineWeight(): Flow<Double?>

    @Query("SELECT SUM(weightTon) FROM mix_entries WHERE productCode = 'درشت دانه'")
    fun totalCoarseWeight(): Flow<Double?>

    @Query("SELECT SUM(weightTon * fePercent) FROM mix_entries WHERE productCode = 'ریز دانه'")
    fun totalFineMetalContent(): Flow<Double?>

    @Query("SELECT SUM(weightTon * fePercent) FROM mix_entries WHERE productCode = 'درشت دانه'")
    fun totalCoarseMetalContent(): Flow<Double?>
}
