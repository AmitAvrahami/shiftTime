package com.example.shiftime.presentation.ui.view.employeescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shiftime.data.local.entity.EmployeeEntity
import com.example.shiftime.data.local.mapper.toEntity
import com.example.shiftime.presentation.ui.common.state.EmployeeUiModel
import com.example.shiftime.presentation.ui.events.uievents.UiEvent
import com.example.shiftime.presentation.ui.view.employeescreen.components.AddNewEmployeeDialog
import com.example.shiftime.presentation.ui.view.employeescreen.components.EditEmployeeDialog
import com.example.shiftime.presentation.ui.view.viewmodels.EmployeeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun EmployeeScreen(
    modifier: Modifier = Modifier,
    employeeViewModel: EmployeeViewModel = hiltViewModel()
) {
    val state by employeeViewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var tempEmployeeEntity by remember { mutableStateOf<EmployeeEntity?>(null) }

    // UI Event Collection
    LaunchedEffect(key1 = true) {
        employeeViewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }

                is UiEvent.NavigateTo -> TODO()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (!state.isLoading) {
                AddNewEmployeeButton(onClick = {
                    employeeViewModel.onEvent(EmployeeEvent.ShowAddEmployeeDialog)
                })
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Screen Header
            EmployeeScreenHeader(title = "רשימת עובדים")

            // Loading Indicator or Content
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp,
                        trackColor = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                // Empty State or Employee List
                if (state.employees.isEmpty()) {
                    EmptyEmployeeList()
                } else {
                    EmployeesList(
                        employees = state.employees,
                        onToggleExpand = { employeeId ->
                            employeeViewModel.onEvent(EmployeeEvent.ToggleEmployeeDetails(employeeId))
                        },
                        onEditClick = { employee ->
                            tempEmployeeEntity = employee.toEntity()
                            employeeViewModel.onEvent(EmployeeEvent.ShowEditDialog(employee))
                        },
                        onDeleteClick = { employee ->
                            employeeViewModel.onEvent(EmployeeEvent.ShowDeleteConfirmDialog(employee))
                        }
                    )
                }
            }

            // Add New Employee Dialog
            if (state.isAddingEmployee) {
                var newEmployee by remember {
                    mutableStateOf(
                        EmployeeEntity(
                            id = 0,
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
                    )
                }

                AddNewEmployeeDialog(
                    employee = newEmployee,
                    onEmployeeChange = { employee -> newEmployee = employee },
                    onDismiss = {
                        employeeViewModel.onEvent(EmployeeEvent.HideAddEmployeeDialog)
                    },
                    onConfirm = {
                        employeeViewModel.onEvent(EmployeeEvent.AddEmployee(newEmployee))
                        employeeViewModel.onEvent(EmployeeEvent.HideAddEmployeeDialog)

                        // איפוס טופס
                        newEmployee = EmployeeEntity()

                    },
                    employeeViewModel = employeeViewModel,
                    validateEmployeeInput = { employee -> employeeViewModel.validateEmployeeInput(employee) }  // העברת ViewModel לדיאלוג
                )
                }

            // Edit Employee Dialog
            if (state.isEditingEmployee && tempEmployeeEntity != null) {
                var editingEmployee by remember(tempEmployeeEntity) { mutableStateOf(tempEmployeeEntity!!) }

                EditEmployeeDialog(
                    employee = editingEmployee,
                    onEmployeeChange = { employee ->
                        editingEmployee = employee
                    },
                    onDismiss = {
                        employeeViewModel.onEvent(EmployeeEvent.HideEditDialog)
                        tempEmployeeEntity = null
                    },
                    onConfirm = {
                        employeeViewModel.onEvent(EmployeeEvent.UpdateEmployee(editingEmployee))
                        employeeViewModel.onEvent(EmployeeEvent.HideEditDialog)
                        tempEmployeeEntity = null
                    },
                    employeeViewModel = employeeViewModel
                )
            }

            if (state.showDeleteConfirmDialog && state.employeeToDelete != null) {
                DeleteConfirmationDialog(
                    employeeName = state.employeeToDelete!!.employeeName,
                    onDismiss = {
                        employeeViewModel.onEvent(EmployeeEvent.HideDeleteConfirmDialog)
                    },
                    onConfirm = {
                        employeeViewModel.onEvent(EmployeeEvent.DeleteEmployee(state.employeeToDelete!!))
                        employeeViewModel.onEvent(EmployeeEvent.HideDeleteConfirmDialog)
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyEmployeeList() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "אין עובדים במערכת",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "לחץ על כפתור ה-+ כדי להוסיף עובד חדש",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmployeeScreenHeader(
    modifier: Modifier = Modifier,
    title: String
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmployeesList(
    modifier: Modifier = Modifier,
    employees: List<EmployeeUiModel>,
    onToggleExpand: (String) -> Unit = {},
    onEditClick: (EmployeeUiModel) -> Unit = {},
    onDeleteClick: (EmployeeUiModel) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(
            items = employees,
            key = { it.employeeId }
        ) { employee ->
            SwipeToDeleteContainer(
                item = employee,
                onDismiss = { onDeleteClick(employee) }
            ) {
                EmployeeItem(
                    employee = it,
                    onToggleExpand = onToggleExpand,
                    onEditClick = onEditClick
                )
            }
        }
    }
}

@Composable
fun EmployeeItem(
    employee: EmployeeUiModel,
    onToggleExpand: (String) -> Unit = {},
    onEditClick: (EmployeeUiModel) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand(employee.employeeId) },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = tween(durationMillis = 300)
                )
        ) {
            // Header - always visible
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = employee.employeeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        text = employee.employeeDesignation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = employee.employeeImage),
                        contentDescription = "תמונת עובד",
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Expanded Details
            AnimatedVisibility(visible = employee.isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Divider(
                        modifier = Modifier.padding(bottom = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    // Phone
                    if (employee.employeePhone.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = employee.employeePhone,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Shifts info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "מינימום משמרות: ${employee.minShifts}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "מקסימום משמרות: ${employee.maxShifts}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Edit Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { onEditClick(employee) },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "ערוך",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ערוך פרטים")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddNewEmployeeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    LargeFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "הוסף עובד חדש",
            modifier = Modifier.size(32.dp)
        )
    }
}


@Composable
fun DeleteConfirmationDialog(
    employeeName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "אישור מחיקה",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "האם אתה בטוח שברצונך למחוק את העובד $employeeName?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("מחק")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("ביטול")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDismiss: () -> Unit,
    content: @Composable (T) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                false
            } else {
                false
            }
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            DeleteBackground(dismissState, SwipeToDismissBoxValue.EndToStart)
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        content = { content(item) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteBackground(dismissState: SwipeToDismissBoxState, dismissDirection: SwipeToDismissBoxValue) {
    val color = when {
        dismissState.dismissDirection == dismissDirection -> MaterialTheme.colorScheme.error
        else -> Color.Transparent
    }

    val alignment = Alignment.CenterEnd

    val icon = Icons.Default.Delete

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 16.dp),
        contentAlignment = alignment
    ) {
        Icon(
            icon,
            contentDescription = "מחק",
            tint = MaterialTheme.colorScheme.onError
        )
    }
}