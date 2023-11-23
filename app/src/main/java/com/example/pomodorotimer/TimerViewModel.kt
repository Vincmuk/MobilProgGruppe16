package com.example.pomodorotimer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import timerx.Timer

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val _timerList = MutableLiveData<List<Timer>>()
    val timerList: LiveData<List<Timer>> get() = _timerList

    private val _timerDurations = MutableLiveData<Map<String, Long>>()
    val timerDurations: LiveData<Map<String, Long>> get() = _timerDurations

    init {
        // Initialize LiveData with default values or load from SharedPreferences
        _timerDurations.value = loadTimerDurations()
    }

    private fun loadTimerDurations(): Map<String, Long> {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
        val defaultDurations = mapOf(
            "WORK" to 25L,
            "SHORT_BREAK" to 5L,
            "LONG_BREAK" to 15L
        )
        return defaultDurations.mapValues { (key, defaultValue) ->
            sharedPreferences.getLong(key, defaultValue)
        }
    }

    fun setTimers(newTimers: List<Timer>, onComplete: () -> Unit) {
        if (newTimers.isNotEmpty()) {
            _timerList.value = newTimers
            onComplete()
        }
    }

    fun clearTimers(onComplete: () -> Unit) {
        if (_timerList.value?.isNotEmpty() == true) {
            _timerList.value = emptyList()
            onComplete()
        }
    }

    fun setTimerDurations(durations: Map<String, Long>) {
        _timerDurations.value = durations
    }
}
