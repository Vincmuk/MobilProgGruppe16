package com.example.pomodorotimer
import android.os.Bundle
import android.os.CountDownTimer
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

class TimerFragment : Fragment() {
    private lateinit var text_time: TextView
    private lateinit var btn_start: TextView
    private lateinit var btn_stop: TextView
    private lateinit var btn_pause: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)

        text_time = view.findViewById(R.id.text_time)
        btn_start = view.findViewById(R.id.btn_start)
        btn_stop = view.findViewById(R.id.btn_stop)
        btn_pause = view.findViewById(R.id.btn_pause)

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

        return view
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}
