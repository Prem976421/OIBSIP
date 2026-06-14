package com.example.stopwatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class Lap(
    val id: Int,
    val lapTime: Long,
    val totalTime: Long
)

class StopwatchViewModel : ViewModel() {

    private val _timeMillis = MutableStateFlow(0L)
    val timeMillis: StateFlow<Long> = _timeMillis.asStateFlow()

    private val _currentLapTimeMillis = MutableStateFlow(0L)
    val currentLapTimeMillis: StateFlow<Long> = _currentLapTimeMillis.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _laps = MutableStateFlow<List<Lap>>(emptyList())
    val laps: StateFlow<List<Lap>> = _laps.asStateFlow()

    private val _fastestLapId = MutableStateFlow<Int?>(null)
    val fastestLapId: StateFlow<Int?> = _fastestLapId.asStateFlow()

    private val _slowestLapId = MutableStateFlow<Int?>(null)
    val slowestLapId: StateFlow<Int?> = _slowestLapId.asStateFlow()

    private var timerJob: Job? = null
    private var lastStartTime = 0L
    private var accumulatedTime = 0L
    private var lastLapTotalTime = 0L

    fun togglePlayPause() {
        if (_isPlaying.value) pause() else start()
    }

    private fun start() {
        if (_isPlaying.value) return
        
        lastStartTime = System.currentTimeMillis()
        _isPlaying.value = true
        
        timerJob = viewModelScope.launch {
            while (isActive && _isPlaying.value) {
                val currentAccTime = accumulatedTime + (System.currentTimeMillis() - lastStartTime)
                _timeMillis.value = currentAccTime
                _currentLapTimeMillis.value = currentAccTime - lastLapTotalTime
                delay(10L) // Update every 10ms for smooth UI
            }
        }
    }

    private fun pause() {
        if (!_isPlaying.value) return
        
        accumulatedTime += System.currentTimeMillis() - lastStartTime
        _timeMillis.value = accumulatedTime
        _currentLapTimeMillis.value = accumulatedTime - lastLapTotalTime
        _isPlaying.value = false
        timerJob?.cancel()
    }

    fun reset() {
        pause()
        accumulatedTime = 0L
        lastLapTotalTime = 0L
        _timeMillis.value = 0L
        _currentLapTimeMillis.value = 0L
        _laps.value = emptyList()
        _fastestLapId.value = null
        _slowestLapId.value = null
    }

    fun lap() {
        if (!_isPlaying.value && _timeMillis.value == 0L) return
        
        val currentTotalTime = if (_isPlaying.value) {
            accumulatedTime + (System.currentTimeMillis() - lastStartTime)
        } else {
            accumulatedTime
        }
        
        val lapTime = currentTotalTime - lastLapTotalTime
        lastLapTotalTime = currentTotalTime
        
        val newLap = Lap(
            id = _laps.value.size + 1,
            lapTime = lapTime,
            totalTime = currentTotalTime
        )
        
        _currentLapTimeMillis.value = 0L
        
        _laps.update { currentLaps ->
            val updatedLaps = listOf(newLap) + currentLaps
            updateLapHighlights(updatedLaps)
            updatedLaps
        }
    }

    private fun updateLapHighlights(lapsList: List<Lap>) {
        if (lapsList.size < 2) {
            _fastestLapId.value = null
            _slowestLapId.value = null
            return
        }
        
        val fastest = lapsList.minByOrNull { it.lapTime }
        val slowest = lapsList.maxByOrNull { it.lapTime }
        
        _fastestLapId.value = fastest?.id
        _slowestLapId.value = slowest?.id
    }

    fun deleteLap(lapId: Int) {
        _laps.update { currentLaps ->
            val updatedLaps = currentLaps.filter { it.id != lapId }
            updateLapHighlights(updatedLaps)
            updatedLaps
        }
    }
}
