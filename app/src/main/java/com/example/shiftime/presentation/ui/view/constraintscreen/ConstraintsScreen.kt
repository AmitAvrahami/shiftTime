package com.example.shiftime.presentation.ui.view.constraintscreen

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.shiftime.R
import com.example.shiftime.presentation.ui.common.state.EmployeeUiModel
import kotlin.io.path.Path
import kotlin.io.path.moveTo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConstraintsScreen(
    modifier: Modifier = Modifier,
    employeesUiModel: List<EmployeeUiModel> = SampleData.employees
) {
    Scaffold(
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
                        modifier=Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { /*TODO*/ },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(64.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(employeesUiModel.size) { index ->
                    EmployeeCard(employee = employeesUiModel[index])
                }
            }
        }
    }
}


@Composable
fun EmployeeCard(
    employee: EmployeeUiModel
){
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(0.4f),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ){
            WavyClippedImage(
                imagePainter = painterResource(id = R.drawable.img),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .blur(8.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top=32.dp)
            ) {
                Image(
                    painter = painterResource(id = employee.employeeImage),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.clip(CircleShape).size(60.dp)
                )

                Text(
                    text = employee.employeeDesignation,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    modifier = Modifier.padding(top = 8.dp)

                )
                Text(
                    text = employee.employeeName,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)

                )
            }
        }




    }

}


@Composable
fun WavyClippedImage(
    imagePainter: Painter,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(WavyShapeFixed())
    ) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
    }
}

class WavyShapeFixed : androidx.compose.ui.graphics.Shape{
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, size.height * 0.5f)

            val waveHeight = 40f
            val waveLength = size.width / 2

            // יוצר גל אחד
            quadraticBezierTo(
                waveLength / 2, size.height * 0.4f + waveHeight,
                waveLength, size.height * 0.4f
            )
            quadraticBezierTo(
                waveLength * 1.5f, size.height * 0.4f - waveHeight,
                size.width, size.height * 0.4f
            )
            lineTo(size.width, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ConstraintsScreen()
}



object SampleData{
    private val employeeImage = R.drawable.ic_launcher_background
    val EmployeeUiModel = EmployeeUiModel(
        employeeImage = R.drawable.ic_launcher_background,
        employeeName = "Employee Name",
        employeeDesignation = "Employee Designation",
        isExpanded = false,
        id = TODO(),
        employeeId = TODO(),
        employeePhone = TODO(),
        minShifts = TODO(),
        maxShifts = TODO(),
//        employeeId = 1,
    )

    val employees: List<EmployeeUiModel> = List(8) { index ->
        EmployeeUiModel(
            employeeImage = R.drawable.img,
            employeeName = "עובד מספר $index",
            employeeDesignation = "בקר ביטחון",
            id = TODO(),
            employeeId = TODO(),
            employeePhone = TODO(),
            minShifts = TODO(),
            maxShifts = TODO(),
            isExpanded = TODO()
        )
    }
}