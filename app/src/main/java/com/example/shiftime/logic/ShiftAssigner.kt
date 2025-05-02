package com.example.shiftime.logic

import com.example.shiftime.models.ShiftSchedule

interface ShiftAssigner {
    fun assign(schedule: ShiftSchedule): Boolean

}