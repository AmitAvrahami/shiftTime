package com.example.shiftime.presentation.ui.events.uievents

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data class NavigateTo(val route: String) : UiEvent()
}