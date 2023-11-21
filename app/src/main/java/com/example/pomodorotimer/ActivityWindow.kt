package com.example.pomodorotimer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.TextView


class ActivityWindow() : Activity() {
    private lateinit var btn_addPoms: TextView
    private lateinit var text_pomodoroNumber: TextView
    private lateinit var text_sessionName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_window)

        btn_addPoms = findViewById(R.id.addPoms)
        text_pomodoroNumber = findViewById(R.id.pomodoroNumber)
        text_sessionName = findViewById(R.id.sessionName)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width*0.8).toInt(), (height*0.8).toInt())

        btn_addPoms.setOnClickListener {
            val pomsToAddText = text_pomodoroNumber.text.toString()
            val sessionName = text_sessionName.text.toString()
            if (pomsToAddText.isNotEmpty()) {
                val pomsToAdd = pomsToAddText.toInt()
                val resultIntent = Intent()
                resultIntent.putExtra("updatedPomsToAddValue", pomsToAdd)
                resultIntent.putExtra("sessionName", sessionName)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                println("Invalid pomsToAdd value: $pomsToAddText")
                setResult(RESULT_CANCELED)
                finish()
            }
        }

    }


}
