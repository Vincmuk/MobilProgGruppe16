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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    //map the types to a timer instead of rewriting the whole timer class
    private val timerTypeMap: MutableMap<Timer, TimerType> = mutableMapOf()

    private lateinit var recyclerView: RecyclerView
    private lateinit var timerAdapter: TimerAdapter

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

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        timerAdapter = TimerAdapter(timerList, timerTypeMap)
        recyclerView.adapter = timerAdapter

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
        // Show a toast message with the given text
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun flip(): Boolean {
        // Toggle the state (true/false) and return the new state
        state = !state
        return state
    }

    @SuppressLint("SetTextI18n")
    private fun startNextTimer() {
        // Start the next timer in the list
        if (timerList.isNotEmpty()) {
            timerAdapter.removeTimer(0)
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
        // Handle the result of the activity for adding tasks
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val updatedPomsToAddValue = data?.getIntExtra("updatedPomsToAddValue", 0)
            if (updatedPomsToAddValue != null) {
                //updatedPomsToAddValue * 2 because we want 1 pomodoro to be a break, and we repeat to make 1 work + 1 break per unit input
                repeat(updatedPomsToAddValue*2) {
                    val timerType = timerTypes[(currentTimerIndex + timerList.size) % timerTypes.size]
                    val timerDuration = timerDurations[timerType] ?: 25L
                    val timer = createTimer(timerDuration, timerType)
                    timerAdapter.addTimer(timer)
                }
                if (timerList.isNotEmpty()) {
                    val lastTimer = timerList.last()
                    val lastTimerType = timerTypeMap[lastTimer]

                    if (lastTimerType != null && lastTimerType != TimerType.WORK) {
                        timerAdapter.removeTimer(timerList.size - 1)
                    }
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
        // Initialize the current timer if it's the first timer in the list
        if (currentTimerIndex == 0 && timerList.isNotEmpty()) {
            currentTimer = timerList.first()
            text_time.text = currentTimer.remainingFormattedTime
        }
    }

    private fun createTimer(
        duration: Long,
        timerType: TimerType,
        timeUnit: TimeUnit = timeUnitToUse,
        format: String = "MM:SS"
    ): Timer {
        // Create and configure a timer with the given parameters
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
        }.also {
            timerTypeMap[it] = timerType
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initButtons() {
        // Initialize and handle button click events
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
            // Stop and reset the current timer and clear the timer list
            if (timerList.isNotEmpty()) {
                currentTimer.stop()
                currentTimer.reset()
                timerAdapter.clearTimers() // Clear the timer list
                currentTimerIndex = 0 // Reset the currentTimerIndex
                text_time.text = "00:00"
            } else {
                showToast("There are no timers to stop!")
            }
        }

        btn_addtask.setOnClickListener {
            // Start an activity for adding tasks
            val intent = Intent(context, ActivityWindow::class.java)
            intent.putExtra("pomsToAddValue", pomsToAddValue)
            startActivityForResult(intent, 1)
        }
    }
}
