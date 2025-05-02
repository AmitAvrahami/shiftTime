package com.example.shiftime.ui.view.constraintscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shiftime.models.ShiftType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConstraintsViewModel : ViewModel(){

    private val _state = MutableStateFlow(ConstraintsUiState())
    val state : MutableStateFlow<ConstraintsUiState> = _state

    fun selectEmployee(employeeId: String){
        _state.update { it.copy(selectedEmployeeId = employeeId) }
    }

    fun toggleUnavailableShift(date: String, shiftType: ShiftType) {
        val currentSet = state.value.unavailableShifts
        val shift = UnavailableShift(date, shiftType)
        val updatedSet = if (shift in currentSet) currentSet - shift else currentSet + shift
        _state.update { it.copy(unavailableShifts = updatedSet) }
    }

    fun submitConstraints() {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, submissionSuccess = null) }

            // סימולציית שליחה
            delay(1000)
            printState()

            _state.update {
                it.copy(
                    isSubmitting = false,
                    submissionSuccess = true
                )
            }
        }
    }

    fun printState() {
        println("current state:${state.value}")
    }

    fun isShiftUnavailable(date: String, shiftType: ShiftType): Boolean {
        return UnavailableShift(date, shiftType) in _state.value.unavailableShifts
    }

    }