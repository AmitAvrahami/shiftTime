//package com.example.shiftime.ui.view.shiftavailabilityscreen
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Checkbox
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.example.shiftime.presentation.ui.common.state.EmployeeUiModel
//import com.example.shiftime.presentation.ui.view.constraintscreen.SampleData
//import com.example.shiftime.presentation.ui.view.schedulescreen.DateSelector
//import com.example.shiftime.presentation.ui.view.schedulescreen.DayItemSelector
//import com.example.shiftime.presentation.ui.view.schedulescreen.MonthAndYearSelector
//import com.example.shiftime.utils.enums.ShiftType
//import com.example.shiftime.utils.HebrewDateMap
//import com.example.shiftime.utils.HebrewDateMap.monthDays
//import java.util.Date
//
//@Composable
//fun ShiftAvailabilityScreen() {
//    val shifts = listOf(
//        ShiftType.MORNING to "06:45" to "14:45",
//        ShiftType.AFTERNOON to "14:45" to "22:45",
//        ShiftType.NIGHT to "22:45" to "06:45"
//    )
//    val selectedMonth = remember { mutableStateOf("ינואר") }
//    val selectedDay = remember { mutableStateOf(1) }
//    var fromDay by remember { mutableStateOf(0) }
//    var toDay by remember { mutableStateOf(7) }
//
//    val daysInMonth = HebrewDateMap.monthDays[selectedMonth.value] ?: 30
//    val daysList = HebrewDateMap.generateDaysList(daysInMonth)
//    var selectedMonthIndex = HebrewDateMap.getMonthNumber(selectedMonth.value)
//    var date = Date(Date().year, selectedMonthIndex, selectedDay.value)
//
//    val visibleDays = daysList.subList(
//        fromDay,
//        toDay.coerceAtMost(daysList.size)
//    )
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//    ) {
//
//        EmployeeHeader(
//            employeeUiModel = SampleData.employees.first()
//        )
//        DateSelector(
//            dayList = visibleDays,
//            monthAndYearSelector = {
//                MonthAndYearSelector(
//                    selectedMonth = selectedMonth.value,
//                    onMonthSelected = {
//                        selectedMonth.value = it
//                        fromDay = 0
//                        toDay = 7
//                        selectedDay.value = fromDay + 1
//                    },
//                    onNextWeekClicked = {
//                        if (fromDay + 7 < daysList.size) {
//                            fromDay += 7
//                            toDay += 7
//                        } else {
//                            fromDay = 0
//                            toDay = 7
//                            val months = monthDays.keys.toList()
//                            val currentMonthIndex = months.indexOf(selectedMonth.value)
//                            if (currentMonthIndex < months.size - 1) {
//                                selectedMonth.value = months[currentMonthIndex + 1]
//                            } else {
//                                selectedMonth.value = months[0]
//                            }
//                        }
//                        selectedDay.value = fromDay + 1 // עדכון נכון
//                    },
//                    onPreviousWeekClicked = {
//                        if (fromDay - 7 >= 0) {
//                            fromDay -= 7
//                            toDay -= 7
//                        } else {
//                            fromDay = 0
//                            toDay = 7
//                            val months = monthDays.keys.toList()
//                            val currentMonthIndex = months.indexOf(selectedMonth.value)
//                            if (currentMonthIndex > 0) {
//                                selectedMonth.value = months[currentMonthIndex - 1]
//                            } else {
//                                selectedMonth.value = months.last()
//                            }
//                            val lastDayOfMonth = monthDays[selectedMonth.value] ?: 30
//                            fromDay = lastDayOfMonth - 6
//                            toDay = lastDayOfMonth
//                            selectedDay.value = fromDay + 1 // עדכון נכון
//
//                        }
//                        selectedDay.value = fromDay + 1
//                    },
//                )
//            },
//            dayItemSelector = { day ->
//                DayItemSelector(
//                    dayName = "יום",
//                    monthDay = day.toString(),
//                    isSelected = day == selectedDay.value,
//                    onClick = { selectedDay.value = day }
//                )
//            },
//            modifier = Modifier.fillMaxWidth()
//        )
//        DayTitle(
//            modifier = Modifier.padding(16.dp),
//            day = "יום שני"
//        )
//        shifts.forEach { (typeAndFrom, to) ->
//            ShiftAvailabilityCard(shiftType = typeAndFrom.first, fromHour = typeAndFrom.second, toHour = to)
//        }
//
//    }
//}
//
//
//@Composable
//fun EmployeeHeader(
//    employeeUiModel: EmployeeUiModel,
//    modifier: Modifier = Modifier
//){
//    Row(
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = modifier
//            .fillMaxWidth()
//            .background(MaterialTheme.colorScheme.surfaceVariant)
//            .padding(16.dp)
//
//    ) {
//        Image(
//            painter = painterResource(employeeUiModel.employeeImage),
//            contentDescription = null,
//            modifier = Modifier
//                .size(64.dp)
//                .clip(CircleShape)
//        )
//        Spacer(modifier = Modifier.width(16.dp))
//        Column(
//            modifier = Modifier,
//            verticalArrangement = Arrangement.spacedBy(4.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = employeeUiModel.employeeDesignation,
//                fontSize = MaterialTheme.typography.titleSmall.fontSize,
//                fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//            Text(
//                text = employeeUiModel.employeeName,
//                fontSize = MaterialTheme.typography.titleMedium.fontSize,
//                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
//
//
//            )
//        }
//    }
//}
//
//@Composable
//fun DayTitle(
//    modifier: Modifier = Modifier,
//    day: String
//){
//    Box(
//        contentAlignment = Alignment.CenterEnd,
//        modifier = modifier.fillMaxWidth()
//    ){
//        Text(
//            text = day,
//            fontSize = MaterialTheme.typography.titleMedium.fontSize,
//            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
//            textAlign = TextAlign.End
//        )
//    }
//}
//
//@Composable
//fun ShiftAvailabilityCard(
//    shiftType: ShiftType,
//    fromHour: String,
//    toHour: String,
//    modifier: Modifier = Modifier
//){
//   val shiftTypeStyle = getShiftCardStyle(shiftType)
//    val shiftTypeText = shiftTypeStyle.first
//    val cardColor = shiftTypeStyle.second
//
//    Card(
//        colors = CardDefaults.cardColors(cardColor),
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//            .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
//
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(
//                text = "$fromHour - $toHour",
//                fontSize = MaterialTheme.typography.titleMedium.fontSize,
//                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
//                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
//            )
//            Text(
//                text = shiftTypeText,
//                fontSize = MaterialTheme.typography.titleMedium.fontSize,
//                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
//            )
//            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp),color = Color.Black)
//        }
//            AvailabilityOptionRow()
//    }
//}
//
//fun getShiftCardStyle(type: ShiftType): Pair<String, Color> = when(type) {
//    ShiftType.MORNING -> "משמרת בוקר" to Color(0xFFFFF176)
//    ShiftType.AFTERNOON -> "משמרת צהריים" to Color(0xFFFFCC80)
//    ShiftType.NIGHT -> "משמרת לילה" to Color(0xFFB0BEC5)
//}
//
//@Composable
//fun AvailabilityOptionRow(
//    isAvailable: Boolean =false,
//    isNotAvailable: Boolean = false,
//    onAvailableChange: (Boolean) -> Unit = {},
//    onNotAvailableChange: (Boolean) -> Unit = {},
//    modifier: Modifier = Modifier
//){
//    Column(
//        modifier = modifier.fillMaxWidth().padding(8.dp),
//        horizontalAlignment = Alignment.End
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//
//        ) {
//            Text(
//                text = "יכול לעבוד"
//            )
//            Checkbox(
//                checked = false,
//                onCheckedChange = {})
//        }
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            Text(
//                text = "לא יכול לעבוד"
//            )
//            Checkbox(
//                checked = false,
//                onCheckedChange = {})
//        }
//    }
//}
//
//
//
//
//@Composable
//@Preview(showBackground = true)
//fun ShiftAvailabilityScreenPreview() {
//    ShiftAvailabilityScreen()
//}
//
