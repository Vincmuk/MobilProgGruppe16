package com.example.pomodorotimer

import Debug
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONObject
import timerx.Timer
import timerx.buildTimer
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.concurrent.TimeUnit

class SessionViewModel : ViewModel() {
    private val _sessionList = MutableLiveData<List<Session>>()
    val sessionList: LiveData<List<Session>> get() = _sessionList

    fun loadExistingSessions(context: Context) {
        try {
            val sessionList = mutableListOf<Session>()
            val fileList = context.fileList()

            fileList.forEach { fileName ->
                if (fileName.startsWith("completed_session_") && fileName.endsWith(".json")) {
                    val sessionId = fileName.removeSuffix(".json").substringAfterLast("_").toLong()
                    val jsonString = context.openFileInput(fileName).bufferedReader().use(BufferedReader::readText)
                    val jsonSession = JSONObject(jsonString)

                    val sessionName = jsonSession.getString("sessionName")
                    val hasEnded = jsonSession.getBoolean("hasEnded")

                    val jsonTimers = jsonSession.getJSONArray("timers")
                    val timers = mutableListOf<Timer>()

                    for (i in 0 until jsonTimers.length()) {
                        val jsonTimer = jsonTimers.getJSONObject(i)
                        // Extract timer properties and create Timer objects
                        val formattedStartTime = jsonTimer.getString("formattedStartTime")
                        // Add any other timer properties you saved

                        val timer = buildTimer {
                            startFormat("MM:SS")
                            startTime(convertFormattedTimeToSeconds(formattedStartTime), TimeUnit.SECONDS)
                        }
                        timers.add(timer)
                    }

                    val session = Session(sessionId, sessionName, timers, hasEnded)
                    sessionList.add(session)
                    println(session)
                }
            }
            Session.nextId = sessionList.maxByOrNull { it.sessionId }?.sessionId?.plus(1) ?: 1

            // Update the _sessionList LiveData with the loaded sessions
            _sessionList.value = sessionList
        } catch (e: Exception) {
            // Handle JSON deserialization or file reading exception
            e.printStackTrace()
        }
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
    var hasEnded: Boolean = false
) {
    companion object {
        var nextId: Long = 1
        fun create(sessionName: String, timers: List<Timer>): Session {
            val newSession = Session(nextId, sessionName, timers.toMutableList())
            nextId++
            return newSession
        }
    }

    fun updateSession() {
        // Check if the last timer in the list has completed
        if (timers.lastOrNull()?.remainingTimeInMillis == 0L) {
            hasEnded = true
        }
    }

    fun saveCompletedSessionToJSON(context: Context) {
        try {
            val jsonSession = JSONObject().apply {
                put("sessionId", sessionId)
                put("sessionName", sessionName)
                put("hasEnded", hasEnded)

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

    override fun toString(): String {
        return "Session(id=$sessionId, name='$sessionName', hasEnded=$hasEnded) Timers: ${Debug.printTimerList(timers)}"
    }

}

