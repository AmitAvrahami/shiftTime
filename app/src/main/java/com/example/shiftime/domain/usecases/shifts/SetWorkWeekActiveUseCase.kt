package com.example.shiftime.domain.usecases

import com.example.shiftime.domain.model.WorkWeek
import com.example.shiftime.domain.repository.WorkWeekRepository
import javax.inject.Inject

/**
 * UseCase להפעלת שבוע עבודה מסוים
 * מבטל את כל השבועות הפעילים הקיימים ומגדיר את השבוע שנבחר כפעיל
 */
class SetWorkWeekActiveUseCase @Inject constructor(
    private val workWeekRepository: WorkWeekRepository
) {
    /**
     * מפעיל את השבוע שמזהה שלו הוא workWeekId
     * @param workWeekId מזהה שבוע העבודה שיופעל
     * @return תוצאה שמכילה את שבוע העבודה המופעל או שגיאה אם הפעולה נכשלה
     */
    suspend operator fun invoke(workWeekId: Long): Result<WorkWeek> {
        return try {
            val workWeek = workWeekRepository.getWorkWeekById(workWeekId)
                ?: return Result.failure(NoSuchElementException("לא נמצא שבוע עבודה עם מזהה $workWeekId"))

            workWeekRepository.deactivateAllWorkWeeks()

            workWeekRepository.setWorkWeekActive(workWeekId, true)

            val updatedWorkWeek = workWeek.copy(isActive = true)

            Result.success(updatedWorkWeek)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}