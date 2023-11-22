package com.example.pomodorotimer

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val sessionNameTextView: TextView = itemView.findViewById(R.id.sessionNameTextView)
    val startTimeTextView: TextView = itemView.findViewById(R.id.startTimeTextView)
    val endTimeTextView: TextView = itemView.findViewById(R.id.endTimeTextView)
    val timersSizeTextView: TextView = itemView.findViewById(R.id.timersSizeTextView)
}
