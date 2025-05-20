package com.example.shiftime.di

import com.example.shiftime.data.local.dao.EmployeeDao
import com.example.shiftime.data.local.dao.ShiftDao
import com.example.shiftime.data.local.dao.WorkWeekDao
import com.example.shiftime.data.local.repository.EmployeeRepositoryImp
import com.example.shiftime.data.repository.ShiftRepositoryImpl
import com.example.shiftime.data.repository.WorkWeekRepositoryImpl
import com.example.shiftime.domain.repository.EmployeeRepository
import com.example.shiftime.domain.repository.ShiftRepository
import com.example.shiftime.domain.repository.WorkWeekRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideShiftRepository(dao: ShiftDao): ShiftRepository {
        return ShiftRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun employeeRepository(dao:EmployeeDao): EmployeeRepository {
        return EmployeeRepositoryImp(dao)
    }

    @Provides
    @Singleton
    fun provideWorkWeekRepository(workWeekDao: WorkWeekDao): WorkWeekRepository {
        return WorkWeekRepositoryImpl(workWeekDao)
    }



}