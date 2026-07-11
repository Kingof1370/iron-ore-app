package ir.alibahmani.ironorecalc.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.alibahmani.ironorecalc.data.local.dao.ProjectDao
import ir.alibahmani.ironorecalc.data.local.entity.ProjectEntity

@Database(
    entities = [ProjectEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
}
