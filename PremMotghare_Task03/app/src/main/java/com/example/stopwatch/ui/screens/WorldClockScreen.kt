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
            label = { Text("Search Timezone") },
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

fun getFlagEmoji(zoneId: String): String {
    val id = zoneId.lowercase()
    return when {
        id.contains("tokyo") || id.contains("japan") -> "🇯🇵"
        id.contains("london") || id.contains("europe/london") -> "🇬🇧"
        id.contains("new_york") || id.contains("america") -> "🇺🇸"
        id.contains("sydney") || id.contains("australia") -> "🇦🇺"
        id.contains("dubai") || id.contains("asia/dubai") -> "🇦🇪"
        id.contains("paris") || id.contains("france") -> "🇫🇷"
        id.contains("calcutta") || id.contains("india") || id.contains("kolkata") -> "🇮🇳"
        id.contains("brazil") || id.contains("sao_paulo") -> "🇧🇷"
        id.contains("canada") || id.contains("toronto") -> "🇨🇦"
        id.contains("mexico") -> "🇲🇽"
        id.contains("berlin") || id.contains("germany") -> "🇩🇪"
        id.contains("rome") || id.contains("italy") -> "🇮🇹"
        id.contains("madrid") || id.contains("spain") -> "🇪🇸"
        else -> "🌍"
    }
}

@Composable
fun CityTimeCard(cityTime: CityTime) {
    val flagEmoji = if (cityTime.city.isLocal) "📍" else getFlagEmoji(cityTime.city.zoneId)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = flagEmoji,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = cityTime.city.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary40
                    )
                }
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
