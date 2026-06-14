package com.example.stopwatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stopwatch.ui.theme.*
import com.example.stopwatch.viewmodel.CityTime
import com.example.stopwatch.viewmodel.WorldClockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldClockScreen(viewModel: WorldClockViewModel = viewModel()) {
    val cityTimes by viewModel.cityTimes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .padding(top = 48.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            text = "World Clock",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Primary40,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            label = { Text("Search Timezone (e.g. Tokyo)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        if (searchResults.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                items(searchResults) { zone ->
                    Text(
                        text = zone,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.addZone(zone) }
                            .padding(16.dp),
                        fontSize = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(cityTimes, key = { it.city.name }) { cityTime ->
                CityTimeCard(cityTime)
            }
        }
    }
}

fun getFlagColors(zoneId: String): List<Color> {
    val id = zoneId.lowercase()
    return when {
        id.contains("tokyo") || id.contains("japan") -> listOf(Color.White, GoogleRed, Color.White)
        id.contains("london") || id.contains("europe") -> listOf(GoogleBlue, Color.White, GoogleRed)
        id.contains("new_york") || id.contains("america") -> listOf(GoogleRed, Color.White, GoogleBlue)
        id.contains("sydney") || id.contains("australia") -> listOf(GoogleBlue, Color.White, GoogleRed)
        id.contains("dubai") || id.contains("asia") -> listOf(GoogleGreen, Color.White, Color.Black)
        id.contains("paris") || id.contains("france") -> listOf(GoogleBlue, Color.White, GoogleRed)
        id.contains("calcutta") || id.contains("india") || id.contains("kolkata") -> listOf(Color(0xFFFF9933), Color.White, GoogleGreen)
        id.contains("brazil") || id.contains("sao_paulo") -> listOf(GoogleGreen, GoogleYellow, GoogleBlue)
        id.contains("canada") || id.contains("toronto") -> listOf(GoogleRed, Color.White, GoogleRed)
        id.contains("mexico") -> listOf(GoogleGreen, Color.White, GoogleRed)
        else -> listOf(GoogleBlue, GoogleRed, GoogleYellow, GoogleGreen) // Google Colors default
    }
}

@Composable
fun CityTimeCard(cityTime: CityTime) {
    val flagColors = getFlagColors(cityTime.city.zoneId)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        // Subtle flag colored border using a box at the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .align(Alignment.BottomCenter)
                .background(Brush.horizontalGradient(flagColors))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = cityTime.city.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary40
                )
                Text(
                    text = cityTime.timeDiffString,
                    fontSize = 14.sp,
                    color = Secondary40,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = cityTime.timeString,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    color = Primary40
                )
                Text(
                    text = cityTime.dateString,
                    fontSize = 14.sp,
                    color = Secondary40,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
