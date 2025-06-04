package com.example.shiftime.presentation.ui.view.homescreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.Navigation
import androidx.navigation.compose.rememberNavController
import com.example.shiftime.R
import com.example.shiftime.data.local.entity.ShiftWithEmployeesEntity
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.model.ShiftWithEmployees
import com.example.shiftime.presentation.ui.events.modelevents.HomeEvent
import com.example.shiftime.presentation.ui.events.uievents.UiEvent
import com.example.shiftime.presentation.ui.view.homescreen.utils.ManagerMessage
import com.example.shiftime.presentation.ui.view.homescreen.utils.StatusLevel
import com.example.shiftime.presentation.ui.view.homescreen.utils.SystemStatus
import com.example.shiftime.presentation.ui.view.viewmodels.HomeViewModel
import com.example.shiftime.utils.enums.Role
import com.example.shiftime.utils.enums.ShiftType
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedHomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToEmployees: () -> Unit ={},
    onNavigateToShifts: () -> Unit={},
    onNavigateToSettings: () -> Unit={},
    onNavigateToReports: () -> Unit={},
    onNavigateToConstraints: () -> Unit={},
    navController: NavController = rememberNavController()
) {
    val state by viewModel.homeState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.NavigateTo -> {
                    navController.navigate(event.route)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            HomeTopAppBar(
                currentUser = null,
                onSettingsClick = onNavigateToSettings
            )
        },
        bottomBar = {
            HomeBottomNavigationBar(
                onEmployeesClick = onNavigateToEmployees,
                onShiftsClick = onNavigateToShifts,
                onReportsClick = onNavigateToReports,
                onConstraintsClick = onNavigateToConstraints
            )
        }
    ) { paddingValues ->

        if (state.isLoading) {
            LoadingScreen(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                // כותרת ברוכים הבאים
                item {
                    WelcomeSection(
                        currentUser = null,
                        currentTime = state.scheduleStatus.time
                    )
                }

                item {
                    QuickActionsSection(
                        onViewCurrentSchedule = {
                            viewModel.onEvent(HomeEvent.ViewCurrentSchedule)
                            onNavigateToShifts()
                        },
                        onCreateNewSchedule = {
                            viewModel.onEvent(HomeEvent.CreateNewSchedule)
                        },
                        onManageEmployees = onNavigateToEmployees,
                        onManageConstraints = onNavigateToConstraints
                    )
                }

//                // הודעת מצב מערכת
//                if (state.systemStatus != null) {
//                    item {
//                        SystemStatusCard(systemStatus = state.systemStatus!!)
//                    }
//                }

                // סטטיסטיקות מהירות
                item {
                    QuickStatsSection(
                        totalEmployees = state.scheduleStatus.employees.size,
                        activeShifts = state.scheduleStatus.activeShifts,
                        pendingAssignments = state.scheduleStatus.pendingAssignments
                    )
                }

                // המשמרת הנוכחית או הבאה
                if (state.scheduleStatus.currentShift != null || state.scheduleStatus.nextShift != null) {
                    item {
                        CurrentShiftSection(
                            currentShift = state.scheduleStatus.currentShift,
                            nextShift = state.scheduleStatus.nextShift
                        )
                    }
                }

                // הודעות מנהל
//                if (state.managerMessages.isNotEmpty()) {
//                    item {
//                        ManagerMessagesSection(
//                            messages = state.managerMessages,
//                            onViewAllMessages = {
//                                viewModel.onEvent(HomeEvent.ViewAllMessages)
//                            }
//                        )
//                    }
//                }

                // רשימת עובדים פעילים היום
                if (state.scheduleStatus.todayActiveEmployees.isNotEmpty()) {
                    item {
                        TodayActiveEmployeesSection(
                            employees = state.scheduleStatus.todayActiveEmployees,
                            onViewAllEmployees = onNavigateToEmployees
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    currentUser: Employee?,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "ShiftTime",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            // הודעות
            IconButton(onClick = { /* TODO: הודעות */ }) {
                Badge(
                    modifier = Modifier.offset(x = 2.dp, y = (-2).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "הודעות"
                    )
                }
            }

            // הגדרות
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "הגדרות"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun HomeBottomNavigationBar(
    onEmployeesClick: () -> Unit,
    onShiftsClick: () -> Unit,
    onReportsClick: () -> Unit,
    onConstraintsClick: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        NavigationBarItem(
            selected = true, // Home is selected
            onClick = { /* Already on home */ },
            icon = { Icon(Icons.Default.Home, contentDescription = "בית") },
            label = { Text("בית") }
        )

        NavigationBarItem(
            selected = false,
            onClick = onEmployeesClick,
            icon = { Icon(Icons.Default.Person, contentDescription = "עובדים") },
            label = { Text("עובדים") }
        )

        NavigationBarItem(
            selected = false,
            onClick = onShiftsClick,
            icon = { Icon(Icons.Default.DateRange, contentDescription = "משמרות") },
            label = { Text("משמרות") }
        )

        NavigationBarItem(
            selected = false,
            onClick = onConstraintsClick,
            icon = { Icon(Icons.Default.Clear, contentDescription = "אילוצים") },
            label = { Text("אילוצים") }
        )

        NavigationBarItem(
            selected = false,
            onClick = onReportsClick,
            icon = { Icon(Icons.Default.MailOutline, contentDescription = "דוחות") },
            label = { Text("דוחות") }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WelcomeSection(
    currentUser: Employee?,
    currentTime: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = getGreeting(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = currentUser?.let { "${it.firstName} ${it.lastName}" } ?: "משתמש",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "תמונת פרופיל",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onViewCurrentSchedule: () -> Unit,
    onCreateNewSchedule: () -> Unit,
    onManageEmployees: () -> Unit,
    onManageConstraints: () -> Unit
) {
    Column {
        Text(
            text = "פעולות מהירות",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            item {
                QuickActionCard(
                    icon = Icons.Default.DateRange,
                    title = "הצג סידור",
                    subtitle = "נוכחי",
                    onClick = onViewCurrentSchedule
                )
            }

            item {
                QuickActionCard(
                    icon = Icons.Default.Create,
                    title = "צור סידור",
                    subtitle = "חדש",
                    onClick = onCreateNewSchedule
                )
            }

            item {
                QuickActionCard(
                    icon = Icons.Default.Person,
                    title = "נהל עובדים",
                    subtitle = "הוסף/עדכן",
                    onClick = onManageEmployees
                )
            }

            item {
                QuickActionCard(
                    icon = Icons.Default.Clear,
                    title = "אילוצים",
                    subtitle = "עובדים",
                    onClick = onManageConstraints
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SystemStatusCard(systemStatus: SystemStatus) {
    val (backgroundColor, textColor, icon) = when (systemStatus.level) {
        StatusLevel.SUCCESS -> Triple(
            Color(0xFF4CAF50).copy(alpha = 0.1f),
            Color(0xFF2E7D32),
            Icons.Default.CheckCircle
        )
        StatusLevel.WARNING -> Triple(
            Color(0xFFFF9800).copy(alpha = 0.1f),
            Color(0xFFE65100),
            Icons.Default.Warning
        )
        StatusLevel.ERROR -> Triple(
            Color(0xFFF44336).copy(alpha = 0.1f),
            Color(0xFFC62828),
            Icons.Default.Info
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = systemStatus.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = systemStatus.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun QuickStatsSection(
    totalEmployees: Int,
    activeShifts: Int,
    pendingAssignments: Int
) {
    Column {
        Text(
            text = "מבט כללי",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(
                value = totalEmployees.toString(),
                label = "עובדים",
                icon = Icons.Default.Person,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            StatCard(
                value = activeShifts.toString(),
                label = "משמרות פעילות",
                icon = Icons.Default.DateRange,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            StatCard(
                value = pendingAssignments.toString(),
                label = "ממתינים לשיבוץ",
                icon = Icons.Default.PlayArrow,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CurrentShiftSection(
    currentShift: ShiftWithEmployees?,
    nextShift: ShiftWithEmployees?
) {
    Column {
        Text(
            text = "משמרות",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        currentShift?.let { shift ->
            EnhancedShiftCard(
                title = "משמרת נוכחית",
                shift = shift,
                isActive = true
            )
        }

        nextShift?.let { shift ->
            Spacer(modifier = Modifier.height(8.dp))
            EnhancedShiftCard(
                title = "משמרת הבאה",
                shift = shift,
                isActive = false
            )
        }
    }
}

@Composable
private fun EnhancedShiftCard(
    title: String,
    shift: ShiftWithEmployees,
    isActive: Boolean
) {

    val shiftTypeLabel = when (shift.shift.shiftType) {
        ShiftType.MORNING -> "בוקר"
        ShiftType.AFTERNOON -> "צהריים"
        ShiftType.NIGHT -> "לילה"
    }

    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val startTime = timeFormatter.format(shift.shift.startTime)
    val endTime = timeFormatter.format(shift.shift.endTime)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$title: $shiftTypeLabel",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "$startTime - $endTime",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isActive) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "פעיל",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            if (shift.employees.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "עובדים במשמרת:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(shift.employees) { employee ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "${employee.firstName} ${employee.lastName}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ManagerMessagesSection(
    messages: List<ManagerMessage>,
    onViewAllMessages: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "הודעות מנהל",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onViewAllMessages) {
                    Text("הצג הכל")
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            messages.take(3).forEach { message ->
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.MailOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = message.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayActiveEmployeesSection(
    employees: List<Employee>,
    onViewAllEmployees: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "עובדים פעילים היום",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onViewAllEmployees) {
                Text("הצג הכל")
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(employees.take(5)) { employee ->
                ActiveEmployeeCard(employee = employee)
            }
        }
    }
}

@Composable
private fun ActiveEmployeeCard(employee: Employee) {
    Card(
        modifier = Modifier.width(120.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "${employee.firstName} ${employee.lastName}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${employee.firstName} ${employee.lastName}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = if (employee.role == Role.MANAGER) "מנהל" else "עובד",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "טוען נתונים...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Helper functions
@RequiresApi(Build.VERSION_CODES.O)
private fun getGreeting(): String {
    val hour = java.time.LocalTime.now().hour
    return when (hour) {
        in 5..11 -> "בוקר טוב"
        in 12..16 -> "צהריים טובים"
        in 17..21 -> "ערב טוב"
        else -> "לילה טוב"
    }
}