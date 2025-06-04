package com.example.shiftime.di

import com.example.shiftime.data.local.dao.EmployeeConstraintDao
import com.example.shiftime.data.local.dao.ShiftDao
import com.example.shiftime.data.local.repository.EmployeeConstraintRepositoryImpl
import com.example.shiftime.domain.repository.EmployeeConstraintRepository
import com.example.shiftime.domain.repository.EmployeeRepository
import com.example.shiftime.domain.repository.ShiftRepository
import com.example.shiftime.domain.repository.WorkWeekRepository
import com.example.shiftime.domain.usecases.employeeconstraints.DeleteEmployeeConstraintUseCase
import com.example.shiftime.domain.usecases.employeeconstraints.GetShiftsWithConstraintsByDayUseCase
import com.example.shiftime.domain.usecases.employeeconstraints.GetWorkWeekWithDaysUseCase
import com.example.shiftime.domain.usecases.employeeconstraints.SaveEmployeeConstraintUseCase
import com.example.shiftime.domain.usecases.employees.GetEmployeesUseCase
import com.example.shiftime.domain.usecases.employees.GetTodayActiveEmployeesUseCase
import com.example.shiftime.domain.usecases.homedata.GetScheduleStatusUseCase
import com.example.shiftime.domain.usecases.settings.GetCurrentUserUseCase
import com.example.shiftime.domain.usecases.settings.GetManagerMessagesUseCase
import com.example.shiftime.domain.usecases.settings.GetSystemStatusUseCase
import com.example.shiftime.domain.usecases.shifts.CreateWorkWeekWithShiftsUseCase
import com.example.shiftime.domain.usecases.shifts.ExecuteShiftOperationUseCase
import com.example.shiftime.domain.usecases.shifts.GetAllWorkWeekUseCase
import com.example.shiftime.domain.usecases.shifts.GetWorkWeekWithShiftsUseCase
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
    fun provideExecuteShiftOperationUseCase(
        shiftRepository: ShiftRepository
    ): ExecuteShiftOperationUseCase {
        return ExecuteShiftOperationUseCase(shiftRepository)
    }

    @Provides
    @Singleton
    fun provideEmployeeConstraintRepository(
        employeeConstraintDao: EmployeeConstraintDao,
        shiftDao: ShiftDao
    ): EmployeeConstraintRepository {
        return EmployeeConstraintRepositoryImpl(employeeConstraintDao, shiftDao)
    }

    @Provides
    @Singleton
    fun provideSaveEmployeeConstraintUseCase(
        repository: EmployeeConstraintRepository
    ): SaveEmployeeConstraintUseCase {
        return SaveEmployeeConstraintUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteEmployeeConstraintUseCase(
        repository: EmployeeConstraintRepository
    ): DeleteEmployeeConstraintUseCase {
        return DeleteEmployeeConstraintUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetShiftsWithConstraintsByDayUseCase(
        repository: EmployeeConstraintRepository
    ): GetShiftsWithConstraintsByDayUseCase {
        return GetShiftsWithConstraintsByDayUseCase(repository)
    }



//    @Provides
//    @Singleton
//    fun provideGetTodayShiftsWithEmployeesUseCase(
//        shiftRepository: ShiftRepository
//    ): GetTodayShiftsWithEmployeesUseCase {
//        return GetTodayShiftsWithEmployeesUseCase(shiftRepository)
//    }

//    @Provides
//    @Singleton
//    fun provideGetShiftsByDayUseCase(
//        shiftRepository: ShiftRepository
//    ): GetShiftsByDayUseCase {
//        return GetShiftsByDayUseCase(shiftRepository)
//    }

    @Provides
    @Singleton
    fun provideGetManagerMessagesUseCase(
    ): GetManagerMessagesUseCase {
        return GetManagerMessagesUseCase()
    }

    @Provides
    @Singleton
    fun provideGetTodayActiveEmployeesUseCase(
    ): GetTodayActiveEmployeesUseCase {
        return GetTodayActiveEmployeesUseCase(
        )
    }

    @Provides
    @Singleton
    fun provideGetSystemStatusUseCase(
    ): GetSystemStatusUseCase {
        return GetSystemStatusUseCase()
    }

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(
    ): GetCurrentUserUseCase {
        return GetCurrentUserUseCase()
    }

    @Provides
    @Singleton
    fun provideGetAllWorkWeekUseCase(workWeekRepository: WorkWeekRepository): GetAllWorkWeekUseCase {
        return GetAllWorkWeekUseCase(workWeekRepository)
    }

    @Provides
    @Singleton
    fun provideGetScheduleStatusUseCase(
        shiftRepository: ShiftRepository,
        getEmployeesUseCase: GetEmployeesUseCase,
        getWorkWeekWithShiftsUseCase: GetWorkWeekWithShiftsUseCase,
    ): GetScheduleStatusUseCase {
        return GetScheduleStatusUseCase(
            shiftRepository = shiftRepository,
            getEmployeesUseCase = getEmployeesUseCase,
            getWorkWeekWithShiftsUseCase = getWorkWeekWithShiftsUseCase
        )
    }
}