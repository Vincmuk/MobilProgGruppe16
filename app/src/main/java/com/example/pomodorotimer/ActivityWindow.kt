package com.example.pomodorotimer

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.TextView


class ActivityWindow() : Activity() {
    private lateinit var btn_addPoms: TextView
    private lateinit var text_pomodoroNumber: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_window)

        btn_addPoms = findViewById(R.id.addPoms)
        text_pomodoroNumber = findViewById(R.id.pomodoroNumber)

        var pomsToAddValue = intent.getIntExtra("pomsToAddValue", 0)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width*0.8).toInt(), (height*0.8).toInt())

        btn_addPoms.setOnClickListener {
            val pomsToAdd = text_pomodoroNumber.text.toString()
            pomsToAddValue = pomsToAdd.toInt()
            println(pomsToAdd)
            finish()
        }
    }


}
