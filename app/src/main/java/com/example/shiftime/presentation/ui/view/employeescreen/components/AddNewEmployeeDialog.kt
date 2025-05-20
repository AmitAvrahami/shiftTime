package com.example.shiftime.presentation.ui.view.employeescreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shiftime.data.local.entity.EmployeeEntity
import com.example.shiftime.presentation.ui.view.viewmodels.EmployeeViewModel
import com.example.shiftime.utils.enums.Role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewEmployeeDialog(
    employee: EmployeeEntity,
    onEmployeeChange: (EmployeeEntity) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    validateEmployeeInput: (EmployeeEntity) -> EmployeeFormState,
    employeeViewModel: EmployeeViewModel = hiltViewModel()
) {
    var expanded by remember { mutableStateOf(false) }
    var formErrors by remember { mutableStateOf(EmployeeFormState()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "הוספת עובד חדש",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // הודעת שגיאה כללית (אם יש)
                if (formErrors.hasGeneralError()) {
                    Text(
                        text = formErrors.generalError ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // First Name
                OutlinedTextField(
                    value = employee.firstName,
                    onValueChange = { firstName ->
                        onEmployeeChange(employee.copy(firstName = firstName))
                        // ניקוי שגיאות כשהמשתמש מתקן
                        if (firstName.isNotBlank() && formErrors.firstNameError != null) {
                            formErrors = formErrors.copy(firstNameError = null)
                        }
                    },
                    label = { Text("שם פרטי") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
                    singleLine = true,
                    isError = formErrors.firstNameError != null,
                    supportingText = {
                        if (formErrors.firstNameError != null) {
                            Text(
                                text = formErrors.firstNameError!!,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                )

                // Last Name
                OutlinedTextField(
                    value = employee.lastName,
                    onValueChange = { lastName ->
                        onEmployeeChange(employee.copy(lastName = lastName))
                        if (lastName.isNotBlank() && formErrors.lastNameError != null) {
                            formErrors = formErrors.copy(lastNameError = null)
                        }
                    },
                    label = { Text("שם משפחה") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
                    singleLine = true,
                    isError = formErrors.lastNameError != null,
                    supportingText = {
                        if (formErrors.lastNameError != null) {
                            Text(
                                text = formErrors.lastNameError!!,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                )

                // ID Number
                OutlinedTextField(
                    value = employee.idNumber,
                    onValueChange = { idNumber ->
                        onEmployeeChange(employee.copy(idNumber = idNumber))
                        if (idNumber.isNotBlank() && formErrors.idNumberError != null) {
                            formErrors = formErrors.copy(idNumberError = null)
                        }
                    },
                    label = { Text("תעודת זהות") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = formErrors.idNumberError != null,
                    supportingText = {
                        if (formErrors.idNumberError != null) {
                            Text(
                                text = formErrors.idNumberError!!,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                )

                // Phone
                OutlinedTextField(
                    value = employee.phoneNumber,
                    onValueChange = { phoneNumber ->
                        onEmployeeChange(employee.copy(phoneNumber = phoneNumber))
                        if ((phoneNumber.isBlank() || isValidPhoneNumber(phoneNumber)) &&
                            formErrors.phoneNumberError != null) {
                            formErrors = formErrors.copy(phoneNumberError = null)
                        }
                    },
                    label = { Text("מספר טלפון") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    isError = formErrors.phoneNumberError != null,
                    supportingText = {
                        if (formErrors.phoneNumberError != null) {
                            Text(
                                text = formErrors.phoneNumberError!!,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                )

                // Min Shifts
                OutlinedTextField(
                    value = employee.minShifts.toString(),
                    onValueChange = { minShifts ->
                        try {
                            val minShiftsValue = minShifts.toIntOrNull() ?: 0
                            onEmployeeChange(employee.copy(minShifts = minShiftsValue))
                            if (minShiftsValue >= 0 && formErrors.minShiftsError != null) {
                                formErrors = formErrors.copy(minShiftsError = null)
                            }

                            // בדיקת תלות בין מינימום ומקסימום
                            if (minShiftsValue <= employee.maxShifts &&
                                formErrors.maxShiftsError != null &&
                                formErrors.maxShiftsError!!.contains("מינימלי")) {
                                formErrors = formErrors.copy(maxShiftsError = null)
                            }
                        } catch (e: Exception) {
                            // שגיאה בהמרה - התעלם
                        }
                    },
                    label = { Text("מינימום משמרות") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = formErrors.minShiftsError != null,
                    supportingText = {
                        if (formErrors.minShiftsError != null) {
                            Text(
                                text = formErrors.minShiftsError!!,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                )

                // Max Shifts
                OutlinedTextField(
                    value = employee.maxShifts.toString(),
                    onValueChange = { maxShifts ->
                        try {
                            val maxShiftsValue = maxShifts.toIntOrNull() ?: 0
                            onEmployeeChange(employee.copy(maxShifts = maxShiftsValue))
                            if (maxShiftsValue >= employee.minShifts &&
                                formErrors.maxShiftsError != null) {
                                formErrors = formErrors.copy(maxShiftsError = null)
                            }
                        } catch (e: Exception) {
                            // שגיאה בהמרה - התעלם
                        }
                    },
                    label = { Text("מקסימום משמרות") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = formErrors.maxShiftsError != null,
                    supportingText = {
                        if (formErrors.maxShiftsError != null) {
                            Text(
                                text = formErrors.maxShiftsError!!,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                )

                // Role Selection with Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = employee.role,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("תפקיד") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        isError = formErrors.roleError != null,
                        supportingText = {
                            if (formErrors.roleError != null) {
                                Text(
                                    text = formErrors.roleError!!,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.exposedDropdownSize()
                    ) {
                        Role.entries.forEach { roleOption ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = roleOption.name,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                onClick = {
                                    onEmployeeChange(employee.copy(role = roleOption.name))
                                    if (formErrors.roleError != null) {
                                        formErrors = formErrors.copy(roleError = null)
                                    }
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // וולידציה מקומית לפני אישור
                    formErrors = validateEmployeeInput(employee)
                    if (!formErrors.hasErrors()) {
                        onConfirm()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("הוסף")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("ביטול")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shape = MaterialTheme.shapes.large,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}

// מחלקת עזר
data class EmployeeFormState(
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val idNumberError: String? = null,
    val phoneNumberError: String? = null,
    val minShiftsError: String? = null,
    val maxShiftsError: String? = null,
    val roleError: String? = null,
    val generalError: String? = null
) {
    fun hasErrors(): Boolean {
        return firstNameError != null ||
                lastNameError != null ||
                idNumberError != null ||
                phoneNumberError != null ||
                minShiftsError != null ||
                maxShiftsError != null ||
                roleError != null ||
                generalError != null
    }

    fun hasGeneralError(): Boolean {
        return generalError != null
    }

}

// פונקציית עזר לוולידציה של מספר טלפון
private fun isValidPhoneNumber(phone: String): Boolean {
    if (phone.isBlank()) return true // מספר טלפון ריק תקין
    // תבנית בסיסית למספר טלפון ישראלי
    val phonePattern = "^(\\+972|0)([23489]|5[02-9]|77)[0-9]{7}\$"
    return phone.matches(phonePattern.toRegex())
}

// פונקציית עזר לוולידציה של תעודת זהות
private fun isValidIdNumber(idNumber: String): Boolean {
    if (idNumber.isBlank()) return false
    // בדיקה פשוטה - 9 ספרות
    if (!idNumber.matches("^[0-9]{9}\$".toRegex())) return false

    // אלגוריתם לבדיקת ת.ז. ישראלית:
    var sum = 0
    for (i in 0 until 9) {
        var digit = idNumber[i].toString().toInt()
        if (i % 2 == 0) {
            digit *= 1
        } else {
            digit *= 2
            if (digit > 9) digit = digit % 10 + digit / 10
        }
        sum += digit
    }
    return sum % 10 == 0
}