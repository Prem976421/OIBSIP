package com.example.stopwatch.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class WorldCity(
    val name: String,
    val zoneId: String,
    val isLocal: Boolean = false
)

data class CityTime(
    val city: WorldCity,
    val timeString: String,
    val dateString: String,
    val timeDiffString: String
)

class WorldClockViewModel(private val app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences("worldclock_prefs", Context.MODE_PRIVATE)

    private val _cityTimes = MutableStateFlow<List<CityTime>>(emptyList())
    val cityTimes: StateFlow<List<CityTime>> = _cityTimes.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<String>>(emptyList())
    val searchResults: StateFlow<List<String>> = _searchResults.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd")
    
    private var savedZones = mutableListOf<String>()

    init {
        loadSavedZones()
        viewModelScope.launch {
            while (isActive) {
                updateTimes()
                delay(1000L)
            }
        }
    }

    private fun loadSavedZones() {
        val zones = prefs.getStringSet("zones", setOf("Europe/London", "America/New_York", "Asia/Tokyo")) ?: emptySet()
        savedZones = zones.toMutableList()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.length >= 2) {
            val allZones = ZoneId.getAvailableZoneIds()
            _searchResults.value = allZones.filter { it.contains(query, ignoreCase = true) }.take(10)
        } else {
            _searchResults.value = emptyList()
        }
    }

    fun addZone(zoneIdStr: String) {
        if (!savedZones.contains(zoneIdStr)) {
            savedZones.add(zoneIdStr)
            prefs.edit().putStringSet("zones", savedZones.toSet()).apply()
            updateTimes()
        }
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    private fun updateTimes() {
        val localZone = ZoneId.systemDefault()
        val localTime = ZonedDateTime.now(localZone)
        
        val allCities = mutableListOf(WorldCity("Local Time", localZone.id, isLocal = true))
        savedZones.forEach { zoneIdStr ->
            val name = zoneIdStr.split("/").last().replace("_", " ")
            allCities.add(WorldCity(name, zoneIdStr))
        }

        _cityTimes.value = allCities.map { city ->
            val zoneId = try { ZoneId.of(city.zoneId) } catch (e:Exception) { ZoneId.systemDefault() }
            val zonedDateTime = ZonedDateTime.now(zoneId)
            
            val diffHours = (zonedDateTime.offset.totalSeconds - localTime.offset.totalSeconds) / 3600
            val diffString = when {
                city.isLocal -> "Current Time"
                diffHours == 0 -> "Same time"
                diffHours > 0 -> "+$diffHours HRS"
                else -> "$diffHours HRS"
            }

            CityTime(
                city = city,
                timeString = zonedDateTime.format(timeFormatter),
                dateString = zonedDateTime.format(dateFormatter),
                timeDiffString = diffString
            )
        }
    }
}
