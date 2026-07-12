package ir.alibahmani.ironorecalc.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.alibahmani.ironorecalc.data.local.dao.BenefDao
import ir.alibahmani.ironorecalc.data.local.dao.MixDao
import ir.alibahmani.ironorecalc.data.local.dao.ProjectDao
import ir.alibahmani.ironorecalc.data.local.entity.BenefDailyEntity
import ir.alibahmani.ironorecalc.data.local.entity.BenefSampleEntity
import ir.alibahmani.ironorecalc.data.local.entity.MixEntryEntity
import ir.alibahmani.ironorecalc.data.local.entity.ProjectEntity

@Database(
    entities = [
        ProjectEntity::class,
        MixEntryEntity::class,
        BenefSampleEntity::class,
        BenefDailyEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun mixDao(): MixDao
    abstract fun benefDao(): BenefDao
}
