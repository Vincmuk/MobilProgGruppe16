package com.example.pomodorotimer

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply the theme when the fragment is created
        applyTheme()

        // Set up listener for theme preference
        findPreference<Preference>("selected_theme")?.onPreferenceChangeListener = this
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Apply the theme when the activity is created (can be important when returning from another activity)
        applyTheme()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Set up listener for theme preference
        findPreference<Preference>("selected_theme")?.onPreferenceChangeListener = this
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        if (preference.key == "selected_theme") {
            // Handle theme changes here
            val selectedTheme = newValue.toString()
            saveSelectedTheme(selectedTheme) // Save the selected theme
            applyTheme() // Apply the selected theme
        }
        return true
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
            "kiwi" -> AppKiwiTheme()
            "strawberry" -> applyStrawberryTheme()
            "watermelon" -> applyWatermelonTheme()
        }
    }

    private fun applyTomatoTheme() {
        // Implement your tomato theme (default theme)
    }
    private fun AppKiwiTheme() {
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
