package com.example.pomodorotimer

import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    private val timerViewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("SettingsFragment", "ON CREATE")

        // Apply the theme when the fragment is created
        applyTheme()

        findPreference<Preference>("selected_theme")?.onPreferenceChangeListener = this

        findPreference<Preference>("WORK")?.onPreferenceChangeListener = this
        findPreference<Preference>("SHORT_BREAK")?.onPreferenceChangeListener = this
        findPreference<Preference>("LONG_BREAK")?.onPreferenceChangeListener = this

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d("SettingsFragment", "ACTIVITY CREATED")

        applyTheme()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>("selected_theme")?.onPreferenceChangeListener = this

        findPreference<Preference>("WORK")?.onPreferenceChangeListener = this
        findPreference<Preference>("SHORT_BREAK")?.onPreferenceChangeListener = this
        findPreference<Preference>("LONG_BREAK")?.onPreferenceChangeListener = this

        Log.d("SettingsFragment", "CREATED PREFERENCES")
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        if (preference.key == "selected_theme") {
            // Handle theme changes here
            val selectedTheme = newValue.toString()
            saveSelectedTheme(selectedTheme) // Save the selected theme
            applyTheme() // Apply the selected theme
        }
        Log.d("SettingsFragment", "ATTEMPTING TO CHECK PREFERENCES")
        when (preference.key) {
            "WORK", "SHORT_BREAK", "LONG_BREAK" -> {
                if (newValue is String) {
                    Log.d("SettingsFragment", "Duration value is a String: $newValue")
                } else {
                    Log.d("SettingsFragment", "Duration value is not a String. Type: ${newValue?.javaClass}")
                    saveTimerDuration(preference.key, newValue)
                }
            }
        }
        return true
    }


    private fun saveTimerDuration(timerKey: String, duration: Any?) {
        Log.d("SettingsFragment", "Saving timer duration for key $timerKey")

        if (duration is Long) {
            val sharedPreferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
            val editor = sharedPreferences?.edit()
            editor?.putLong(timerKey, duration)
            editor?.apply()

            Log.d("SettingsFragment", "Timer duration saved successfully.")
        } else {
            Log.e("SettingsFragment", "Invalid duration type: ${duration?.javaClass}")
        }
    }



    // Save the selected theme in SharedPreferences
    private fun saveSelectedTheme(theme: String) {
        val sharedPreferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        val editor = sharedPreferences?.edit()
        editor?.putString("selected_theme", theme)
        editor?.apply()
    }

    // Retrieve the selected theme from SharedPreferences
    private fun getSelectedTheme(): String? {
        val sharedPreferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        return sharedPreferences?.getString("selected_theme", "tomato")
         // Default to tomato theme
    }



    private fun applyTheme() {

        when (getSelectedTheme()) {
            "tomato" ->applyTomatoTheme()
            "kiwi" -> applyKiwiTheme()
            "strawberry" -> applyStrawberryTheme()
            "watermelon" -> applyWatermelonTheme()
        }
    }

    private fun applyTomatoTheme() {
        // Implement your tomato theme (default theme)
    }
    private fun applyKiwiTheme() {
        // Apply Kiwi theme colors to your views
        // Example: set background color of your views
        view?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_light_primary))
    }

    private fun applyStrawberryTheme() {
        // Apply Strawberry theme colors to your views
        // Example: set background color of your views
        view?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.strawberryBackground))
    }
    private fun applyWatermelonTheme() {
        // Implement your watermelon theme
    }

}
