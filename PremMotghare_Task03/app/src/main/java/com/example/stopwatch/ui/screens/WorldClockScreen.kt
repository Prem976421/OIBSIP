package com.example.stopwatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

data class CountryCapital(val country: String, val capital: String, val zoneId: String, val flag: String)

val globalCapitals = listOf(
    CountryCapital("Japan", "Tokyo", "Asia/Tokyo", "🇯🇵"),
    CountryCapital("United Kingdom", "London", "Europe/London", "🇬🇧"),
    CountryCapital("United States", "Washington D.C.", "America/New_York", "🇺🇸"),
    CountryCapital("Australia", "Sydney", "Australia/Sydney", "🇦🇺"),
    CountryCapital("United Arab Emirates", "Dubai", "Asia/Dubai", "🇦🇪"),
    CountryCapital("France", "Paris", "Europe/Paris", "🇫🇷"),
    CountryCapital("India", "New Delhi", "Asia/Kolkata", "🇮🇳"),
    CountryCapital("Brazil", "Brasilia", "America/Sao_Paulo", "🇧🇷"),
    CountryCapital("Canada", "Ottawa", "America/Toronto", "🇨🇦"),
    CountryCapital("Mexico", "Mexico City", "America/Mexico_City", "🇲🇽"),
    CountryCapital("Germany", "Berlin", "Europe/Berlin", "🇩🇪"),
    CountryCapital("Italy", "Rome", "Europe/Rome", "🇮🇹"),
    CountryCapital("Spain", "Madrid", "Europe/Madrid", "🇪🇸"),
    CountryCapital("Russia", "Moscow", "Europe/Moscow", "🇷🇺"),
    CountryCapital("China", "Beijing", "Asia/Shanghai", "🇨🇳"),
    CountryCapital("South Korea", "Seoul", "Asia/Seoul", "🇰🇷"),
    CountryCapital("South Africa", "Johannesburg", "Africa/Johannesburg", "🇿🇦"),
    CountryCapital("Egypt", "Cairo", "Africa/Cairo", "🇪🇬"),
    CountryCapital("Argentina", "Buenos Aires", "America/Argentina/Buenos_Aires", "🇦🇷")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldClockScreen(viewModel: WorldClockViewModel = viewModel()) {
    val cityTimes by viewModel.cityTimes.collectAsState()
    
    var expanded by remember { mutableStateOf(false) }

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

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = "Select Country Capital",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                globalCapitals.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = "${item.flag} ${item.country} - ${item.capital}") },
                        onClick = {
                            viewModel.addZone(item.zoneId)
                            expanded = false
                        }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(cityTimes, key = { it.city.name }) { cityTime ->
                CityTimeCard(cityTime)
            }
        }
    }
}

fun getFlagEmojiFallback(zoneId: String): String {
    val match = globalCapitals.find { it.zoneId == zoneId }
    return match?.flag ?: "🌍"
}

@Composable
fun CityTimeCard(cityTime: CityTime) {
    val flagEmoji = if (cityTime.city.isLocal) "📍" else getFlagEmojiFallback(cityTime.city.zoneId)
    
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
