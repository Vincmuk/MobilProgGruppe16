package com.example.pomodorotimer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val timerFragment = TimerFragment()
    private val settingsFragment = SettingsFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, timerFragment)
            .commit()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.page_home

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.page_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, timerFragment)
                        .commitNow()
                    true
                }
                R.id.page_settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, settingsFragment)
                        .commitNow()
                    true
                }
                R.id.page_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, profileFragment)
                        .commitNow()
                    true
                }
                else -> false
            }
        }
    }
}
