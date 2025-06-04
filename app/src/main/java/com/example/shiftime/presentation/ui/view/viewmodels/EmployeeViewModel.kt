package com.example.shiftime.presentation.ui.view.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shiftime.data.local.entity.EmployeeEntity
import com.example.shiftime.data.local.mapper.toEntity
import com.example.shiftime.data.local.mapper.toUiModel
import com.example.shiftime.domain.usecases.employees.AddNewEmployeeUseCase
import com.example.shiftime.domain.usecases.employees.DeleteEmployeeUseCase
import com.example.shiftime.domain.usecases.employees.FindEmployeeByIdUseCase
import com.example.shiftime.domain.usecases.employees.GetEmployeesUseCase
import com.example.shiftime.domain.usecases.employees.UpdateEmployeeUseCase
import com.example.shiftime.presentation.ui.common.state.UiState
import com.example.shiftime.presentation.ui.events.uievents.UiEvent
import com.example.shiftime.presentation.ui.view.employeescreen.EmployeeEvent
import com.example.shiftime.presentation.ui.view.employeescreen.EmployeeUiState
import com.example.shiftime.presentation.ui.view.employeescreen.components.EmployeeFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject



@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val updateEmployeeUseCase: UpdateEmployeeUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase,
    private val addEmployeeUseCase: AddNewEmployeeUseCase,
    private val findEmployeeByIdUseCase: FindEmployeeByIdUseCase
): ViewModel() {

    private var _state: MutableStateFlow<EmployeeUiState> = MutableStateFlow(EmployeeUiState())
    val state: StateFlow<EmployeeUiState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    init {
        getEmployees()
    }

    fun onEvent(event: EmployeeEvent) {
        when (event) {
            is EmployeeEvent.AddEmployee -> {
                addEmployee(event.employee)
            }

            is EmployeeEvent.DeleteEmployee -> {
                viewModelScope.launch {
                    deleteEmployeeById(event.employee.id)
                    Log.d("EmployeeViewModel", "Deleted employee with ID: ${event.employee}")
                }
            }

            is EmployeeEvent.HideAddEmployeeDialog -> {
                _state.value = state.value.copy(
                    isAddingEmployee = false
                )
            }

            is EmployeeEvent.ShowAddEmployeeDialog -> {
                _state.value = state.value.copy(
                    isAddingEmployee = true
                )
            }

            is EmployeeEvent.UpdateEmployee -> {
                updateEmployee(event.employee)
            }

            is EmployeeEvent.ToggleEmployeeDetails -> {
                _state.value = state.value.copy(
                    employees = state.value.employees.map { employee ->
                        if (employee.employeeId == event.employeeId) {
                            employee.copy(isExpanded = !employee.isExpanded)
                        } else {
                            employee
                        }
                    }
                )
            }

            is EmployeeEvent.ShowEditDialog -> {
                _state.value = state.value.copy(
                    isEditingEmployee = true,
                    employeeToEdit = event.employee.toEntity()
                )
            }

            is EmployeeEvent.HideEditDialog -> {
                _state.value = state.value.copy(
                    isEditingEmployee = false,
                    employeeToEdit = null
                )
            }

            is EmployeeEvent.ShowDeleteConfirmDialog -> {
                _state.value = state.value.copy(
                    showDeleteConfirmDialog = true,
                    employeeToDelete = event.employee
                )
            }

            is EmployeeEvent.HideDeleteConfirmDialog -> {
                _state.value = state.value.copy(
                    showDeleteConfirmDialog = false,
                    employeeToDelete = null
                )

            }
        }
    }

    // בViewModel
    fun validateEmployeeInput(employee: EmployeeEntity): EmployeeFormState {
        val errors = mutableMapOf<String, String>()

        if (employee.firstName.isBlank()) {
            errors["firstNameError"] = "שם פרטי לא יכול להיות ריק"
        }

        if (employee.lastName.isBlank()) {
            errors["lastNameError"] = "שם משפחה לא יכול להיות ריק"
        }

        if (employee.idNumber.isBlank()) {
            errors["idNumberError"] = "תעודת זהות לא יכולה להיות ריקה"
        } else if (!isValidIdNumber(employee.idNumber)) {
            errors["idNumberError"] = "תעודת זהות לא תקינה"
        }

        if (employee.phoneNumber.isNotBlank() && !isValidPhoneNumber(employee.phoneNumber)) {
            errors["phoneNumberError"] = "מספר טלפון לא תקין"
        }

        if (employee.minShifts < 0) {
            errors["minShiftsError"] = "מספר המשמרות המינימלי חייב להיות חיובי"
        }

        if (employee.maxShifts < employee.minShifts) {
            errors["maxShiftsError"] = "מספר המשמרות המקסימלי חייב להיות גדול או שווה למינימלי"
        }

        return EmployeeFormState(
            firstNameError = errors["firstNameError"],
            lastNameError = errors["lastNameError"],
            idNumberError = errors["idNumberError"],
            phoneNumberError = errors["phoneNumberError"],
            minShiftsError = errors["minShiftsError"],
            maxShiftsError = errors["maxShiftsError"],
            roleError = errors["roleError"]
        )
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        if (phone.isBlank()) return true // מספר טלפון ריק תקין
        // תבנית בסיסית למספר טלפון ישראלי
        val phonePattern = "^(\\+972|0)([23489]|5[02-9]|77)[0-9]{7}\$"
        return phone.matches(phonePattern.toRegex())
    }

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

    fun updateEmployee(employee: EmployeeEntity) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        updateEmployeeUseCase(employee).collect { res ->
            _state.update { it.copy(isLoading = false) }

            when (res) {
                is UiState.Success -> {
                    val updatedEmployees = state.value.employees.map { employeeUi ->
                        Log.d("EmployeeViewModel", "Updating employee with ID: ${employeeUi.id} to ID: ${employee.id}")
                        if (employeeUi.id.toString() == employee.id.toString()) {
                            employee.toUiModel()
                        } else {
                            employeeUi
                        }
                    }

                    _state.update { it.copy(
                        employees = updatedEmployees,
                        error = null
                    )}
                    _uiEvent.emit(UiEvent.ShowSnackbar("פרטי העובד ${employee.firstName} עודכנו בהצלחה"))
                }

                is UiState.Error -> {
                    _state.update { it.copy(
                        error = res.message,
                        isLoading = false
                    )}
                    _uiEvent.emit(UiEvent.ShowSnackbar(res.message ?: "שגיאה בעדכון פרטי העובד"))
                }

                is UiState.Loading -> Unit
            }
        }
    }



    fun deleteEmployeeById(employeeId: Long) = viewModelScope.launch {
        // עדכון מצב טעינה
        _state.value = state.value.copy(isLoading = true)

        try {
            findEmployeeByIdUseCase(employeeId).collect { findResult ->
                when (findResult) {
                    is UiState.Success -> {
                        val employeeEntity = findResult.data.toEntity()

                        deleteEmployeeUseCase(employeeEntity).collect { deleteResult ->
                            when (deleteResult) {
                                is UiState.Success -> {
                                    getEmployees()
                                    _uiEvent.emit(UiEvent.ShowSnackbar("העובד נמחק בהצלחה"))
                                }
                                is UiState.Error -> {
                                    _state.value = state.value.copy(
                                        error = deleteResult.message,
                                        isLoading = false
                                    )
                                    _uiEvent.emit(UiEvent.ShowSnackbar("שגיאה במחיקת העובד: ${deleteResult.message}"))
                                }
                                is UiState.Loading -> {
                                    // כבר נמצאים במצב טעינה, אין צורך לעדכן שוב
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        _state.value = state.value.copy(
                            error = findResult.message,
                            isLoading = false
                        )
                        _uiEvent.emit(UiEvent.ShowSnackbar("לא נמצא עובד עם מזהה זה: ${findResult.message}"))
                    }
                    is UiState.Loading -> {
                        // כבר נמצאים במצב טעינה, אין צורך לעדכן שוב
                    }
                }
            }
        } catch (e: Exception) {
            // טיפול בשגיאות לא צפויות
            _state.value = state.value.copy(
                error = e.message ?: "שגיאה לא ידועה",
                isLoading = false
            )
            _uiEvent.emit(UiEvent.ShowSnackbar("שגיאה לא צפויה: ${e.message}"))
        } finally {
            // וידוא שסטטוס הטעינה מוסר בכל מקרה
            _state.value = state.value.copy(isLoading = false)
        }
    }

    fun addEmployee(employee: EmployeeEntity) = viewModelScope.launch(Dispatchers.IO) {
        addEmployeeUseCase(employee).collect { res ->
            when (res) {
                is UiState.Success -> {
                    val employees = state.value.employees.toMutableList()
                    employees.add(employee.toUiModel())
                    _state.value = state.value.copy(
                        employees = employees
                    )
                    _state.value = state.value.copy(
                        isLoading = false
                    )
                }

                is UiState.Error -> {
                    _state.value = state.value.copy(
                        error = res.message
                    )
                    _state.value = state.value.copy(
                        isLoading = false
                    )
                }

                is UiState.Loading -> {
                    _state.value = state.value.copy(
                        isLoading = true
                    )
                }
            }
        }
    }

    fun getEmployees() = viewModelScope.launch(Dispatchers.IO) {
        _state.value = state.value.copy(
            isLoading = true
        )
        getEmployeesUseCase().collect { employees ->
                    val employees = employees.map { it.toUiModel()}
                    _state.update { it.copy(employees = employees)  }
                    _state.update {  it.copy( isLoading = false) }

        }
    }

    fun resetEmployee():EmployeeEntity {
      return  EmployeeEntity(
            firstName = "",
            lastName = "",
            role = "REGULAR",
            email = "",
            phoneNumber = "",
            address = "",
            idNumber = "",
            dateOfBirth = Date().time,
            minShifts = 0,
            maxShifts = 5,
            totalWorkHoursLimit = 40.0
        )
    }
}