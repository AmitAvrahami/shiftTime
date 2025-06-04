package com.example.shiftime.presentation.ui.view.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shiftime.data.local.mapper.toUiModel
import com.example.shiftime.domain.model.EmployeeConstraint
import com.example.shiftime.domain.usecases.employeeconstraints.DeleteEmployeeConstraintUseCase
import com.example.shiftime.domain.usecases.employeeconstraints.GetActiveWorkWeekUseCase
import com.example.shiftime.domain.usecases.employeeconstraints.GetWorkWeekWithDaysUseCase
import com.example.shiftime.domain.usecases.employeeconstraints.SaveEmployeeConstraintUseCase
import com.example.shiftime.domain.usecases.employees.FindEmployeeByIdUseCase
import com.example.shiftime.domain.usecases.employees.GetEmployeesUseCase
import com.example.shiftime.presentation.ui.common.state.EmployeeConstraintsState
import com.example.shiftime.presentation.ui.common.state.EmployeeUiModel
import com.example.shiftime.presentation.ui.common.state.EmployeesListState
import com.example.shiftime.presentation.ui.common.state.UiState
import com.example.shiftime.presentation.ui.events.modelevents.EmployeeConstraintsEvent
import com.example.shiftime.presentation.ui.events.uievents.UiEvent
import com.example.shiftime.utils.enums.Days
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EmployeeConstraintsViewModel @Inject constructor(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val getActiveWorkWeekUseCase: GetActiveWorkWeekUseCase,
    private val getWorkWeekWithDaysUseCase: GetWorkWeekWithDaysUseCase,
    private val saveEmployeeConstraintUseCase: SaveEmployeeConstraintUseCase,
    private val deleteEmployeeConstraintUseCase: DeleteEmployeeConstraintUseCase,
    private val findEmployeeByIdUseCase: FindEmployeeByIdUseCase
) : ViewModel() {

    // מצב UI לרשימת העובדים
    private val _employeesState = MutableStateFlow(EmployeesListState())
    val employeesState: StateFlow<EmployeesListState> = _employeesState.asStateFlow()

    // מצב UI למסך אילוצי העובד
    private val _constraintsState = MutableStateFlow(EmployeeConstraintsState())
    val constraintsState: StateFlow<EmployeeConstraintsState> = _constraintsState.asStateFlow()

    // אירועי UI חד פעמיים
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadEmployees()
        loadActiveWorkWeek()
    }

    private fun loadEmployees() {
        viewModelScope.launch(Dispatchers.IO) {
            _employeesState.update { it.copy(isLoading = true) }

            getEmployeesUseCase().collectLatest { employees ->
                _employeesState.update {
                    it.copy(
                        employees = employees.map { employee -> employee.toUiModel() },
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadActiveWorkWeek() {
        viewModelScope.launch {
            getActiveWorkWeekUseCase().collectLatest { workWeek ->
                _constraintsState.update {
                    it.copy(
                        activeWorkWeek = workWeek,
                        selectedWorkWeekId = workWeek?.id ?: 0L
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: EmployeeConstraintsEvent) {
        when (event) {
            is EmployeeConstraintsEvent.SelectEmployee -> selectEmployee(event.employeeId)
            is EmployeeConstraintsEvent.SelectDay -> selectDay(event.day)
            is EmployeeConstraintsEvent.ToggleCanWork -> toggleCanWork(event.shiftId, event.canWork, event.comment)
            is EmployeeConstraintsEvent.DeleteConstraint -> deleteConstraint(event.shiftId)
            is EmployeeConstraintsEvent.NavigateBack -> navigateBack()
            is EmployeeConstraintsEvent.ChangeMonth -> changeMonth(event.newDate)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun selectEmployee(employeeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            findEmployeeByIdUseCase(employeeId).collect{ res ->
                when(res){
                    is UiState.Success -> {
                        _constraintsState.update {
                            it.copy(
                                selectedEmployeeId = employeeId,
                                selectedEmployee = res.data,
                            )
                        }
                    }
                    is UiState.Error -> {
                        _constraintsState.update {
                            it.copy(
                                error = res.message,
                                isLoading = false
                            )
                        }
                    }
                    is UiState.Loading -> {
                        _constraintsState.update { it.copy(isLoading = true) }
                    }
                }
            }
            if (constraintsState.value.error != null) {
                sendUiEvent(UiEvent.ShowSnackbar(constraintsState.value.error ?: "שגיאה לא ידועה"))
                return@launch
            }
            loadWorkWeekDays(employeeId, _constraintsState.value.selectedWorkWeekId)
            _uiEvent.send(UiEvent.NavigateTo(employeeId.toString()))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadWorkWeekDays(employeeId: Long, workWeekId: Long) {
        viewModelScope.launch {
            _constraintsState.update { it.copy(isLoading = true) }

            getWorkWeekWithDaysUseCase(employeeId, workWeekId).collectLatest { days ->
                _constraintsState.update {
                    it.copy(
                        days = days,
                        isLoading = false
                    )
                }

                // בחר את היום הראשון כברירת מחדל אם אין יום נבחר
                if (constraintsState.value.selectedDay == null && days.isNotEmpty()) {
                    selectDay(days.first().day)
                }
            }
        }
    }

    private fun selectDay(day: Days) {
        _constraintsState.update { state ->
            val selectedDayWithShifts = state.days.find { it.day == day }
            state.copy(
                selectedDay = day,
                selectedDayShifts = selectedDayWithShifts?.shifts ?: emptyList()
            )
        }
    }

    private fun changeMonth(date: LocalDate) {
        _constraintsState.update { it.copy(selectedDate = date) }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun toggleCanWork(shiftId: Long, canWork: Boolean, comment: String?) {
        viewModelScope.launch {
            val employeeId = _constraintsState.value.selectedEmployeeId ?: return@launch

            val constraint = EmployeeConstraint(
                employeeId = employeeId,
                shiftId = shiftId,
                canWork = canWork,
                comment = comment
            )

            saveEmployeeConstraintUseCase.invoke(constraint).onSuccess {
                _constraintsState.update { state ->
                    state.copy(
                        selectedDayShifts = state.selectedDayShifts.map { shiftWithConstraint ->
                            if (shiftWithConstraint.shift.id == shiftId) {
                                shiftWithConstraint.copy(
                                    constraint = constraint,
                                )
                            } else {
                                shiftWithConstraint
                            }
                        }
                    )
                }

            }.onFailure {
                sendUiEvent(UiEvent.ShowSnackbar("שגיאה: ${it.message}"))
                return@launch
            }

            sendUiEvent(UiEvent.ShowSnackbar("האילוץ נשמר בהצלחה"))
        }
    }

    private fun deleteConstraint(shiftId: Long) {
        viewModelScope.launch {
            val employeeId = _constraintsState.value.selectedEmployeeId ?: return@launch

            deleteEmployeeConstraintUseCase(employeeId, shiftId)

            // רענן את המסך אחרי המחיקה
            val currentDay = _constraintsState.value.selectedDay
            if (currentDay != null) {
                selectDay(currentDay)
            }

            sendUiEvent(UiEvent.ShowSnackbar("האילוץ נמחק בהצלחה"))
        }
    }

    private fun navigateBack() {
        // אפס את מצב העובד הנבחר
        _constraintsState.update {
            it.copy(
                selectedEmployeeId = null,
                selectedEmployee = null,
                selectedDay = null,
                selectedDayShifts = emptyList()
            )
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}