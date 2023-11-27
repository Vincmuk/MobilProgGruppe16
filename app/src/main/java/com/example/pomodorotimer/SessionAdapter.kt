package com.example.pomodorotimer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class SessionAdapter : ListAdapter<Session, SessionViewHolder>(SessionDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.session_item_layout, parent, false)
        return SessionViewHolder(itemView)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = getItem(position)
        holder.sessionNameTextView.text = session.sessionName
        holder.startTimeTextView.text = "Start Time: ${session.getFormattedTime(session.startTime)}"
        holder.endTimeTextView.text = "End Time: ${session.getFormattedTime(session.endTime)}"
        holder.timersSizeTextView.text = "Timers Count: ${session.timers.size}"
    }

    private class SessionDiffCallback : DiffUtil.ItemCallback<Session>() {
        override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
            return oldItem.sessionId == newItem.sessionId
        }
        override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
            return oldItem == newItem
        }
    }
}
