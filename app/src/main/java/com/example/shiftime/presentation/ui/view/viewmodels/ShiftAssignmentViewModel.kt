package com.example.shiftime.presentation.ui.view.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shiftime.domain.usecases.assignments.AssignEmployeeToShiftUseCase
import com.example.shiftime.domain.usecases.assignments.GetEmployeeWithAssignedShiftsUseCase
import com.example.shiftime.domain.usecases.assignments.GetShiftWithAssignedEmployeesUseCase
import com.example.shiftime.domain.usecases.assignments.RemoveEmployeeFromShiftUseCase
import com.example.shiftime.domain.usecases.assignments.UpdateAssignmentStatusUseCase
import com.example.shiftime.presentation.ui.common.state.ShiftAssignmentUiState
import com.example.shiftime.presentation.ui.events.modelevents.ShiftAssignmentEvent
import com.example.shiftime.presentation.ui.events.uievents.UiEvent
import com.example.shiftime.utils.enums.AssignmentStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShiftAssignmentViewModel @Inject constructor(
    private val assignEmployeeToShiftUseCase: AssignEmployeeToShiftUseCase,
    private val removeEmployeeFromShiftUseCase: RemoveEmployeeFromShiftUseCase,
    private val updateAssignmentStatusUseCase: UpdateAssignmentStatusUseCase,
    private val getShiftWithAssignedEmployeesUseCase: GetShiftWithAssignedEmployeesUseCase,
    private val getEmployeeWithAssignedShiftsUseCase: GetEmployeeWithAssignedShiftsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ShiftAssignmentUiState())
    val state: StateFlow<ShiftAssignmentUiState> = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: ShiftAssignmentEvent) {
        when (event) {
            is ShiftAssignmentEvent.AssignEmployeeToShift -> {
                assignEmployeeToShift(event.employeeId, event.shiftId, event.note)
            }
            is ShiftAssignmentEvent.RemoveEmployeeFromShift -> {
                removeEmployeeFromShift(event.employeeId, event.shiftId)
            }
            is ShiftAssignmentEvent.UpdateAssignmentStatus -> {
                updateAssignmentStatus(event.employeeId, event.shiftId, event.status)
            }
            is ShiftAssignmentEvent.LoadShiftWithEmployees -> {
                loadShiftWithEmployees(event.shiftId)
            }
            is ShiftAssignmentEvent.LoadEmployeeWithShifts -> {
                loadEmployeeWithShifts(event.employeeId)
            }
        }
    }

    private fun assignEmployeeToShift(employeeId: Long, shiftId: Long, note: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = assignEmployeeToShiftUseCase(employeeId, shiftId, note)

            result.fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false) }
                    _uiEvent.send(UiEvent.ShowSnackbar("העובד שובץ בהצלחה"))
                    // רענון נתוני המשמרת
                    loadShiftWithEmployees(shiftId)
                },
                onFailure = { exception ->
                    _state.update { it.copy(isLoading = false, error = exception.message) }
                    _uiEvent.send(UiEvent.ShowSnackbar(exception.message ?: "שגיאה בשיבוץ עובד"))
                }
            )
        }
    }

    private fun removeEmployeeFromShift(employeeId: Long, shiftId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = removeEmployeeFromShiftUseCase(employeeId, shiftId)

            result.fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false) }
                    _uiEvent.send(UiEvent.ShowSnackbar("העובד הוסר מהמשמרת בהצלחה"))
                    // רענון נתוני המשמרת
                    loadShiftWithEmployees(shiftId)
                },
                onFailure = { exception ->
                    _state.update { it.copy(isLoading = false, error = exception.message) }
                    _uiEvent.send(UiEvent.ShowSnackbar(exception.message ?: "שגיאה בהסרת עובד מהמשמרת"))
                }
            )
        }
    }

    private fun updateAssignmentStatus(employeeId: Long, shiftId: Long, status: AssignmentStatus) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = updateAssignmentStatusUseCase(employeeId, shiftId, status)

            result.fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false) }
                    _uiEvent.send(UiEvent.ShowSnackbar("סטטוס השיבוץ עודכן בהצלחה"))
                    // רענון נתוני המשמרת
                    loadShiftWithEmployees(shiftId)
                },
                onFailure = { exception ->
                    _state.update { it.copy(isLoading = false, error = exception.message) }
                    _uiEvent.send(UiEvent.ShowSnackbar(exception.message ?: "שגיאה בעדכון סטטוס שיבוץ"))
                }
            )
        }
    }

    private fun loadShiftWithEmployees(shiftId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getShiftWithAssignedEmployeesUseCase(shiftId).collect { shiftWithEmployees ->
                _state.update { it.copy(
                    isLoading = false,
                    currentShiftWithEmployees = shiftWithEmployees
                ) }
            }
        }
    }

    private fun loadEmployeeWithShifts(employeeId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getEmployeeWithAssignedShiftsUseCase(employeeId).collect { employeeWithShifts ->
                _state.update { it.copy(
                    isLoading = false,
                    currentEmployeeWithShifts = employeeWithShifts
                ) }
            }
        }
    }
}