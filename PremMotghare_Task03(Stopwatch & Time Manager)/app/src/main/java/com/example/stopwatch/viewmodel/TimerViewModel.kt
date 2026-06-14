package com.example.stopwatch.viewmodel

import android.app.Application
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _totalTimeMillis = MutableStateFlow(0L)
    val totalTimeMillis: StateFlow<Long> = _totalTimeMillis.asStateFlow()

    private val _remainingTimeMillis = MutableStateFlow(0L)
    val remainingTimeMillis: StateFlow<Long> = _remainingTimeMillis.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private var timerJob: Job? = null
    private var lastStartTime = 0L

    fun setTime(hours: Int, minutes: Int, seconds: Int) {
        val totalMillis = (hours * 3600 + minutes * 60 + seconds) * 1000L
        _totalTimeMillis.value = totalMillis
        _remainingTimeMillis.value = totalMillis
    }

    fun togglePlayPause() {
        if (_isPlaying.value) pause() else start()
    }

    private fun playTimerFinishedSound() {
        viewModelScope.launch {
            try {
                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                val ringtone = RingtoneManager.getRingtone(app, uri)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ringtone.isLooping = true
                }
                ringtone.audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                
                ringtone.play()
                
                delay(10000)
                
                if (ringtone.isPlaying) {
                    ringtone.stop()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun start() {
        if (_remainingTimeMillis.value <= 0) return
        
        lastStartTime = System.currentTimeMillis()
        _isPlaying.value = true
        
        val initialRemaining = _remainingTimeMillis.value
        
        timerJob = viewModelScope.launch {
            while (isActive && _isPlaying.value && _remainingTimeMillis.value > 0) {
                val elapsed = System.currentTimeMillis() - lastStartTime
                val currentRemaining = initialRemaining - elapsed
                
                if (currentRemaining <= 0) {
                    _remainingTimeMillis.value = 0
                    _isPlaying.value = false
                    playTimerFinishedSound()
                    break
                } else {
                    _remainingTimeMillis.value = currentRemaining
                }
                delay(10L)
            }
        }
    }

    private fun pause() {
        if (!_isPlaying.value) return
        _isPlaying.value = false
        timerJob?.cancel()
    }

    fun reset() {
        pause()
        _remainingTimeMillis.value = _totalTimeMillis.value
    }
    
    fun clear() {
        pause()
        _totalTimeMillis.value = 0L
        _remainingTimeMillis.value = 0L
    }
}
