package com.example.shiftime.presentation.ui.view.employeeconstraintsscreen


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shiftime.R
import com.example.shiftime.domain.model.DayWithShifts
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.presentation.ui.events.modelevents.EmployeeConstraintsEvent
import com.example.shiftime.presentation.ui.events.uievents.UiEvent
import com.example.shiftime.presentation.ui.view.viewmodels.EmployeeConstraintsViewModel
import com.example.shiftime.utils.enums.Days
import com.example.shiftime.utils.enums.ShiftType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EmployeeConstraintsScreen(
    employeeId: Long,
    viewModel: EmployeeConstraintsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.constraintsState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(employeeId,state.days) {
        viewModel.onEvent(EmployeeConstraintsEvent.SelectEmployee(employeeId))
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.NavigateTo -> {
                    //  //TODO: IMPLEMENT
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavigationBar()
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
            ) {
                EmployeeHeader(
                    employee = state.selectedEmployee,
                    modifier = Modifier.fillMaxWidth()
                )

                MonthSelector(
                    currentDate = state.selectedDate ?: LocalDate.now(),
                    onMonthChanged = { date -> viewModel.onEvent(EmployeeConstraintsEvent.ChangeMonth(date)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 16.dp)
                )

                WeekDaySelector(
                    days = state.days,
                    selectedDay = state.selectedDay,
                    selectedDate = state.selectedDate ?: LocalDate.now(),
                    onDaySelected = { day ->
                        viewModel.onEvent(EmployeeConstraintsEvent.SelectDay(day))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                SelectedDayHeader(
                    selectedDay = state.selectedDay,
                    selectedDate = state.selectedDate,
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.selectedDayShifts) { shiftWithConstraint ->
                        ShiftConstraintCard(
                            shift = shiftWithConstraint.shift,
                            workAvailability = if(shiftWithConstraint.canWork) WorkAvailability.CanWork else WorkAvailability.CannotWork,
                            onAvailabilityChanged = { workAvailability ->
                                viewModel.onEvent(
                                    EmployeeConstraintsEvent.ToggleCanWork(
                                        shiftId = shiftWithConstraint.shift.id,
                                        canWork = workAvailability == WorkAvailability.CanWork,
                                        comment = null
                                    )
                                )
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}


@Composable
private fun EmployeeHeader(
    employee: Employee?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "תמונת פרופיל",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "בקר ביטחון",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = employee?.let { "${it.firstName} ${it.lastName}" } ?: "עמית אברהמי",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthSelector(
    currentDate: LocalDate,
    onMonthChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onMonthChanged(currentDate.minusDays(7)) }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "חודש קודם",
                tint = Color.Black
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = getMonthName(currentDate),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "פתח בחירת חודש",
                tint = Color.Black
            )
        }

        IconButton(
            onClick = { onMonthChanged(currentDate.plusDays(7)) }
        ) {
            Icon(
                imageVector =  Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "חודש הבא",
                tint = Color.Black
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeekDaySelector(
    days: List<DayWithShifts>,
    selectedDay: Days?,
    selectedDate: LocalDate,
    onDaySelected: (Days) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayNames = mapOf(
        Days.SUNDAY to "רא",
        Days.MONDAY to "שנ",
        Days.TUESDAY to "של",
        Days.WEDNESDAY to "רב",
        Days.THURSDAY to "חמ",
        Days.FRIDAY to "שי",
        Days.SATURDAY to "שב"
    )

    val weekDates = remember(selectedDate) {
        val startOfWeek = selectedDate.with(java.time.DayOfWeek.SUNDAY)
        (0..6).map { startOfWeek.plusDays(it.toLong()) }
    }


    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        itemsIndexed(Days.entries.toTypedArray()) { index, day ->
            val date = weekDates[index]
            val isSelected = day == selectedDay
            val hasShifts = days.any { it.day == day && it.shifts.isNotEmpty() }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onDaySelected(day) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = dayNames[day] ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) Color.Black else Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (isSelected) Color.Black else Color.Transparent,
                            shape = CircleShape
                        )
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) Color.White else Color.Black,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 16.sp
                    )
                }

                if (hasShifts && !isSelected) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(Color.Gray, CircleShape)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SelectedDayHeader(
    selectedDay: Days?,
    selectedDate: LocalDate?,
    modifier: Modifier = Modifier
) {
    val dayNames = mapOf(
        Days.SUNDAY to "יום ראשון",
        Days.MONDAY to "יום שני",
        Days.TUESDAY to "יום שלישי",
        Days.WEDNESDAY to "יום רביעי",
        Days.THURSDAY to "יום חמישי",
        Days.FRIDAY to "יום שישי",
        Days.SATURDAY to "יום שבת"
    )

    Text(
        text = selectedDay?.let { dayNames[it] } ?: "בחר יום",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = Color.Black,
        modifier = modifier
    )
}

@Composable
private fun ShiftConstraintCard(
    shift: Shift,
    workAvailability: WorkAvailability?,
    onAvailabilityChanged: (WorkAvailability) -> Unit,
    modifier: Modifier = Modifier
) {
    val (shiftName, backgroundColor) = when (shift.shiftType) {
        ShiftType.MORNING -> "משמרת בוקר" to Color(0xFFFFEB3B)
        ShiftType.AFTERNOON -> "משמרת צהריים" to Color(0xFFFF9800)
        ShiftType.NIGHT -> "משמרת לילה" to Color(0xFF9E9E9E)
    }

    val timeText = formatShiftTime(shift)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = shiftName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Text(
                    text = timeText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            WorkAvailabilitySelector(
                selectedAvailability = workAvailability,
                onAvailabilityChanged = onAvailabilityChanged
            )
        }
    }
}

@Composable
private fun WorkAvailabilitySelector(
    selectedAvailability: WorkAvailability?,
    onAvailabilityChanged: (WorkAvailability) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ConstraintOption(
            text = "יכול/ה לעבוד",
            isSelected = selectedAvailability == WorkAvailability.CanWork,
            onClick = { onAvailabilityChanged(WorkAvailability.CanWork) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ConstraintOption(
            text = "לא יכול/ה לעבוד",
            isSelected = selectedAvailability == WorkAvailability.CannotWork,
            onClick = { onAvailabilityChanged(WorkAvailability.CannotWork) }
        )
    }
}



@Composable
private fun ConstraintOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}


@Composable
private fun BottomNavigationBar(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem(
                icon = Icons.Default.DateRange,
                text = "ראשי",
                isSelected = false
            )

            BottomNavItem(
                icon = Icons.Default.Check,
                text = "אילוצים",
                isSelected = true
            )

            BottomNavItem(
                icon = Icons.Default.Face,
                text = "סטטיסטיקה",
                isSelected = false
            )

            BottomNavItem(
                icon = Icons.Default.Place,
                text = "הגדרות",
                isSelected = false
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = if (isSelected) Color.Black else Color.Gray,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.Black else Color.Gray,
            fontSize = 12.sp
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getMonthName(date: LocalDate): String {
    val monthNames = mapOf(
        1 to "January", 2 to "February", 3 to "March", 4 to "April",
        5 to "May", 6 to "June", 7 to "July", 8 to "August",
        9 to "September", 10 to "October", 11 to "November", 12 to "December"
    )
    return monthNames[date.monthValue] ?: "Unknown"
}

private fun formatShiftTime(shift: Shift): String {
    val formatter = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
    return "${formatter.format(shift.startTime)} - ${formatter.format(shift.endTime)}"
}