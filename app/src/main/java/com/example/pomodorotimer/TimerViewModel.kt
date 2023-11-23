package com.example.pomodorotimer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timerx.Timer

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val _timerList = MutableLiveData<List<Timer>>()
    val timerList: LiveData<List<Timer>> get() = _timerList

    private val _timerDuration = MutableLiveData<Map<String, Long>>()
    val timerDuration: LiveData<Map<String, Long>> get() = _timerDuration

    private val timerRepository = TimerRepository(application)

    init {
        // Fetch preferences once and observe changes
        timerRepository.observePreferences { preferences ->
            val timerDurations = preferences ?: getDefaultTimerDurations()
            _timerDuration.value = timerDurations
        }
    }


    private fun getDefaultTimerDurations(): Map<String, Long> {
        // Provide default values if needed
        return mapOf(
            "WORK" to 25L,
            "SHORT_BREAK" to 5L,
            "LONG_BREAK" to 15L
        )
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
}
