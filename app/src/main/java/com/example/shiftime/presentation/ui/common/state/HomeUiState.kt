package com.example.shiftime.presentation.ui.common.state

import com.example.shiftime.data.local.entity.ShiftWithEmployeesEntity
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.model.ScheduleStatus
import com.example.shiftime.domain.model.ShiftWithEmployees
import com.example.shiftime.presentation.ui.view.homescreen.utils.ManagerMessage
import com.example.shiftime.presentation.ui.view.homescreen.utils.SystemStatus

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val scheduleStatus: ScheduleStatus = ScheduleStatus(),
)