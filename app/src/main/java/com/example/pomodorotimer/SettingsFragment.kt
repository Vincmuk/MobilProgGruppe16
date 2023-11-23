package com.example.pomodorotimer

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    private var timerRepository: TimerRepository? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        timerRepository = TimerRepository(requireActivity())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        Log.d("SettingsFragment", "onCreatePreferences called")

        val workPreference: EditTextPreference? = findPreference("WORK")
        workPreference?.onPreferenceChangeListener = this


        workPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
        val shortBreakPreference: EditTextPreference? = findPreference("SHORT_BREAK")
        shortBreakPreference?.onPreferenceChangeListener = this

        shortBreakPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
        val longBreakPreference: EditTextPreference? = findPreference("LONG_BREAK")
        longBreakPreference?.onPreferenceChangeListener = this

        longBreakPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

    }
    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        Log.d("SettingsFragment", "onPreferenceChange called")

        // Assuming you have a reference to TimerRepository in your fragment
        timerRepository?.let { repo ->
            when (preference.key) {
                "WORK", "SHORT_BREAK", "LONG_BREAK" -> {
                    if (newValue is String) {
                        try {
                            // Try to parse the String to Long
                            val value = newValue.toLong()
                            repo.updatePreference(preference.key, value)
                        } catch (e: NumberFormatException) {
                            Log.e("SettingsFragment", "Invalid number format for ${preference.key}")
                        }
                    }
                }
            }
        }

        return true
    }


}

