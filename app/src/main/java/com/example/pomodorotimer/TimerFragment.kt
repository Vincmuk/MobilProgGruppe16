package com.example.pomodorotimer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

    //map the types to a timer instead of rewriting the whole timer class
    private val timerTypeMap: MutableMap<Timer, TimerType> = mutableMapOf()

    private val timerViewModel: TimerViewModel by viewModels()
    private val sessionViewModel: SessionViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var timerAdapter: TimerAdapter
    private lateinit var sessionAdapter: SessionAdapter

    private lateinit var text_time: TextView
    private lateinit var btn_start: TextView
    private lateinit var btn_stop: TextView
    private lateinit var btn_addtask: TextView
    private var state = false
    private var currentTimerIndex = 0
    private lateinit var currentTimer: Timer


    //CHANGE TO MINUTES BEFORE PRODUCTION
    private val timeUnitToUse = TimeUnit.SECONDS

    private var timerRepository: TimerRepository? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        timerRepository = TimerRepository(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)

        Log.d("TimerFragment", "${timerRepository?.getWorkDuration()}")
        Log.d("TimerFragment", "${timerRepository?.getShortBreakDuration()}")
        Log.d("TimerFragment", "${timerRepository?.getLongBreakDuration()}")

        Log.d("TimerFragment", "${timerViewModel.timerList.value}")

        recyclerView = view.findViewById(R.id.recyclerViewTimer)
        recyclerView.layoutManager = LinearLayoutManager(context)
        timerAdapter = TimerAdapter(timerTypeMap)
        sessionAdapter = SessionAdapter()
        recyclerView.adapter = timerAdapter


        timerViewModel.timerList.observe(viewLifecycleOwner) { timers ->
            timerAdapter.submitList(timers)
            if (timers.isNotEmpty()) {
                text_time.text = timers[0].remainingFormattedTime
            } else {
                text_time.text = "00:00"
            }
        }

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

    private fun startNextTimer() {
        if (timerAdapter.itemCount > 0) {
            timerAdapter.removeFirstTimer {
                val updatedList = timerAdapter.currentList
                if (updatedList.isNotEmpty()) {
                    currentTimer = updatedList[0]
                    text_time.text = currentTimer.remainingFormattedTime
                } else {
                    timerViewModel.clearTimers {
                    showToast("All Pomodoros are done")
                    Log.d("TimerFragment", "${timerViewModel.timerList.value}")
                    }
                }
                // Update the timer list in the ViewModel
                timerViewModel.setTimers(updatedList) {
                    btn_start.text = "Start"
                    state = false
                }
            }
        }
    }

    // https://stackoverflow.com/questions/73452871/creating-a-vibrator-for-android-in-kotlin
    @RequiresApi(Build.VERSION_CODES.S)
    private fun vibrateDevice() {
        if (context != null) {
            if (Build.VERSION.SDK_INT >= 31) {
                val vibratorManager =
                    requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createOneShot(1000L, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                val v = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= 26) {
                    v.vibrate(VibrationEffect.createOneShot(1000L, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    v.vibrate(VibrationEffect.createOneShot(1000L, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Handle the result of the activity for adding tasks
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val updatedPomsToAddValue = data?.getIntExtra("updatedPomsToAddValue", 0)
            val sessionName = data?.getStringExtra("sessionName")
            if (updatedPomsToAddValue != null) {
                val newTimerList = createNewTimerList(updatedPomsToAddValue)

                // Remove the last break as it is not needed
                if (newTimerList.isNotEmpty()) {
                    removeLastBreak(newTimerList)

                    // Save the session and set the new timer list to the adapter
                    handleNewTimerList(newTimerList, sessionName)
                }
            }
        }
    }

    private fun createNewTimerList(updatedPomsToAddValue: Int): MutableList<Timer> {
        val newTimerList = mutableListOf<Timer>()
        Log.d("Timer Fragment", "Attempting to create list")

        val workDuration = timerRepository?.getWorkDuration() ?: 25L
        val shortBreakDuration = timerRepository?.getShortBreakDuration() ?: 5L
        val longBreakDuration = timerRepository?.getLongBreakDuration() ?: 15L

        repeat(updatedPomsToAddValue * 2) {
            val timerType = timerTypes[(currentTimerIndex + newTimerList.size) % timerTypes.size]
            val timerDuration = when (timerType) {
                TimerType.WORK -> workDuration
                TimerType.SHORT_BREAK -> shortBreakDuration
                TimerType.LONG_BREAK -> longBreakDuration
            }
            val timer = createTimer(timerDuration, timerType)
            newTimerList.add(timer)
        }

        return newTimerList
    }


    private fun removeLastBreak(newTimerList: MutableList<Timer>) {
        val lastTimer = newTimerList.last()
        val lastTimerType = timerTypeMap[lastTimer]
        if (lastTimerType != null && lastTimerType != TimerType.WORK) {
            newTimerList.removeLast()
        }
    }

    private fun handleNewTimerList(newTimerList: List<Timer>, sessionName: String?) {
        if (sessionName != null) {
            saveSession(sessionName, newTimerList)
        }
        if (newTimerList.isNotEmpty()) {
            timerViewModel.setTimers(newTimerList) {
                currentTimer = newTimerList[currentTimerIndex % newTimerList.size]
                text_time.text = currentTimer.remainingFormattedTime
            }
        }
    }

    private fun initializeTimer() {
        // Initialize the current timer if it's the first timer in the list
        if (currentTimerIndex == 0 && timerAdapter.currentList.isNotEmpty()) {
            currentTimer = timerAdapter.currentList.first()
            text_time.text = currentTimer.remainingFormattedTime
        } else {
            text_time.text = "00:00"
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    vibrateDevice()
                }
                sessionViewModel.getOldestNonCompletedSession()?.let { context?.let { it1 -> sessionViewModel.updateAndSaveSession(it, it1) } }
                context?.let { playSound(it) }
                startNextTimer()
            }
        }.also {
            timerTypeMap[it] = timerType
        }
    }

    private fun playSound(context: Context) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.pop
        )
        mediaPlayer.start()
    }

    // Extracted function for initializing buttons
    @SuppressLint("SetTextI18n")
    private fun initButtons() {
        btn_start.setOnClickListener {
            handleStartButtonClick()
        }

        btn_stop.setOnClickListener {
            handleStopButtonClick()
        }

        btn_addtask.setOnClickListener {
            startAddTaskActivity()
        }
    }

    // Extracted function for handling start button click
    @SuppressLint("SetTextI18n")
    private fun handleStartButtonClick() {
        if (timerAdapter.currentList.isNotEmpty()) {
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

    // Extracted function for handling stop button click
    private fun handleStopButtonClick() {
        if (timerAdapter.currentList.isNotEmpty()) {
            currentTimer.stop()
            currentTimer.reset()

            // Clear the timer list in the ViewModel
            timerViewModel.clearTimers() {
                currentTimerIndex = 0
                text_time.text = "00:00"
            }
        } else {
            showToast("There are no timers to stop!")
        }
    }

    // Extracted function for starting the AddTask activity
    private fun startAddTaskActivity() {
        val intent = Intent(context, ActivityWindow::class.java)
        startActivityForResult(intent, 1)
    }
    private fun saveSession(sessionName: String, timers: List<Timer>) {
        val session = Session.create(sessionName, timers)
        println(session)
        sessionViewModel.addSession(session)
    }

}
