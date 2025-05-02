package com.example.shiftime.models

import java.util.Date

data class Employee(
    val id: String ="",
    val firstName: String="",
    val lastName: String="",
    val idNumber: String="",
    val email: String="",
    val phoneNumber: String="",
    val address: String="",
    val dateOfBirth: Date = Date(),
    val maxShifts: Int,
    val minShifts: Int,
    val totalWorkHoursLimit: Double = 40.0,
    val unavailableShifts: MutableList<Shift> = mutableListOf(),
    val role: Role = Role.REGULAR,
    val shifts: MutableList<Shift> = mutableListOf(),
    val workHours: List<Double> = emptyList(),
    ){
    override fun toString(): String {
        val builder = StringBuilder()

        builder.appendLine("=".repeat(60))
        builder.appendLine("🧑 עובד:")
        builder.appendLine("שם מלא: $firstName $lastName")
        builder.appendLine("תעודת זהות: $idNumber")
        builder.appendLine("אימייל: $email")
        builder.appendLine("טלפון: $phoneNumber")
        builder.appendLine("כתובת: $address")
        builder.appendLine("תאריך לידה: $dateOfBirth")
        builder.appendLine("תפקיד: ${role.name}")
        builder.appendLine("משמרות מקסימום: $maxShifts")
        builder.appendLine("משמרות מינימום: $minShifts")
        builder.appendLine("הגבלת שעות עבודה: $totalWorkHoursLimit שעות בשבוע")
        builder.appendLine("משמרות אסורות (${unavailableShifts.size}):")

        if (unavailableShifts.isEmpty()) {
            builder.appendLine("  אין מגבלות!")
        } else {
            for (shift in unavailableShifts) {
                builder.appendLine("  ❌ ${shift.shiftDay.name.lowercase().replaceFirstChar { it.uppercase() }} - משמרת ${shift.shiftType.name.lowercase()}")
            }
        }

        builder.appendLine("משמרות שובץ (${shifts.size}):")
        if (shifts.isEmpty()) {
            builder.appendLine("  עדיין לא שובץ למשמרות.")
        } else {
            for (shift in shifts) {
                builder.appendLine("  ✔️ ${shift.shiftDay.name.lowercase().replaceFirstChar { it.uppercase() }} - משמרת ${shift.shiftType.name.lowercase()}")
            }
        }

        builder.appendLine("=".repeat(60))
        return builder.toString()
    }

    override fun equals(other: Any?): Boolean {
        return id == (other as Employee).id
    }

    override fun hashCode(): Int {
        var result = maxShifts
        result = 31 * result + minShifts
        result = 31 * result + id.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + idNumber.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + dateOfBirth.hashCode()
        result = 31 * result + shifts.hashCode()
        result = 31 * result + workHours.hashCode()
        return result
    }
}
enum class Role {
    REGULAR,
    MANAGER,
}
