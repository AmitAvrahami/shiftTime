package com.example.shiftime.presentation.ui.view.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shiftime.data.local.entity.ShiftEntity
import com.example.shiftime.data.local.entity.ShiftWithEmployeesEntity
import com.example.shiftime.data.local.mapper.toEntity
import com.example.shiftime.domain.model.ScheduleStatus
import com.example.shiftime.domain.model.WorkWeek
import com.example.shiftime.domain.usecases.employeeconstraints.GetActiveWorkWeekUseCase
import com.example.shiftime.domain.usecases.employees.GetEmployeesUseCase
import com.example.shiftime.domain.usecases.employees.GetTodayActiveEmployeesUseCase
import com.example.shiftime.domain.usecases.homedata.GetScheduleStatusUseCase
import com.example.shiftime.domain.usecases.settings.GetCurrentUserUseCase
import com.example.shiftime.domain.usecases.settings.GetManagerMessagesUseCase
import com.example.shiftime.domain.usecases.settings.GetSystemStatusUseCase
import com.example.shiftime.presentation.ui.common.state.HomeUiState
import com.example.shiftime.presentation.ui.common.state.UiState
import com.example.shiftime.presentation.ui.events.modelevents.HomeEvent
import com.example.shiftime.presentation.ui.events.uievents.UiEvent
import com.example.shiftime.presentation.ui.navigation.ShiftTimeDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getScheduleStatusUseCase: GetScheduleStatusUseCase
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var dataLoadJob : Job? = null
    private var timeUpdateJob : Job? = null

    init {
        loadHomeData()
        startTimeUpdater()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ViewCurrentSchedule -> viewCurrentSchedule()
            is HomeEvent.CreateNewSchedule -> createNewSchedule()
            is HomeEvent.ViewAllMessages -> viewAllMessages()
            is HomeEvent.RefreshData -> loadHomeData()
        }
    }

    private fun loadHomeData() {
        dataLoadJob?.cancel()
        dataLoadJob = viewModelScope.launch {
            _homeState.update { it.copy(isLoading = true,error = null) }

            getScheduleStatusUseCase().catch { exception ->
                _homeState.update {
                    it.copy(
                        isLoading = false,
                        error = "שגיאה בטעינת נתונים: ${exception.message}"
                    )
                }
                sendUiEvent(UiEvent.ShowSnackbar("שגיאה בטעינת נתונים"))
            }.collect { scheduleStatus ->
                    _homeState.update { it.copy(
                        isLoading = false,
                        scheduleStatus = scheduleStatus
                    ) }

                }
        }
    }

    private fun startTimeUpdater() {
        timeUpdateJob?.cancel()
        timeUpdateJob = viewModelScope.launch {
            while (true) {
                val currentTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

                _homeState.update {state -> state.copy(
                    scheduleStatus = state.scheduleStatus.copy(time = currentTime)
                ) }

                kotlinx.coroutines.delay(60_000)
            }
        }
    }


    private fun viewCurrentSchedule() {
        sendUiEvent(UiEvent.NavigateTo("shifts/current_schedule"))
    }

    private fun createNewSchedule() {
        sendUiEvent(UiEvent.NavigateTo(ShiftTimeDestinations.CREATE_SCHEDULE))
    }

    private fun viewAllMessages() {
        sendUiEvent(UiEvent.NavigateTo("messages"))
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.trySend(event)
        }
    }

    // Helper functions
    private fun calculateActiveShifts(workWeek: WorkWeek): Int {
        // TODO: Implement actual calculation
        return 12 // Placeholder
    }

    private fun calculatePendingAssignments(workWeek: WorkWeek): Int {
        // TODO: Implement actual calculation
        return 3 // Placeholder
    }

    override fun onCleared() {
        super.onCleared()
        dataLoadJob?.cancel()
        timeUpdateJob?.cancel()
    }
}