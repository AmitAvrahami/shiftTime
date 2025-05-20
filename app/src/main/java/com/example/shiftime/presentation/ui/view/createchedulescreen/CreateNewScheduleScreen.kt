package com.example.shiftime.presentation.ui.view.createchedulescreen

import android.app.DatePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.WorkWeek
import com.example.shiftime.presentation.ui.events.modelevents.ShiftEvent
import com.example.shiftime.presentation.ui.events.uievents.UiEvent
import com.example.shiftime.presentation.ui.viewmodels.ShiftViewModel
import com.example.shiftime.utils.enums.AssignmentStyle
import com.example.shiftime.utils.enums.ShiftType
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
private fun String.toLocalDate(): LocalDate {
    val parts = this.split("/")
    return LocalDate.of(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateScheduleScreen(
    viewModel: ShiftViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val shifts by viewModel.shifts.collectAsState()
    val context = LocalContext.current

    // איסוף אירועי UI
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is UiEvent.NavigateTo -> TODO()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ScreenHeaderBanner(title = "ניהול משמרות")

            Spacer(modifier = Modifier.height(16.dp))

            // בחירת שבוע קיים
            if (state.allWorkWeeks.isNotEmpty()) {
                WorkWeekSelector(
                    workWeeks = state.allWorkWeeks,
                    currentWorkWeek = state.currentWorkWeek,
                    onWorkWeekSelected = { workWeekId ->
                        viewModel.onEvent(ShiftEvent.ActivateWorkWeek(workWeekId))
                    }
                )
            }

            // יצירת שבוע חדש
            AssignmentStyleDropdown(
                selectedStyle = state.assignmentStyle.label,
                onStyleSelected = { style -> viewModel.onEvent(ShiftEvent.SetAssignmentStyle(style)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            WeekPickerButton(
                selectedDate = state.startDate?.let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                        Date.from(it.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    )
                } ?: "בחר שבוע",
                onWeekSelected = { dateString ->
                    val pattern = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date = pattern.parse(dateString)
                    val localDate = date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                    localDate?.let { viewModel.onEvent(ShiftEvent.SetStartDate(it)) }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // בוחר ימים
            WeekDaySelector(
                selectedDay = state.selectedDay.ordinal,
                onDaySelected = { dayIndex -> viewModel.onEvent(ShiftEvent.SetSelectedDay(dayIndex)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // רשימת משמרות ליום נבחר
            val filteredShifts = shifts.filter { it.shiftDay == state.selectedDay }
            ShiftListForDay(
                shifts = filteredShifts,
                onShiftClick = { shift -> viewModel.onEvent(ShiftEvent.ShowEditShiftDialog(shift)) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // כפתור יצירת סידור
            GenerateScheduleButton(
                onClick = { viewModel.onEvent(ShiftEvent.GenerateSchedule) },
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // דיאלוג עריכת משמרת
        if (state.isEditDialogVisible && state.currentEditShift != null) {
            EditShiftDialog(
                shift = state.currentEditShift!!,
                onDismissRequest = { viewModel.onEvent(ShiftEvent.HideEditShiftDialog) },
                onSaveClick = { updatedShift -> viewModel.onEvent(ShiftEvent.UpdateShift(updatedShift)) }
            )
        }

        // לוגיקת טעינה
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ScreenHeaderBanner(title: String = "יצירת סידור חדש") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp))
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun AssignmentStyleDropdown(
    selectedStyle: String = AssignmentStyle.BALANCED.label,
    onStyleSelected: (AssignmentStyle) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.TopEnd)) {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(16.dp)
                .widthIn(min = 170.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(Icons.Default.ArrowDropDown, contentDescription = "פתח תפריט")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = selectedStyle)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(0.dp, 0.dp)
        ) {
            AssignmentStyle.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onStyleSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekPickerButton(
    onWeekSelected: (String) -> Unit,
    selectedDate: String
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val now = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                if (!selectedCalendar.after(now)) {
                    Toast.makeText(context, "נא לבחור תאריך עתידי", Toast.LENGTH_SHORT).show()
                    return@DatePickerDialog
                }

                // בדיקה אם זה יום ראשון
                if (selectedCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(selectedCalendar.time)
                    onWeekSelected(formattedDate)
                } else {
                    // מציאת היום ראשון הקרוב
                    while (selectedCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                        selectedCalendar.add(Calendar.DAY_OF_MONTH, 1)
                    }

                    val adjustedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(selectedCalendar.time)

                    Toast.makeText(
                        context,
                        "התאריך כוון ליום ראשון הקרוב: $adjustedDate",
                        Toast.LENGTH_SHORT
                    ).show()

                    onWeekSelected(adjustedDate)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { datePickerDialog.show() },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(end = 16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(Icons.Default.DateRange, contentDescription = "בחר שבוע")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = selectedDate)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekDaySelector(
    onDaySelected: (Int) -> Unit,
    selectedDay: Int
) {
    val days = listOf("א׳", "ב׳", "ג׳", "ד׳", "ה׳", "ו׳", "ש׳")

    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SingleChoiceSegmentedButtonRow {
            days.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index, days.size),
                    onClick = { onDaySelected(index) },
                    selected = index == selectedDay,
                    label = { Text(label) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ShiftListForDay(
    shifts: List<Shift>,
    onShiftClick: (Shift) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        if (shifts.isEmpty()) {
            item {
                EmptyStateCard(
                    message = "אין משמרות ליום זה",
                    modifier = Modifier.width(180.dp)
                )
            }
        } else {
            items(shifts, key = { it.id }) { shift ->
                ShiftCard(
                    shift = shift,
                    onClick = { onShiftClick(shift) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
fun GenerateScheduleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 32.dp)
            .height(56.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(6.dp)
    ) {
        Icon(
            Icons.Default.Edit,
            contentDescription = "יצירת סידור",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = "צור סידור חדש",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun EditShiftDialog(
    shift: Shift,
    onDismissRequest: () -> Unit,
    onSaveClick: (Shift) -> Unit
) {
    var employeesRequired by remember { mutableStateOf(shift.employeesRequired.toString()) }
    var startHour by remember { mutableStateOf(SimpleDateFormat("HH", Locale.getDefault()).format(shift.startTime)) }
    var startMinute by remember { mutableStateOf(SimpleDateFormat("mm", Locale.getDefault()).format(shift.startTime)) }
    var endHour by remember { mutableStateOf(SimpleDateFormat("HH", Locale.getDefault()).format(shift.endTime)) }
    var endMinute by remember { mutableStateOf(SimpleDateFormat("mm", Locale.getDefault()).format(shift.endTime)) }

    var startTimeError by remember { mutableStateOf<String?>(null) }
    var endTimeError by remember { mutableStateOf<String?>(null) }
    var employeeCountError by remember { mutableStateOf<String?>(null) }

    fun validateInputs(): Boolean {
        var isValid = true

        // בדיקת שעת התחלה
        if (startHour.toIntOrNull() !in 0..23) {
            startTimeError = "שעה לא תקינה (0-23)"
            isValid = false
        } else {
            startTimeError = null
        }

        if (startMinute.toIntOrNull() !in 0..59) {
            startTimeError = "דקות לא תקינות (0-59)"
            isValid = false
        } else if (startTimeError == null) {
            startTimeError = null
        }

        // בדיקת שעת סיום
        if (endHour.toIntOrNull() !in 0..23) {
            endTimeError = "שעה לא תקינה (0-23)"
            isValid = false
        } else {
            endTimeError = null
        }

        if (endMinute.toIntOrNull() !in 0..59) {
            endTimeError = "דקות לא תקינות (0-59)"
            isValid = false
        } else if (endTimeError == null) {
            endTimeError = null
        }

        // בדיקת מספר עובדים
        if (employeesRequired.toIntOrNull() == null || employeesRequired.toIntOrNull()!! <= 0) {
            employeeCountError = "יש להזין מספר חיובי"
            isValid = false
        } else {
            employeeCountError = null
        }

        return isValid
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                if (validateInputs()) {
                    // יצירת תאריך התחלה חדש
                    val startCalendar = Calendar.getInstance()
                    startCalendar.time = shift.startTime
                    startCalendar.set(Calendar.HOUR_OF_DAY, startHour.toInt())
                    startCalendar.set(Calendar.MINUTE, startMinute.toInt())

                    // יצירת תאריך סיום חדש
                    val endCalendar = Calendar.getInstance()
                    endCalendar.time = shift.endTime
                    endCalendar.set(Calendar.HOUR_OF_DAY, endHour.toInt())
                    endCalendar.set(Calendar.MINUTE, endMinute.toInt())

                    // בדיקה אם משמרת לילה (אם כן, צריך לוודא שתאריך הסיום הוא ביום למחרת)
                    if (shift.shiftType == ShiftType.NIGHT && endCalendar.before(startCalendar)) {
                        endCalendar.add(Calendar.DAY_OF_MONTH, 1)
                    }

                    val updatedShift = shift.copy(
                        startTime = startCalendar.time,
                        endTime = endCalendar.time,
                        employeesRequired = employeesRequired.toInt()
                    )

                    onSaveClick(updatedShift)
                }
            }) {
                Text("שמור")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("בטל")
            }
        },
        title = {
            Text("עריכת משמרת")
        },
        icon = {
            Icon(
                imageVector = when (shift.shiftType) {
                    ShiftType.MORNING -> Icons.Default.Edit
                    ShiftType.AFTERNOON -> Icons.Default.Edit
                    ShiftType.NIGHT -> Icons.Default.Edit
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // פרטי משמרת
                Text(
                    text = "${shift.shiftType.label} - ${shift.shiftDay.label}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "תאריך: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(shift.startTime)}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Divider()

                // שעת התחלה
                Text(
                    text = "שעת התחלה",
                    style = MaterialTheme.typography.labelLarge
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // שעה
                    OutlinedTextField(
                        value = startHour,
                        onValueChange = { if (it.length <= 2) startHour = it },
                        label = { Text("שעה") },
                        singleLine = true,
                        isError = startTimeError != null,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp)
                    )

                    Text(":")

                    // דקות
                    OutlinedTextField(
                        value = startMinute,
                        onValueChange = { if (it.length <= 2) startMinute = it },
                        label = { Text("דקות") },
                        singleLine = true,
                        isError = startTimeError != null,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp)
                    )
                }

                // הצגת שגיאה אם יש
                startTimeError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // שעת סיום
                Text(
                    text = "שעת סיום",
                    style = MaterialTheme.typography.labelLarge
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // שעה
                    OutlinedTextField(
                        value = endHour,
                        onValueChange = { if (it.length <= 2) endHour = it },
                        label = { Text("שעה") },
                        singleLine = true,
                        isError = endTimeError != null,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp)
                    )

                    Text(":")

                    // דקות
                    OutlinedTextField(
                        value = endMinute,
                        onValueChange = { if (it.length <= 2) endMinute = it },
                        label = { Text("דקות") },
                        singleLine = true,
                        isError = endTimeError != null,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp)
                    )
                }

                // הצגת שגיאה אם יש
                endTimeError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Divider()

                // כמות עובדים נדרשת
                OutlinedTextField(
                    value = employeesRequired,
                    onValueChange = { if (it.all { char -> char.isDigit() }) employeesRequired = it },
                    label = { Text("כמות עובדים נדרשת") },
                    singleLine = true,
                    isError = employeeCountError != null,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // הצגת שגיאה אם יש
                employeeCountError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp)
    )


    @Composable
    fun EmptyStateCard(
        message: String,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier
                .padding(8.dp)
                .height(150.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }


    @Composable
    fun ShiftCard(
        shift: Shift,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val backgroundColor = when (shift.shiftType) {
            ShiftType.MORNING -> Color(0xFFFFF9C4) // צהוב בהיר
            ShiftType.AFTERNOON -> Color(0xFFFFCC80) // כתום בהיר
            ShiftType.NIGHT -> Color(0xFFB0BEC5) // כחול אפור
        }

        val icon = when (shift.shiftType) {
            ShiftType.MORNING -> Icons.Default.Edit
            ShiftType.AFTERNOON -> Icons.Default.Favorite
            ShiftType.NIGHT -> Icons.Default.DateRange
        }

        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())

        Card(
            modifier = modifier
                .padding(8.dp)
                .height(150.dp)
                .width(180.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onClick() }
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // כותרת ואייקון
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = shift.shiftType.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.7f)
                    )
                }

                // שעות
                Text(
                    text = "${formatter.format(shift.startTime)} - ${formatter.format(shift.endTime)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )

                // כמות עובדים
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.DarkGray
                    )

                    Text(
                        text = "${shift.assignedEmployees.size}/${shift.employeesRequired}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // כפתור עריכה
                IconButton(
                    onClick = onClick,
                    modifier = Modifier
                        .align(Alignment.End)
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "ערוך משמרת",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .height(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ShiftCard(
    shift: Shift,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (shift.shiftType) {
        ShiftType.MORNING -> Color(0xFFFFF9C4) // צהוב בהיר
        ShiftType.AFTERNOON -> Color(0xFFFFCC80) // כתום בהיר
        ShiftType.NIGHT -> Color(0xFFB0BEC5) // כחול אפור
    }

    val icon = when (shift.shiftType) {
        ShiftType.MORNING -> Icons.Default.DateRange
        ShiftType.AFTERNOON -> Icons.Default.DateRange
        ShiftType.NIGHT -> Icons.Default.Edit
    }

    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    Card(
        modifier = modifier
            .padding(8.dp)
            .height(150.dp)
            .width(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // כותרת ואייקון
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = shift.shiftType.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black.copy(alpha = 0.7f)
                )
            }

            // שעות
            Text(
                text = "${formatter.format(shift.startTime)} - ${formatter.format(shift.endTime)}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            // כמות עובדים
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.DarkGray
                )

                Text(
                    text = "${shift.assignedEmployees.size}/${shift.employeesRequired}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // כפתור עריכה
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .align(Alignment.End)
                    .size(32.dp)
                    .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "ערוך משמרת",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkWeekSelector(
    workWeeks: List<WorkWeek>,
    currentWorkWeek: WorkWeek?,
    onWorkWeekSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable { expanded = true },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = currentWorkWeek?.name ?: "בחר שבוע עבודה",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    currentWorkWeek?.let {
                        Text(
                            text = "${it.startDate.format(dateFormatter)} - ${it.endDate.format(dateFormatter)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "פתח רשימת שבועות",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(300.dp)
                .background(MaterialTheme.colorScheme.surface)
                .clip(RoundedCornerShape(12.dp))
        ) {
            if (workWeeks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "אין שבועות זמינים",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            } else {
                workWeeks.sortedByDescending { it.startDate }.forEach { week ->
                    val isSelected = week.id == currentWorkWeek?.id
                    val isActive = week.isActive

                    DropdownMenuItem(
                        text = {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = week.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    if (isActive) {
                                        Text(
                                            text = "(פעיל)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Text(
                                    text = "${week.startDate.format(dateFormatter)} - ${week.endDate.format(dateFormatter)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        },
                        onClick = {
                            onWorkWeekSelected(week.id)
                            expanded = false
                        },
                        modifier = Modifier.background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }
    }
}