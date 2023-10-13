package com.example.pomodorotimer

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.pomodorotimer.ui.theme.PomodoroTimerTheme
import timerx.Timer
import timerx.buildTimer
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodoroTimerTheme {
                Myapp()
            }
        }

        setContentView(R.layout.activity_main)


        val text_time = findViewById<TextView>(R.id.text_time)
        val btn_start = findViewById<TextView>(R.id.btn_start)
        val btn_stop = findViewById<TextView>(R.id.btn_stop)
        val btn_pause = findViewById<TextView>(R.id.btn_pause)
        val btn_addtask = findViewById<TextView>(R.id.btn_addtask)
        val btn_addtask2 = findViewById<TextView>(R.id.btn_addtask2)
        val btn_addtask3 = findViewById<TextView>(R.id.btn_addtask3)
        val btn_addtask4 = findViewById<TextView>(R.id.btn_addtask4)



        var cTimer: CountDownTimer? = null
        lateinit var timer: Timer

        timer = buildTimer {
            startFormat("MM:SS:L")
            startTime(15, TimeUnit.MINUTES)
            useExactDelay(true)
            onTick { millis, formattedTime ->
                text_time.text = formattedTime
                Log.i("Timer", "Remainingtime = $millis")
            }
            onFinish {
                showToast("Finished!")
            }
        }

        text_time.text = timer.formattedStartTime

        btn_start.setOnClickListener { timer.start() }
        btn_stop.setOnClickListener {
            timer.stop()
            timer.setTime(15, TimeUnit.MINUTES)
            text_time.text = timer.remainingFormattedTime
        }
        btn_pause.setOnClickListener { timer.stop() }




    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    @Preview()
    @Composable
    fun GreetingPreview() {
        PomodoroTimerTheme {
            Myapp()
        }
    }



}