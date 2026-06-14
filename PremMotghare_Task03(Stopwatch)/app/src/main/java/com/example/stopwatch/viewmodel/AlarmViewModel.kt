package com.example.stopwatch.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import com.example.stopwatch.receiver.AlarmReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

data class Alarm(
    val id: Int,
    val hour: Int,
    val minute: Int,
    val label: String,
    var isEnabled: Boolean
) {
    val timeStr: String get() = String.format("%02d:%02d", hour, minute)
}

class AlarmViewModel(private val app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences("alarms_prefs", Context.MODE_PRIVATE)
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val _alarms = MutableStateFlow<List<Alarm>>(emptyList())
    val alarms: StateFlow<List<Alarm>> = _alarms.asStateFlow()

    init {
        loadAlarms()
    }

    private fun loadAlarms() {
        val alarmsSet = prefs.getStringSet("alarms", emptySet()) ?: emptySet()
        val loaded = alarmsSet.mapNotNull {
            val parts = it.split("|")
            if (parts.size == 5) {
                Alarm(parts[0].toInt(), parts[1].toInt(), parts[2].toInt(), parts[3], parts[4].toBoolean())
            } else null
        }.sortedBy { it.hour * 60 + it.minute }
        _alarms.value = loaded
    }

    private fun saveAlarms() {
        val alarmsSet = _alarms.value.map {
            "${it.id}|${it.hour}|${it.minute}|${it.label}|${it.isEnabled}"
        }.toSet()
        prefs.edit().putStringSet("alarms", alarmsSet).apply()
    }

    fun toggleAlarm(id: Int) {
        val updated = _alarms.value.map {
            if (it.id == id) {
                val toggled = it.copy(isEnabled = !it.isEnabled)
                if (toggled.isEnabled) scheduleAlarm(toggled) else cancelAlarm(toggled)
                toggled
            } else it
        }
        _alarms.value = updated
        saveAlarms()
    }

    fun deleteAlarm(id: Int) {
        val alarm = _alarms.value.find { it.id == id }
        if (alarm != null) {
            cancelAlarm(alarm)
            _alarms.update { list -> list.filter { it.id != id } }
            saveAlarms()
        }
    }

    fun addAlarm(hour: Int, minute: Int, label: String) {
        val newId = (_alarms.value.maxOfOrNull { it.id } ?: 0) + 1
        val newAlarm = Alarm(newId, hour, minute, label, true)
        val updated = (_alarms.value + newAlarm).sortedBy { it.hour * 60 + it.minute }
        _alarms.value = updated
        saveAlarms()
        scheduleAlarm(newAlarm)
    }

    private fun scheduleAlarm(alarm: Alarm) {
        val intent = Intent(app, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("ALARM_LABEL", alarm.label)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            app, alarm.id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
        }
        
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Cannot schedule exact alarms, fallback to inexact or skip
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    private fun cancelAlarm(alarm: Alarm) {
        val intent = Intent(app, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            app, alarm.id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}

