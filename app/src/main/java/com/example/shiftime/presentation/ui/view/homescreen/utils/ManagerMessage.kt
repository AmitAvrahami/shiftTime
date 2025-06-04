package com.example.shiftime.presentation.ui.view.homescreen.utils

import java.util.Date

data class ManagerMessage(
    val id: Long,
    val title: String,
    val content: String,
    val timestamp: Date,
    val isRead: Boolean = false,
    val priority: MessagePriority = MessagePriority.NORMAL
)