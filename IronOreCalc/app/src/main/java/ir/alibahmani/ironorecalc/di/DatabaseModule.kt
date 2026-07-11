package ir.alibahmani.ironorecalc.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.alibahmani.ironorecalc.data.local.dao.ProjectDao
import ir.alibahmani.ironorecalc.data.local.database.AppDatabase
import ir.alibahmani.ironorecalc.data.repository.ProjectRepository
import ir.alibahmani.ironorecalc.data.repository.ProjectRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "iron_ore_calc.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideProjectDao(db: AppDatabase): ProjectDao = db.projectDao()

    @Provides
    @Singleton
    fun provideProjectRepository(impl: ProjectRepositoryImpl): ProjectRepository = impl
}
