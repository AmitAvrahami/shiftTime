package com.example.shiftime.di

import android.content.Context
import androidx.room.Room
import com.example.shiftime.data.local.dao.EmployeeConstraintDao
import com.example.shiftime.data.local.dao.EmployeeDao
import com.example.shiftime.data.local.dao.ShiftAssignmentDao
import com.example.shiftime.data.local.dao.ShiftDao
import com.example.shiftime.data.local.dao.WorkWeekDao
import com.example.shiftime.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDatabaseModule {

    @Provides
    fun provideShiftDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "shift_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideShiftDao(database: AppDatabase): ShiftDao {
        return database.shiftDao()
    }

  @Provides
  @Singleton
  fun provideEmployeeDao(database: AppDatabase): EmployeeDao {
      return database.employeeDao()
  }

    @Provides
    @Singleton
    fun provideWorkWeekDao(database: AppDatabase): WorkWeekDao {
        return database.workWeekDao()
    }

    @Provides
    @Singleton
    fun provideShiftAssignmentDao(database: AppDatabase): ShiftAssignmentDao {
        return database.shiftAssignmentDao()
    }

    @Provides
    @Singleton
    fun provideEmployeeConstraintDao(database: AppDatabase): EmployeeConstraintDao {
        return database.employeeConstraintDao()
    }

}