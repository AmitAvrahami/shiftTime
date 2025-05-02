package com.example.shiftime.ui.view.homescreen

import android.media.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.ShifTimeTheme
import com.example.shiftime.R
import com.example.shiftime.models.Employee
import com.example.shiftime.models.ShiftType

@Composable
fun MainScreen() {
    ShifTimeTheme {
        val buttonsDetails = listOf(
            ButtonDetails(
                icon = Icons.Default.DateRange,
                text = "הצג סידור נוכחי",
                onClick = {}
            ),
            ButtonDetails(
                icon = Icons.Default.Create,
                text = "צור סידור חדש",
                onClick = {}
            )
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(painter = painterResource(id = R.drawable.img)
                , contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(4.dp))
            ActionButtonsRow(
                buttonsDetails = buttonsDetails
            )
            HorizontalDivider(modifier = Modifier.padding(16.dp))
            ShiftCard()
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
            ShiftCard()
            HorizontalDivider(modifier = Modifier.padding(top = 16.dp, end = 16.dp, start = 16.dp))
            Text(
                text= "הודעות מנהל",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, end = 8.dp),
                textAlign = TextAlign.End,
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
            )

            ManagerMessageCard()
        }
    }
}

@Composable
fun ActionButtonsRow(
    modifier: Modifier = Modifier,
    buttonsDetails: List<ButtonDetails>
)
{
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
           buttonsDetails.forEach { btn->
               Column(
                   modifier = Modifier.padding(16.dp),
                   horizontalAlignment = Alignment.CenterHorizontally,
                   verticalArrangement = Arrangement.spacedBy(12.dp),
               ) {
              LargeFloatingActionButton(
                  onClick = btn.onClick,
                  containerColor = MaterialTheme.colorScheme.primary,
              )
              {
                  Icon(
                      imageVector = btn.icon,
                      contentDescription = btn.text
                  )
              }
                   Text(
                       text = btn.text,
                       style = MaterialTheme.typography.bodyMedium,
                       textAlign = TextAlign.Center,

                   )
                   }
           }

    }
}

@Composable
fun ShiftCard(
    title: String = "משמרת נוכחית",
    shiftType: ShiftType = ShiftType.MORNING,
    employees: List<String> = listOf("עובד א", "עובד ב"),
    image: Int = R.drawable.img
) {
    val employeesStr = employees.joinToString(", ")
    val shiftTypeLabel = when (shiftType) {
        ShiftType.MORNING -> "בוקר"
        ShiftType.AFTERNOON -> "צהריים"
        ShiftType.NIGHT -> "לילה"
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.elevatedCardColors()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "$title: $shiftTypeLabel",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "עובדים: $employeesStr",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Image(
                painter = painterResource(image),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .height(72.dp)
                    .width(72.dp)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun  ManagerMessageCard(
    messages: List<String> = listOf("הודעה 1", "הודעה 2", "הודעה 3")
)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(shape = MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colorScheme.primary
            )
    ) {
        messages.forEach { message ->
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        ElevatedButton (
            onClick = {  },
            elevation = ButtonDefaults.buttonElevation(16.dp),
            modifier = Modifier
                .padding(8.dp)
            ) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
            Text(text = "לכל ההודעות")
        }
    }
}



@Composable
@Preview(showBackground = true)
fun PreviewSquareButton(){
    MainScreen()
}

data class ButtonDetails(
    val icon: ImageVector,
    val text: String,
    val onClick: () -> Unit
)