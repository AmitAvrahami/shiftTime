package com.example.shiftime.presentation.ui.view.employeeselectionscreen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shiftime.R
import com.example.shiftime.presentation.ui.common.state.EmployeeUiModel
import com.example.shiftime.presentation.ui.events.modelevents.EmployeeConstraintsEvent
import com.example.shiftime.presentation.ui.events.uievents.UiEvent
import com.example.shiftime.presentation.ui.view.viewmodels.EmployeeConstraintsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeSelectionScreen(
    viewModel: EmployeeConstraintsViewModel = hiltViewModel(),
    onNavigateToEmployeeConstraints: (Long) -> Unit = {}, //TODO : IMPLEMENT
    onNavigateBack: () -> Unit = {}, //TODO : IMPLEMENT
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val employeesState by viewModel.employeesState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.NavigateTo -> {
                    //TODO: IMPLEMENT
                    onNavigateToEmployeeConstraints(event.route.toLong())
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "בחר עובד",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "חזור",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                employeesState.isLoading -> {
                    LoadingState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                employeesState.error != null -> {
                    ErrorState(
                        error = employeesState.error ?: "שגיאה",
                        onRetry = {
                            //TODO:implement retry logic
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                employeesState.employees.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    EmployeesGrid(
                        employees = employeesState.employees,
                        onEmployeeClick = { employeeId -> viewModel.onEvent(EmployeeConstraintsEvent.SelectEmployee(employeeId))},
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmployeesGrid(
    employees: List<EmployeeUiModel>,
    onEmployeeClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = modifier
    ) {
        items(
            items = employees,
            key = { employee -> employee.id }
        ) { employee ->
            EnhancedEmployeeCard(
                employee = employee,
                onClick = { onEmployeeClick(employee.id) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
private fun EnhancedEmployeeCard(
    employee: EmployeeUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // רקע מטושטש עם צורה גלית
            WavyClippedImage(
                imagePainter = painterResource(id = R.drawable.img),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .blur(8.dp)
            )

            // תוכן הכרטיס
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                // תמונת פרופיל עם מסגרת
                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .drawBehind {
                                drawCircle(
                                    color = Color.Blue,
                                    style = Stroke(width = 6.dp.toPx())
                                )
                            }
                    )

                    Image(
                        painter = painterResource(id = employee.employeeImage),
                        contentDescription = "תמונת ${employee.employeeName}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // תפקיד
                Text(
                    text = employee.employeeDesignation,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // שם העובד
                Text(
                    text = employee.employeeName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}

@Composable
private fun WavyClippedImage(
    imagePainter: Painter,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.clip(WavyShapeFixed())
    ) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
    }
}

class WavyShapeFixed : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, size.height * 0.6f)

            val waveHeight = 30f
            val waveLength = size.width / 3

            quadraticTo(
                waveLength * 0.5f,
                size.height * 0.6f + waveHeight,
                waveLength,
                size.height * 0.6f
            )

            quadraticTo(
                waveLength * 1.5f,
                size.height * 0.6f - waveHeight,
                waveLength * 2f,
                size.height * 0.6f
            )

            quadraticTo(
                waveLength * 2.5f,
                size.height * 0.6f + waveHeight,
                size.width,
                size.height * 0.6f
            )

            lineTo(size.width, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
private fun LoadingState(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "טוען עובדים...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(32.dp)
    ) {
        Text(
            text = "אופס! משהו השתבש",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("נסה שוב")
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(32.dp)
    ) {
        Text(
            text = "אין עובדים במערכת",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "נראה שטרם הוספת עובדים למערכת.\nעבור למסך ניהול עובדים כדי להוסיף עובד ראשון.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}