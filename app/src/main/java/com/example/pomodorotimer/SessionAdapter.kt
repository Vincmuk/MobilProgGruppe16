package com.example.pomodorotimer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class SessionAdapter : ListAdapter<Session, SessionAdapter.SessionViewHolder>(SessionDiffCallback()) {

    class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sessionNameTextView: TextView = itemView.findViewById(R.id.sessionNameTextView) // Replace with your TextView ID
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.session_item_layout, parent, false) // Replace with your item layout
        return SessionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val currentSession = getItem(position)
        holder.sessionNameTextView.text = currentSession.sessionName
        // Add any other bindings based on your Session model
    }
}

class SessionDiffCallback : DiffUtil.ItemCallback<Session>() {
    override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem.sessionId == newItem.sessionId
    }

    override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem == newItem
    }
}
