package com.example.pomodorotimer

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData

class TimerRepository(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _preferencesLiveData = MutableLiveData<Map<String, Long>>()

    init {
        // Initialize with the current preferences
        _preferencesLiveData.value = loadPreferences()

        // Register a listener to observe changes in preferences
        preferences.registerOnSharedPreferenceChangeListener { _, _ ->
            // Update LiveData when preferences change
            _preferencesLiveData.value = loadPreferences()
        }
    }

    fun getWorkDuration(): Long = preferences.getLong("WORK", DEFAULT_WORK_DURATION)

    fun getShortBreakDuration(): Long = preferences.getLong("SHORT_BREAK", DEFAULT_SHORT_BREAK_DURATION)

    fun getLongBreakDuration(): Long = preferences.getLong("LONG_BREAK", DEFAULT_LONG_BREAK_DURATION)


    fun observePreferences(listener: (Map<String, Long>?) -> Unit) {
        _preferencesLiveData.observeForever { listener(it) }
    }

    private fun loadPreferences(): Map<String, Long> {
        return mapOf(
            "WORK" to preferences.getLong("WORK", DEFAULT_WORK_DURATION),
            "SHORT_BREAK" to preferences.getLong("SHORT_BREAK", DEFAULT_SHORT_BREAK_DURATION),
            "LONG_BREAK" to preferences.getLong("LONG_BREAK", DEFAULT_LONG_BREAK_DURATION)
        )
    }

    // Function to update a specific preference
    fun updatePreference(key: String, value: Long) {
        preferences.edit {
            Log.d("TimerRepository", "$key + $value")
            putLong(key, value)
        }
    }

    companion object {
        private const val PREFS_NAME = "com.example.pomodorotimer.preferences"
        private const val DEFAULT_WORK_DURATION = 25L
        private const val DEFAULT_SHORT_BREAK_DURATION = 5L
        private const val DEFAULT_LONG_BREAK_DURATION = 15L
    }
}
