package com.example.shiftime.ui.view.schedulescreen

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shiftime.employees
import com.example.shiftime.models.Employee
import com.example.shiftime.models.ShiftType
import com.example.shiftime.ui.view.constraintscreen.EmployeeUiModel
import com.example.shiftime.ui.view.constraintscreen.SampleData
import java.util.Date

@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier
) {
    val selectedMonth = remember { mutableStateOf("ינואר") }
    val selectedDay = remember { mutableStateOf(1) }
    var fromDay by remember { mutableStateOf(0) }
    var toDay by remember { mutableStateOf(7) }

    val monthDays = mapOf(
        "ינואר" to 31, "פברואר" to 28, "מרץ" to 31, "אפריל" to 30,
        "מאי" to 31, "יוני" to 30, "יולי" to 31, "אוגוסט" to 31,
        "ספטמבר" to 30, "אוקטובר" to 31, "נובמבר" to 30, "דצמבר" to 31
    )
    val daysInMonth = monthDays[selectedMonth.value] ?: 30
    val daysList = (1..daysInMonth).toList()


    val visibleDays = daysList.subList(
        fromDay,
        toDay.coerceAtMost(daysList.size)
    )

    Column(
        modifier = modifier
            .fillMaxSize()

    ) {
        DateSelector(
            dayList = visibleDays,
            monthAndYearSelector = {
                MonthAndYearSelector(
                    selectedMonth = selectedMonth.value,
                    onMonthSelected = {
                        selectedMonth.value = it
                        fromDay = 0
                        toDay = 7
                        selectedDay.value = fromDay + 1
                    },
                    onNextWeekClicked = {
                        if (fromDay + 7 < daysList.size) {
                            fromDay += 7
                            toDay += 7
                        } else {
                            fromDay = 0
                            toDay = 7
                            val months = monthDays.keys.toList()
                            val currentMonthIndex = months.indexOf(selectedMonth.value)
                            if (currentMonthIndex < months.size - 1) {
                                selectedMonth.value = months[currentMonthIndex + 1]
                            } else {
                                selectedMonth.value = months[0]
                            }
                        }
                        selectedDay.value = fromDay + 1 // עדכון נכון
                    },
                    onPreviousWeekClicked = {
                        if (fromDay - 7 >= 0) {
                            fromDay -= 7
                            toDay -= 7
                        } else {
                            fromDay = 0
                            toDay = 7
                            val months = monthDays.keys.toList()
                            val currentMonthIndex = months.indexOf(selectedMonth.value)
                            if (currentMonthIndex > 0) {
                                selectedMonth.value = months[currentMonthIndex - 1]
                            } else {
                                selectedMonth.value = months.last()
                            }
                            val lastDayOfMonth = monthDays[selectedMonth.value] ?: 30
                            fromDay = lastDayOfMonth - 6
                            toDay = lastDayOfMonth
                            selectedDay.value = fromDay + 1 // עדכון נכון

                        }
                        selectedDay.value = fromDay + 1
                    },
                )
            },
            dayItemSelector = { day ->
                DayItemSelector(
                    dayName = "יום",
                    monthDay = day.toString(),
                    isSelected = day == selectedDay.value,
                    onClick = { selectedDay.value = day }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        Button (
            onClick = { /*TODO*/ },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.Create, contentDescription = null)
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = "ערוך סידור ידנית"
            )
        }
        ShiftDetailCard(
            modifier = Modifier.padding(16.dp),
            shiftCardDetails = ShiftCardDetails(
                shiftType = ShiftType.MORNING,
                shiftStart = "9:00",
                shiftEnd = "12:00",
                employees = SampleData.employees.subList(0,2),
                shiftColor = Color.Yellow
            )
        )
        ShiftDetailCard(
            modifier = Modifier.padding(16.dp),
            shiftCardDetails = ShiftCardDetails(
                shiftType = ShiftType.MORNING,
                shiftStart = "9:00",
                shiftEnd = "12:00",
                employees = SampleData.employees.subList(0,2),
                shiftColor = Color.Green
            )
        )
    }
}

@Composable
fun DateSelector(
    monthAndYearSelector: @Composable () -> Unit = {},
    dayItemSelector: @Composable (Int) -> Unit = {},
    dayList: List<Int> = emptyList(),
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)

    ) {
        monthAndYearSelector()
        LazyRow(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(dayList.size) { day ->
                dayItemSelector(dayList[day])
            }
        }
    }
}
@Composable
fun MonthAndYearSelector(
    modifier: Modifier = Modifier,
    selectedMonth: String,
    onMonthSelected: (String) -> Unit,
    onNextWeekClicked: () -> Unit,
    onPreviousWeekClicked: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val months = listOf("ינואר", "פברואר", "מרץ", "אפריל", "מאי", "יוני", "יולי", "אוגוסט", "ספטמבר", "אוקטובר", "נובמבר", "דצמבר")

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousWeekClicked) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedMonth,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "בחר חודש")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                months.forEach { month ->
                    DropdownMenuItem(
                        text = { Text(month) },
                        onClick = {
                            expanded = false
                            onMonthSelected(month)
                        }
                    )
                }
            }
        }
        IconButton(onClick = onNextWeekClicked) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}





@Composable
fun DayItemSelector(
    modifier: Modifier = Modifier,
    dayName: String ,
    monthDay: String ,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
    ){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .fillMaxWidth().padding(8.dp)
    ) {
        Text(
            text = dayName,
            fontWeight = MaterialTheme.typography.labelLarge.fontWeight,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = monthDay,
            fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
            color = MaterialTheme.colorScheme.onPrimary
        )
        if (isSelected)
            Icon(Icons.Default.Star, contentDescription = null,)

    }
}

@Composable
fun ShiftDetailCard(
    modifier: Modifier = Modifier,
    shiftCardDetails: ShiftCardDetails
){
    val shiftName: String = when(shiftCardDetails.shiftType){
        ShiftType.MORNING -> "משמרת בוקר"
        ShiftType.AFTERNOON -> "משמרת צהריים"
        ShiftType.NIGHT -> "משמרת לילה"
    }
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .background(shiftCardDetails.shiftColor),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = shiftCardDetails.shiftColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
        ){

            Text(
                text = shiftCardDetails.shiftStart + " - " + shiftCardDetails.shiftEnd
            )
            Text(
                text = shiftName
            )
        }

        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            shiftCardDetails.employees.forEach {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = it.employeeImage),
                        contentDescription = null,
                        modifier=Modifier.clip(CircleShape)
                    )
                    Text(text = it.employeeDesignation)
                    Text(text = it.employeeName)
                 }
                if (it != shiftCardDetails.employees.last())
                    VerticalDivider(modifier= Modifier.height(100.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ScheduleScreen()
}

data class ShiftCardDetails(
    val shiftType: ShiftType,
    val shiftStart: String,
    val shiftEnd: String,
    val employees: List<EmployeeUiModel>,
    val shiftColor: Color
)