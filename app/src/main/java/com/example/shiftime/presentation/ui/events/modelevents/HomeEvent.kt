package com.example.shiftime.presentation.ui.events.modelevents

sealed class HomeEvent {
    object ViewCurrentSchedule : HomeEvent()
    object CreateNewSchedule : HomeEvent()
    object ViewAllMessages : HomeEvent()
    object RefreshData : HomeEvent()
}
