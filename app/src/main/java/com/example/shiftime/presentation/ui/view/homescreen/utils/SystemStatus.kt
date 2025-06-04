package com.example.shiftime.presentation.ui.view.homescreen.utils

import java.util.Date

data class SystemStatus(
    val title: String,
    val message: String,
    val level: StatusLevel,
    val timestamp: Date = Date()
)
