package com.example.pomodorotimer

import Debug.Companion.printTimerList
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import timerx.Timer
import timerx.buildTimer
import java.util.concurrent.TimeUnit

enum class TimerType {
    WORK, SHORT_BREAK, LONG_BREAK
}

class TimerFragment : Fragment() {
    private val timerTypes = mutableListOf(
        TimerType.WORK, TimerType.SHORT_BREAK, TimerType.WORK, TimerType.SHORT_BREAK,
        TimerType.WORK, TimerType.SHORT_BREAK, TimerType.WORK, TimerType.LONG_BREAK
    )

    private val timerDurations = mapOf(
        TimerType.WORK to 25L, // Work timer duration in minutes
        TimerType.SHORT_BREAK to 5L, // Short break timer duration in minutes
        TimerType.LONG_BREAK to 15L // Long break timer duration in minutes
    )

    private lateinit var text_time: TextView
    private lateinit var btn_start: TextView
    private lateinit var btn_stop: TextView
    private lateinit var btn_addtask: TextView
    private var state = false
    private var timerList: MutableList<Timer> = mutableListOf()
    private var currentTimerIndex = 0
    private lateinit var currentTimer: Timer
    private var pomsToAddValue: Int = 0

    //CHANGE TO MINUTES BEFORE PRODUCTION
    private val timeUnitToUse = TimeUnit.SECONDS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)

        text_time = view.findViewById(R.id.text_time)
        btn_start = view.findViewById(R.id.btn_start)
        btn_stop = view.findViewById(R.id.btn_stop)
        btn_addtask = view.findViewById(R.id.btn_addtask)

        // Initialize buttons and timer
        initButtons()
        initializeTimer()

        return view
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun flip(): Boolean {
        state = !state
        return state
    }

    @SuppressLint("SetTextI18n")
    private fun startNextTimer() {
        if (timerList.isNotEmpty()) {
            timerList.removeAt(0)
            if (timerList.isNotEmpty()) {
                currentTimer = timerList[0]
                text_time.text = currentTimer.remainingFormattedTime
            } else {
                showToast("All Pomodoros are done.")
            }
            btn_start.text = "Start"
            state = false
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val updatedPomsToAddValue = data?.getIntExtra("updatedPomsToAddValue", 0)
            if (updatedPomsToAddValue != null) {
                repeat(updatedPomsToAddValue) {
                    val timerType = timerTypes[(currentTimerIndex + timerList.size) % timerTypes.size]
                    val timerDuration = timerDurations[timerType] ?: 25L
                    val timer = createTimer(timerDuration)
                    timerList.add(timer)
                }
                if (timerList.isNotEmpty()) {
                    currentTimer = timerList[currentTimerIndex % timerList.size]
                    text_time.text = currentTimer.remainingFormattedTime
                }
            }
        }
        printTimerList(timerList)
    }

    private fun initializeTimer() {
        if (currentTimerIndex == 0 && timerList.isNotEmpty()) {
            currentTimer = timerList.first()
            text_time.text = currentTimer.remainingFormattedTime
        }
    }

    private fun createTimer(
        duration: Long,
        timeUnit: TimeUnit = timeUnitToUse,
        format: String = "MM:SS"
    ): Timer {
        return buildTimer {
            startFormat(format)
            startTime(duration, timeUnit)
            useExactDelay(true)
            onTick { millis, formattedTime ->
                text_time.text = formattedTime  // Update text_time for the current timer
                Log.i("Timer", "Remaining time = $millis")
            }
            onFinish {
                showToast("Finished!")
                startNextTimer()
            }
        }
    }





    @SuppressLint("SetTextI18n")
    private fun initButtons() {
        btn_start.setOnClickListener {
            if (timerList.isNotEmpty()) {
                if (!state) {
                    currentTimer.start()
                    flip()
                    btn_start.text = "Pause"
                } else {
                    btn_start.text = "Resume"
                    currentTimer.stop()
                    flip()
                }
            } else {
                showToast("There are no timers to run!")
            }
        }

        btn_stop.setOnClickListener {
            if (timerList.isNotEmpty()) {
                currentTimer.stop()
                currentTimer.reset()
                timerList.clear() // Clear the timer list
                currentTimerIndex = 0 // Reset the currentTimerIndex
                text_time.text = ""
            } else {
                showToast("There are no timers to stop!")
            }
        }

        btn_addtask.setOnClickListener {
            val intent = Intent(context, ActivityWindow::class.java)
            intent.putExtra("pomsToAddValue", pomsToAddValue)
            startActivityForResult(intent, 1)
        }
    }
}

