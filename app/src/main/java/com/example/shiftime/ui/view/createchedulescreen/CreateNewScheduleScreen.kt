package com.example.shiftime.ui.view.createchedulescreen

import android.widget.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.shiftime.models.ShiftType


@Composable
fun CreateScheduleScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ScreenHeaderBanner()
        AssignmentStyleDropdown()
        WeekPickerButton()
        WeekDaySelector()
        ShiftEditCard(
            shiftType = ShiftType.MORNING,
            startHour = "06:45",
            endHour = "14:45",
            onEditClick = { /* פתיחת דיאלוג */ }
        )
        Box(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            GenerateScheduleButton(onClick = {})
        }
    }

}

@Composable
fun ScreenHeaderBanner(
    title: String = "יצירת סידור חדש",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(
                RoundedCornerShape(
                    bottomStart = 60.dp,
                    bottomEnd = 60.dp
                )
            )
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
    selectedStyle: String = "איזון מירבי",
    onStyleSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val options = listOf("איזון מירבי", "מינימום חורים", "העדפות עובדים")
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "פתח תפריט",
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = selectedStyle,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(0.dp, 0.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onStyleSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun WeekPickerButton(
    onWeekSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = { TODO() },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(end = 16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "פתח תפריט",
                modifier = Modifier.padding(end = 4.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "בחר שבוע",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekDaySelector(){
    var selectedIndex by remember { mutableIntStateOf(0) }
    val days = listOf("א׳", "ב׳", "ג׳", "ד׳", "ה׳", "ו׳", "ש׳")
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        SingleChoiceSegmentedButtonRow {
            days.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = days.size
                    ),
                    onClick = { selectedIndex = index },
                    selected = index == selectedIndex,
                    label = { Text(label) },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ShiftEditCard(
    shiftType: ShiftType,
    startHour: String,
    endHour: String,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (label, color) = when (shiftType) {
        ShiftType.MORNING -> " בוקר" to Color(0xFFFFF59D)
        ShiftType.AFTERNOON -> " צהריים" to Color(0xFFFFCC80)
        ShiftType.NIGHT -> " לילה" to Color(0xFFB0BEC5)
    }

    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Text(
                    text = "$startHour - $endHour",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "ערוך משמרת",
                        tint = MaterialTheme.colorScheme.onSurface
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
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "יצירת סידור",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = "צור סידור חדש",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview (showBackground = true)
@Composable
fun CreateScheduleScreenPreview(){
    CreateScheduleScreen()
}