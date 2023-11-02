package com.example.pomodorotimer
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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
    private lateinit var btn_addtask: TextView
    private lateinit var test_text: TextView
    private var state = false

    private var pomsToAddValue: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)

        text_time = view.findViewById(R.id.text_time)
        btn_start = view.findViewById(R.id.btn_start)
        btn_stop = view.findViewById(R.id.btn_stop)
        btn_addtask = view.findViewById(R.id.btn_addtask)
        test_text = view.findViewById(R.id.testText)

       var cTimer: CountDownTimer? = null
        lateinit var timer: Timer


         timer = buildTimer {
            startFormat("MM:SS")
            startTime(25, TimeUnit.MINUTES)
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

        btn_start.setOnClickListener {
            if (!state) {
                timer.start()
                flip()
                btn_start.text = "Pause"
            } else {
                btn_start.text = "Resume"
                timer.stop()
                flip()
            }

        }
        btn_stop.setOnClickListener {
            timer.stop()
            timer.setTime(25, TimeUnit.MINUTES)
            text_time.text = timer.remainingFormattedTime
        }

        btn_addtask.setOnClickListener {
            val intent = Intent(context, ActivityWindow::class.java)
            intent.putExtra("pomsToAddValue", pomsToAddValue)
            startActivityForResult(intent, 1)

        }

        return view
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun flip(): Boolean {
        state = !state
        return state
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val updatedPomsToAddValue = data?.getIntExtra("updatedPomsToAddValue", 0)
            // Use the updatedPomsToAddValue as needed
            test_text.text = updatedPomsToAddValue.toString()
        }
    }

}