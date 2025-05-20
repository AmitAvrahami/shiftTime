package com.example.shiftime.presentation.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.repository.WorkWeekRepository
import com.example.shiftime.domain.usecases.SetWorkWeekActiveUseCase
import com.example.shiftime.domain.usecases.shifts.CreateWorkWeekWithShiftsUseCase
import com.example.shiftime.domain.usecases.shifts.GetWorkWeekWithShiftsUseCase
import com.example.shiftime.domain.usecases.shifts.UpdateShiftUseCase
import com.example.shiftime.presentation.ui.common.state.ShiftUiState
import com.example.shiftime.presentation.ui.events.modelevents.ShiftEvent
import com.example.shiftime.presentation.ui.events.uievents.UiEvent
import com.example.shiftime.utils.enums.Days
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ShiftViewModel @Inject constructor(
    private val createWorkWeekWithShiftsUseCase: CreateWorkWeekWithShiftsUseCase,
    private val getWorkWeekWithShiftsUseCase: GetWorkWeekWithShiftsUseCase,
    private val updateShiftUseCase: UpdateShiftUseCase,
    private val workWeekRepository: WorkWeekRepository,
    private val setWorkWeekActiveUseCase: SetWorkWeekActiveUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ShiftUiState())
    val state: StateFlow<ShiftUiState> = _state.asStateFlow()

    private val _shifts = MutableStateFlow<List<Shift>>(emptyList())
    val shifts: StateFlow<List<Shift>> = _shifts.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadActiveWorkWeek()
        loadAllWorkWeeks()
    }

    fun onEvent(event: ShiftEvent) {
        when (event) {
            is ShiftEvent.SetStartDate -> {
                val isContainThisDate = _state.value.allWorkWeeks.any { it.startDate == event.date }
                if (isContainThisDate) sendUiEvent(UiEvent.ShowSnackbar("יש כבר שבוע עם תאריך זהה"))
                else setStartDate(event.date)
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
                    setStartDate(date)
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
        }
    }

    private fun setStartDate(date: LocalDate) {
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

                    // טעינה מחדש של כל השבועות
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

            updateShiftUseCase(shift)
                .onSuccess {
                    _state.update { it.copy(
                        isLoading = false,
                        error = null,
                        isEditDialogVisible = false,
                        currentEditShift = null
                    )}
                    sendUiEvent(UiEvent.ShowSnackbar("משמרת עודכנה בהצלחה"))

                    // רענון המשמרות לאחר העדכון
                    loadActiveWorkWeek()
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

    private fun loadActiveWorkWeek() {
        viewModelScope.launch {
            getWorkWeekWithShiftsUseCase.getActiveWorkWeekWithShifts().collect { weekWithShifts ->
                weekWithShifts?.let { data ->
                    _state.update { it.copy(
                        currentWorkWeek = data.workWeek,
                        startDate = data.workWeek.startDate
                    )}
                    _shifts.value = data.shifts
                }
            }
        }
    }

    private fun loadAllWorkWeeks() {
        viewModelScope.launch {
            workWeekRepository.getAllWorkWeeks().collectLatest { workWeeks ->
                _state.update { it.copy(allWorkWeeks = workWeeks) }
            }
        }
    }

    fun activateWorkWeek(workWeekId: Long) {
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

                    // רענון המשמרות של השבוע הפעיל
                    loadActiveWorkWeek()
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

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}