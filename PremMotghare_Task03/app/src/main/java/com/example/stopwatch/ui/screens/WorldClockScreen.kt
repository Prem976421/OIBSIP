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
import com.example.stopwatch.ui.theme.Primary40
import com.example.stopwatch.ui.theme.Secondary40
import com.example.stopwatch.ui.theme.SurfaceLight
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

@Composable
fun CityTimeCard(cityTime: CityTime) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
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
