package com.example.pomodorotimer

import Debug
import android.content.Context
import android.util.Log
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
        Log.d("SessionViewModel", "setSessions called with ${newSessions.size} sessions")
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
                        val startTime = parseFormattedTime(jsonSession.getString("startTime")) // Handle missing or invalid startTime
                        val endTime = parseFormattedTime(jsonSession.getString("endTime"))    // Handle missing or invalid endTime

                        val jsonTimers = jsonSession.getJSONArray("timers")
                        val timers = mutableListOf<Timer>()

                        for (i in 0 until jsonTimers.length()) {
                            try {
                                val jsonTimer = jsonTimers.getJSONObject(i)
                                // Extract timer properties and create Timer objects
                                val formattedStartTime = jsonTimer.getString("formattedStartTime")
                                // Add any other timer properties you saved

                                val timer = buildTimer {
                                    startFormat("MM:SS")
                                    startTime(convertFormattedTimeToSeconds(formattedStartTime), TimeUnit.SECONDS)
                                }
                                timers.add(timer)
                            } catch (e: JSONException) {
                                // Handle the exception for individual timers (log, ignore, etc.)

                                e.printStackTrace()
                            }
                        }

                        val session = Session(sessionId, sessionName, timers, hasEnded, startTime, endTime)
                        sessionList.add(session)
                    } catch (e: Exception) {
                        // Handle the exception for the entire session (log, ignore, etc.)
                        e.printStackTrace()
                    }
                }
            }
            Session.nextId = sessionList.maxByOrNull { it.sessionId }?.sessionId?.plus(1) ?: 1

            // Update the _sessionList LiveData with the loaded sessions
            setSessions(sessionList)
            println(sessionList)
        } catch (e: Exception) {
            // Handle JSON deserialization or file reading exception
            e.printStackTrace()
        }
    }

    fun parseFormattedTime(formattedTime: String): Long {
        val TIME_FORMAT = "HH:mm"
        val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        val date = sdf.parse(formattedTime)
        return date?.time ?: 0L
    }

    private fun convertFormattedTimeToSeconds(formattedTime: String): Long {
        val parts = formattedTime.split(":")
        if (parts.size == 2) {
            val minutes = parts[0].toLongOrNull() ?: 0L
            val seconds = parts[1].toLongOrNull() ?: 0L
            return TimeUnit.MINUTES.toSeconds(minutes) + seconds
        }
        return 0L
    }

    fun addSession(session: Session) {
        val currentList = _sessionList.value.orEmpty()
        val newList = currentList.toMutableList().apply { add(session) }
        _sessionList.value = newList
    }

    fun getOldestNonCompletedSession(): Session? {
        return _sessionList.value.orEmpty().firstOrNull { !it.hasEnded }
    }

    fun updateAndSaveSession(session: Session, context: Context) {
        // Update the session (e.g., mark it as completed)
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
            val newSession = Session(nextId, sessionName, timers.toMutableList())
            newSession.startTime = System.currentTimeMillis()
            nextId++
            return newSession
        }
    }

    fun updateSession() {
        // Check if the last timer in the list has completed
        if (timers.lastOrNull()?.remainingTimeInMillis == 0L) {
            hasEnded = true
            endTime = System.currentTimeMillis()
        }
    }

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
                        // Add any other timer properties you want to save
                    }
                    jsonTimers.put(jsonTimer)
                }
                put("timers", jsonTimers)
            }

            // Convert JSON object to a string
            val jsonString = jsonSession.toString()
            println(jsonSession.toString())

            // Define the file name and create a FileOutputStream
            val fileName = "completed_session_${sessionId}.json"
            val fileOutputStream: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)

            // Use OutputStreamWriter to write the JSON string to the file
            OutputStreamWriter(fileOutputStream).use { writer ->
                writer.write(jsonString)
                println("Written to data/data/com.example.pomodorotimer/files")
            }

            // Close the file output stream
            fileOutputStream.close()

        } catch (e: Exception) {
            // Handle JSON serialization or file writing exception
            e.printStackTrace()
        }
    }

    fun getFormattedTime(timeInMillis: Long): String {
        val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        return sdf.format(calendar.time)
    }

    override fun toString(): String {
        return "Session(id=$sessionId, name='$sessionName', hasEnded=$hasEnded, " +
                "startTime=${getFormattedTime(startTime)}, endTime=${getFormattedTime(endTime)}) Timers: ${Debug.printTimerList(timers)}"
    }

}

