package com.example.shiftime.presentation.ui.view.employeeconstraintsscreen

sealed class WorkAvailability {
    object CanWork : WorkAvailability()
    object CannotWork : WorkAvailability()
    object NoPreference : WorkAvailability()
}