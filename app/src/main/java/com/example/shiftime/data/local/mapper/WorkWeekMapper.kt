package com.example.shiftime.data.local.mapper

import com.example.shiftime.data.local.entity.WorkWeekEntity
import com.example.shiftime.data.mapper.toDate
import com.example.shiftime.data.mapper.toLocalDate
import com.example.shiftime.domain.model.WorkWeek

    // מיפוי ישויות WorkWeek
    fun WorkWeekEntity.toDomain(): WorkWeek {
        return WorkWeek(
            id = id,
            name = name,
            startDate = startDate.toLocalDate(),
            endDate = endDate.toLocalDate(),
            isActive = isActive
        )
    }

    fun WorkWeek.toEntity(): WorkWeekEntity {
        return WorkWeekEntity(
            id = id,
            name = name,
            startDate = startDate.toDate(),
            endDate = endDate.toDate(),
            isActive = isActive
        )
    }

