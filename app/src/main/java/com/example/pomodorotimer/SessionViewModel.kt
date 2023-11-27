package com.example.pomodorotimer

import Debug
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timerx.Timer
import timerx.buildTimer
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class SessionViewModel : ViewModel() {
    private val _sessionList = MutableLiveData<List<Session>>()
    val sessionList: LiveData<List<Session>> get() = _sessionList

    fun setSessions(newSessions: List<Session>) {
        _sessionList.value = newSessions
    }

    fun loadExistingSessions(context: Context) {
        try {
            val sessionList = mutableListOf<Session>()
            val fileList = context.fileList()

            fileList.forEach { fileName ->
                if (fileName.startsWith("completed_session_") && fileName.endsWith(".json")) {
                    try {
                        val sessionId = fileName.removeSuffix(".json").substringAfterLast("_").toLong()
                        val jsonString = context.openFileInput(fileName).bufferedReader().use(BufferedReader::readText)
                        val jsonSession = JSONObject(jsonString)

                        val sessionName = jsonSession.getString("sessionName")
                        val hasEnded = jsonSession.getBoolean("hasEnded")
                        val startTime = parseFormattedTime(jsonSession.getString("startTime"))
                        val endTime = parseFormattedTime(jsonSession.getString("endTime"))

                        val jsonTimers = jsonSession.getJSONArray("timers")
                        val timers = mutableListOf<Timer>()

                        for (i in 0 until jsonTimers.length()) {
                            try {
                                val jsonTimer = jsonTimers.getJSONObject(i)
                                val formattedStartTime = jsonTimer.getString("formattedStartTime")
                                val timer = buildTimer {
                                    startFormat("MM:SS")
                                    startTime(convertFormattedTimeToSeconds(formattedStartTime), TimeUnit.SECONDS)
                                }
                                timers.add(timer)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }

                        val session = Session(sessionId, sessionName, timers, hasEnded, startTime, endTime)
                        sessionList.add(session)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            // Set the nextId for creating new sessions
            Session.nextId = (sessionList.maxByOrNull { it.sessionId }?.sessionId ?: 0) + 1
            setSessions(sessionList)
            println(sessionList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Function to parse formatted time string to milliseconds
    fun parseFormattedTime(formattedTime: String): Long {
        val TIME_FORMAT = "HH:mm"
        val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        val date = sdf.parse(formattedTime)
        return date?.time ?: 0L
    }

    // Function to convert formatted time string to seconds
    private fun convertFormattedTimeToSeconds(formattedTime: String): Long {
        val parts = formattedTime.split(":")
        if (parts.size == 2) {
            val minutes = parts[0].toLongOrNull() ?: 0L
            val seconds = parts[1].toLongOrNull() ?: 0L
            return TimeUnit.MINUTES.toSeconds(minutes) + seconds
        }
        return 0L
    }

    // Function to add a new session
    fun addSession(session: Session) {
        val currentList = _sessionList.value.orEmpty()
        val newList = currentList.toMutableList().apply { add(session) }
        _sessionList.value = newList
    }

    // Function to get the oldest non-completed session, we assume that this is the current session.
    fun getOldestNonCompletedSession(): Session? {
        return _sessionList.value.orEmpty().firstOrNull { !it.hasEnded }
    }

    // Function to update and save a session
    fun updateAndSaveSession(session: Session, context: Context) {
        session.updateSession()
        // Save the completed session to JSON
        if (session.hasEnded) {
        session.saveCompletedSessionToJSON(context)
        }
    }
}

data class Session(
    val sessionId: Long,
    val sessionName: String,
    var timers: MutableList<Timer>,
    var hasEnded: Boolean = false,
    var startTime: Long = 0L,
    var endTime: Long = 0L
) {
    companion object {
        var nextId: Long = 1
        private const val TIME_FORMAT = "HH:mm"
        fun create(sessionName: String, timers: List<Timer>): Session {
            val newSession = Session(nextId++, sessionName, timers.toMutableList())
            newSession.startTime = System.currentTimeMillis()
            return newSession
        }
    }

    // Function to update the session status based on timers
    fun updateSession() {
        // Check if the last timer in the list has completed
        if (timers.lastOrNull()?.remainingTimeInMillis == 0L) {
            hasEnded = true
            endTime = System.currentTimeMillis()
        }
    }

    // Function to save a completed session to JSON
    fun saveCompletedSessionToJSON(context: Context) {
        try {
            val jsonSession = JSONObject().apply {
                put("sessionId", sessionId)
                put("sessionName", sessionName)
                put("hasEnded", hasEnded)
                put("startTime", getFormattedTime(startTime))
                put("endTime", getFormattedTime(endTime))

                val jsonTimers = JSONArray()
                timers.forEachIndexed { index, timer ->
                    val jsonTimer = JSONObject().apply {
                        put("timerIndex", index)
                        put("formattedStartTime", timer.formattedStartTime)
                    }
                    jsonTimers.put(jsonTimer)
                }
                put("timers", jsonTimers)
            }

            // Convert JSON object to a string
            val jsonString = jsonSession.toString()
            println(jsonSession.toString())

            val fileName = "completed_session_${sessionId}.json"
            val fileOutputStream: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)

            OutputStreamWriter(fileOutputStream).use { writer ->
                writer.write(jsonString)
                println("Written to data/data/com.example.pomodorotimer/files")
            }
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Function to get formatted time string from milliseconds
    fun getFormattedTime(timeInMillis: Long): String {
        val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        return sdf.format(calendar.time)
    }

    // Override toString() for debugging purposes
    override fun toString(): String {
        return "Session(id=$sessionId, name='$sessionName', hasEnded=$hasEnded, " +
                "startTime=${getFormattedTime(startTime)}, endTime=${getFormattedTime(endTime)}) Timers: ${Debug.printTimerList(timers)}"
    }

}

