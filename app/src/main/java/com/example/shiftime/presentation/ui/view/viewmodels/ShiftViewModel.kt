package com.example.shiftime.presentation.ui.view.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.usecases.SetWorkWeekActiveUseCase
import com.example.shiftime.domain.usecases.schedule.GetSchedulingDataUseCase
import com.example.shiftime.domain.usecases.schedule.SchedulingData
import com.example.shiftime.domain.usecases.shifts.CreateWorkWeekWithShiftsUseCase
import com.example.shiftime.domain.usecases.shifts.ExecuteShiftOperationUseCase
import com.example.shiftime.domain.usecases.shifts.GetAllWorkWeekUseCase
import com.example.shiftime.domain.usecases.shifts.GetShiftsUseCase
import com.example.shiftime.domain.usecases.shifts.command_pattern.ShiftOperations
import com.example.shiftime.presentation.ui.common.state.ShiftUiState
import com.example.shiftime.presentation.ui.events.modelevents.ShiftEvent
import com.example.shiftime.presentation.ui.events.uievents.UiEvent
import com.example.shiftime.utils.enums.Days
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ShiftViewModel @Inject constructor(
    private val getShiftsUseCase: GetShiftsUseCase,
    private val executeShiftOperationUseCase: ExecuteShiftOperationUseCase,
    private val createWorkWeekWithShiftsUseCase: CreateWorkWeekWithShiftsUseCase,
    private val setWorkWeekActiveUseCase: SetWorkWeekActiveUseCase,
    private val getAllWorkWeekUseCase: GetAllWorkWeekUseCase,
    private val getSchedulingDataUseCase: GetSchedulingDataUseCase


) : ViewModel() {

    // הוסף state לנתוני השיבוץ
    private val _schedulingData = MutableStateFlow<SchedulingData?>(null)
    val schedulingData: StateFlow<SchedulingData?> = _schedulingData.asStateFlow()

    // הוסף state לדיבוג
    private val _debugInfo = MutableStateFlow("")
    val debugInfo: StateFlow<String> = _debugInfo.asStateFlow()

    // הוסף state עבור שגיאות
    private val _dataExtractionError = MutableStateFlow<String?>(null)
    val dataExtractionError: StateFlow<String?> = _dataExtractionError.asStateFlow()

    private val _state = MutableStateFlow(ShiftUiState())
    val state: StateFlow<ShiftUiState> = _state.asStateFlow()

    private val _shifts = MutableStateFlow<List<Shift>>(emptyList())
    val shifts: StateFlow<List<Shift>> = _shifts.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var dataLoadJob : Job? = null
    private var activeWorkWeekJob: Job? = null

    init {
        loadActiveWorkWeek()
        loadAllWorkWeeks()
    }

    fun onEvent(event: ShiftEvent) {
        when (event) {
            is ShiftEvent.SetStartDate -> {
                val isContainThisDate = _state.value.allWorkWeeks.any { it.startDate == event.date }
                if (isContainThisDate) {
                    sendUiEvent(UiEvent.ShowSnackbar("יש כבר שבוע עם תאריך זהה"))
                } else {
                    createNewWorkWeek(event.date)
                }
            }
            is ShiftEvent.SetSelectedDay -> {
                _state.update { it.copy(selectedDay = Days.entries[event.dayIndex]) }
            }
            is ShiftEvent.SetAssignmentStyle -> {
                _state.update { it.copy(assignmentStyle = event.style) }
            }
            is ShiftEvent.UpdateShift -> {
                updateShift(event.shift)
            }
            is ShiftEvent.ShowEditShiftDialog -> {
                _state.update { it.copy(
                    isEditDialogVisible = true,
                    currentEditShift = event.shift
                )}
            }
            is ShiftEvent.HideEditShiftDialog -> {
                _state.update { it.copy(
                    isEditDialogVisible = false,
                    currentEditShift = null
                )}
            }
            is ShiftEvent.GenerateSchedule -> {
                _state.value.startDate?.let { date ->
                    createNewWorkWeek(date)
                } ?: run {
                    sendUiEvent(UiEvent.ShowSnackbar("יש לבחור תאריך התחלה תחילה"))
                }
            }
            is ShiftEvent.LoadActiveWorkWeek -> {
                loadActiveWorkWeek()
            }
            is ShiftEvent.ActivateWorkWeek -> {
                activateWorkWeek(event.workWeekId)
            }
            is ShiftEvent.RefreshData -> {
                refreshAllData()
            }
            is ShiftEvent.TestDataExtraction -> {
                testDataExtraction()
            }
        }

    }

    private fun createNewWorkWeek(date: LocalDate) {
        _state.update { it.copy(
            startDate = date,
            isLoading = true
        )}

        viewModelScope.launch {
            createWorkWeekWithShiftsUseCase(date)
                .onSuccess { result ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = null,
                        currentWorkWeek = result.workWeek
                    )}
                    _shifts.value = result.shifts
                    sendUiEvent(UiEvent.ShowSnackbar("נוצרו ${result.shifts.size} משמרות בהצלחה"))

                    loadAllWorkWeeks()
                }
                .onFailure { exception ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = exception.message
                    )}
                    sendUiEvent(UiEvent.ShowSnackbar("שגיאה: ${exception.message}"))
                }
        }
    }

    private fun updateShift(shift: Shift) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            executeShiftOperationUseCase.invoke(ShiftOperations.Update( // ✅ תיקון - execute במקום invoke
                shiftId = shift.id,
                updatedShift = shift
            ))
                .onSuccess { message ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = null,
                        isEditDialogVisible = false,
                        currentEditShift = null
                    )}
                    sendUiEvent(UiEvent.ShowSnackbar(message))

                    loadShiftsForCurrentWorkWeek()
                }
                .onFailure { exception ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = exception.message
                    )}
                    sendUiEvent(UiEvent.ShowSnackbar("שגיאה בעדכון המשמרת: ${exception.message}"))
                }
        }
    }

    private fun loadShiftsForWorkWeek(workWeekId: Long) {
        viewModelScope.launch {
            getShiftsUseCase.getShiftsByWorkWeek(workWeekId)
                .onSuccess { shifts ->
                    _shifts.value = shifts
                    _state.update { it.copy(
                        isLoading = false,
                        error = null
                    )}
                }
                .onFailure { error ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = error.message
                    )}
                    sendUiEvent(UiEvent.ShowSnackbar("שגיאה בטעינת משמרות: ${error.message}"))
                }
        }
    }

    private fun loadActiveWorkWeek() {
        activeWorkWeekJob?.cancel()
        activeWorkWeekJob = viewModelScope.launch {
            // כאן צריך UseCase שמחזיר את השבוע הפעיל
            // getActiveWorkWeekUseCase()
            //     .catch { error ->
            //         _state.update { it.copy(error = error.message) }
            //     }
            //     .collectLatest { activeWorkWeek ->
            //         _state.update { it.copy(currentWorkWeek = activeWorkWeek) }
            //         activeWorkWeek?.let { loadShiftsForWorkWeek(it.id) }
            //     }

            // ✅ זמנית - אם אין UseCase לשבוע פעיל, נטען את הראשון מהרשימה שפעיל
            _state.value.allWorkWeeks.firstOrNull { it.isActive }?.let { activeWorkWeek ->
                _state.update { it.copy(currentWorkWeek = activeWorkWeek) }
                loadShiftsForWorkWeek(activeWorkWeek.id)
            }
        }
    }

    private fun loadShiftsForCurrentWorkWeek() {
        _state.value.currentWorkWeek?.let { workWeek ->
            loadShiftsForWorkWeek(workWeek.id)
        }
    }


    private fun loadAllWorkWeeks() {
        dataLoadJob?.cancel()
        dataLoadJob = viewModelScope.launch {
            getAllWorkWeekUseCase()
                .catch { error ->
                    _state.update { it.copy(error = error.message) }
                    sendUiEvent(UiEvent.ShowSnackbar("שגיאה בטעינת שבועות: ${error.message}"))
                }
                .collectLatest { workWeeks ->
                    _state.update { it.copy(allWorkWeeks = workWeeks) }

                    if (_state.value.currentWorkWeek == null) {
                        workWeeks.firstOrNull { it.isActive }?.let { activeWorkWeek ->
                            _state.update { it.copy(currentWorkWeek = activeWorkWeek) }
                            loadShiftsForWorkWeek(activeWorkWeek.id)
                        }
                    }
                }
        }
    }

    private fun activateWorkWeek(workWeekId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            setWorkWeekActiveUseCase(workWeekId)
                .onSuccess { workWeek ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = null,
                        currentWorkWeek = workWeek
                    )}
                    sendUiEvent(UiEvent.ShowSnackbar("השבוע הופעל בהצלחה"))

                    loadShiftsForWorkWeek(workWeek.id)

                    loadAllWorkWeeks()
                }
                .onFailure { error ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = error.message
                    )}
                    sendUiEvent(UiEvent.ShowSnackbar("שגיאה בהפעלת השבוע: ${error.message}"))
                }
        }
    }


    private fun refreshAllData() {
        loadAllWorkWeeks()
        loadActiveWorkWeek()
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    /**
     * פונקציה לבדיקת חילוץ הנתונים
     */
    private fun testDataExtraction() {
        val currentWorkWeek = _state.value.currentWorkWeek
        if (currentWorkWeek == null) {
            _dataExtractionError.value = "לא נבחר שבוע עבודה"
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            _dataExtractionError.value = null

            // קריאה לחילוץ הנתונים
           val result = getSchedulingDataUseCase.invoke(currentWorkWeek.id).onSuccess {
               val data = it
               _schedulingData.value = data

               // יצירת מידע דיבוג
               val debugText = buildDebugInfo(data)
               _debugInfo.value = debugText

               // הודעת הצלחה
               sendUiEvent(UiEvent.ShowSnackbar("נתונים נחלצו בהצלחה!"))
           }.onFailure {
               val error = it.message ?: "שגיאה לא ידועה"
               _dataExtractionError.value = error
               sendUiEvent(UiEvent.ShowSnackbar("שגיאה: $error"))
           }

            _state.value = _state.value.copy(isLoading = false)
        }
    }

    /**
     * פונקציה ליצירת מידע דיבוג מהנתונים
     */
    private fun buildDebugInfo(data: SchedulingData): String {
        return buildString {
            appendLine("=== בדיקת נתוני השיבוץ ===")
            appendLine("שבוע עבודה: ${data.workWeek?.name ?: "לא ידוע"}")
            appendLine("תאריכים: ${data.workWeek?.startDate} - ${data.workWeek?.endDate}")
            appendLine()

            appendLine("📅 משמרות (${data.shifts.size}):")
            data.shifts.groupBy { it.shiftDay }.forEach { (day, dayShifts) ->
                appendLine("  ${day.label}:")
                dayShifts.forEach { shift ->
                    appendLine("    ${shift.shiftType.label}: ${shift.employeesRequired} עובדים")
                }
            }
            appendLine()

            appendLine("👥 עובדים (${data.employees.size}):")
            data.employees.forEach { employee ->
                val remaining = data.getRemainingShiftCapacity(employee.id)
                appendLine("  ${employee.firstName} ${employee.lastName}: יכול עוד $remaining משמרות")
            }
            appendLine()

            appendLine("🚫 אילוצים (${data.constraints.size}):")
            if (data.constraints.isEmpty()) {
                appendLine("  אין אילוצים מוגדרים")
            } else {
                data.constraints.groupBy { it.employeeId }.forEach { (employeeId, employeeConstraints) ->
                    val employee = data.employees.find { it.id == employeeId }
                    appendLine("  ${employee?.firstName} ${employee?.lastName}:")
                    employeeConstraints.forEach { constraint ->
                        val shift = data.shifts.find { it.id == constraint.shiftId }
                        appendLine("    ${shift?.shiftDay?.label} ${shift?.shiftType?.label}: ${if (constraint.canWork) "יכול" else "לא יכול"}")
                    }
                }
            }
            appendLine()

            appendLine("📋 שיבוצים קיימים (${data.existingAssignments.size}):")
            if (data.existingAssignments.isEmpty()) {
                appendLine("  אין שיבוצים קיימים")
            } else {
                data.existingAssignments.groupBy { it.shiftId }.forEach { (shiftId, assignments) ->
                    val shift = data.shifts.find { it.id == shiftId }
                    appendLine("  ${shift?.shiftDay?.label} ${shift?.shiftType?.label}:")
                    assignments.forEach { assignment ->
                        val employee = data.employees.find { it.id == assignment.employeeId }
                        appendLine("    ${employee?.firstName} ${employee?.lastName}")
                    }
                }
            }
            appendLine()

            // סיכום עמדות פתוחות
            val unfilledShifts = data.getUnfilledShifts()
            appendLine("🎯 משמרות שצריכות עובדים (${unfilledShifts.size}):")
            unfilledShifts.forEach { shift ->
                val missing = data.getMissingEmployeesCount(shift.id)
                val availableForShift = data.getAvailableEmployeesForShift(shift.id).size
                appendLine("  ${shift.shiftDay.label} ${shift.shiftType.label}: חסרים $missing, זמינים $availableForShift")
            }

            val availableEmployees = data.getAvailableEmployees()
            appendLine()
            appendLine("✅ עובדים זמינים להשמה (${availableEmployees.size}):")
            availableEmployees.forEach { employee ->
                val remaining = data.getRemainingShiftCapacity(employee.id)
                appendLine("  ${employee.firstName} ${employee.lastName}: יכול עוד $remaining משמרות")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        dataLoadJob?.cancel()
        activeWorkWeekJob?.cancel()
    }
}