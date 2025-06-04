package com.example.shiftime.domain.usecases.employeeconstraints

import android.database.sqlite.SQLiteConstraintException
import com.example.shiftime.domain.model.EmployeeConstraint
import com.example.shiftime.domain.repository.EmployeeConstraintRepository
import com.example.shiftime.presentation.ui.common.state.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SaveEmployeeConstraintUseCase @Inject constructor(
    private val repository: EmployeeConstraintRepository
) {
    suspend operator fun invoke(constraint: EmployeeConstraint): Result<Unit> {
        return try {
            validateConstraint(constraint)
            repository.saveConstraint(constraint)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapErrorMessage(e))
        }
    }
}

private fun validateConstraint(constraint: EmployeeConstraint) {
    require(constraint.employeeId > 0) { "Employee ID לא תקין" }
    require(constraint.shiftId > 0) { "Shift ID לא תקין" }
    //TODO: Add more validations as needed
}

private fun mapErrorMessage(exception: Exception): Throwable {
    val message =  when (exception) {
        is IllegalArgumentException -> "נתונים לא תקינים: ${exception.message}"
        is SQLiteConstraintException -> "האילוץ כבר קיים במערכת"
        else -> exception.message ?: "שגיאה לא ידועה"
    }
    return Throwable(message)
}