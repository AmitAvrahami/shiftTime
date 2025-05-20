package com.example.shiftime.di

import com.example.shiftime.domain.repository.ShiftRepository
import com.example.shiftime.domain.repository.WorkWeekRepository
import com.example.shiftime.domain.usecases.shifts.CreateWorkWeekWithShiftsUseCase
import com.example.shiftime.domain.usecases.shifts.GetShiftsForDayUseCase
import com.example.shiftime.domain.usecases.shifts.GetWorkWeekWithShiftsUseCase
import com.example.shiftime.domain.usecases.shifts.UpdateShiftUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShiftModule {


    @Provides
    @Singleton
    fun provideGenerateWeeklyShiftsUseCase(
        shiftRepository: ShiftRepository,
        workWeekRepository: WorkWeekRepository
    ): CreateWorkWeekWithShiftsUseCase {
        return CreateWorkWeekWithShiftsUseCase(
            shiftRepository = shiftRepository,
            workWeekRepository = workWeekRepository
        )
    }

    @Provides
    @Singleton
    fun provideGetWorkWeekWithShiftsUseCase(
        shiftRepository: ShiftRepository,
        workWeekRepository: WorkWeekRepository
    ): GetWorkWeekWithShiftsUseCase {
        return GetWorkWeekWithShiftsUseCase(
            shiftRepository = shiftRepository,
            workWeekRepository = workWeekRepository
        )
    }


    @Provides
    @Singleton
    fun provideUpdateShiftUseCase(
        shiftRepository: ShiftRepository
    ): UpdateShiftUseCase {
        return UpdateShiftUseCase(shiftRepository)
    }



    @Provides
    @Singleton
    fun provideGetShiftsForDayUseCase(
        shiftRepository: ShiftRepository
    ): GetShiftsForDayUseCase {
        return GetShiftsForDayUseCase(shiftRepository)
    }
}