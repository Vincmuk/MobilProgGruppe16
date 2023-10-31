package com.example.pomodorotimer

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val timerFragment = TimerFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the initial fragment (com.example.pomodorotimer.TimerFragment)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, timerFragment)
            .commit()

        // Find the navigation buttons
        val homeButton = findViewById<Button>(R.id.button_home)
        val settingsButton = findViewById<Button>(R.id.button_settings)
        val profileButton = findViewById<Button>(R.id.button_profile)

        // Set click listeners to navigate to the corresponding fragments
        homeButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, timerFragment)
                .commit()
        }

        settingsButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, settingsFragment)
                .commit()
        }

        profileButton.setOnClickListener {
            // Navigate to the ProfileFragment (replace with your actual ProfileFragment)
            // For example:
            // supportFragmentManager.beginTransaction()
            //     .replace(R.id.fragment_container, profileFragment)
            //     .commit()
        }
    }
}
