package ir.alibahmani.ironorecalc.data.local.dao

import androidx.room.*
import ir.alibahmani.ironorecalc.data.local.entity.BenefDailyEntity
import ir.alibahmani.ironorecalc.data.local.entity.BenefSampleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BenefDao {

    // ── Samples ─────────────────────────────────────────────────────────────
    @Query("SELECT * FROM benef_samples ORDER BY date DESC, rowNum ASC")
    fun getAllSamples(): Flow<List<BenefSampleEntity>>

    @Query("SELECT * FROM benef_samples WHERE date = :date ORDER BY rowNum ASC")
    fun getSamplesByDate(date: String): Flow<List<BenefSampleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSample(sample: BenefSampleEntity): Long

    @Query("DELETE FROM benef_samples WHERE id = :id")
    suspend fun deleteSample(id: Long)

    @Query("SELECT DISTINCT date FROM benef_samples ORDER BY date DESC")
    fun getDistinctSampleDates(): Flow<List<String>>

    // ── Daily Reports ────────────────────────────────────────────────────────
    @Query("SELECT * FROM benef_daily ORDER BY date DESC")
    fun getAllDailyReports(): Flow<List<BenefDailyEntity>>

    @Query("SELECT * FROM benef_daily WHERE date = :date LIMIT 1")
    suspend fun getDailyByDate(date: String): BenefDailyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDaily(report: BenefDailyEntity): Long

    @Query("DELETE FROM benef_daily WHERE id = :id")
    suspend fun deleteDaily(id: Long)

    @Query("SELECT SUM(dailyProductionTon) FROM benef_daily")
    fun totalProduction(): Flow<Double?>
}
