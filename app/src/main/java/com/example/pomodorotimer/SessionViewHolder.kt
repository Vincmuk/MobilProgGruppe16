package com.example.pomodorotimer

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val sessionNameTextView: TextView = itemView.findViewById(R.id.sessionNameTextView) // Replace with your TextView ID
    // Add references to other views in your session item layout if needed
}
