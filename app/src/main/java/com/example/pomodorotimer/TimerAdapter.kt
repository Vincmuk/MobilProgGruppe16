package com.example.pomodorotimer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.android.material.card.MaterialCardView
import timerx.Timer

class TimerAdapter(private val timerTypeMap: Map<Timer, TimerType>) :
    ListAdapter<Timer, TimerItemViewHolder>(TimerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.timer_item_layout, parent, false)

        return TimerItemViewHolder(itemView)
    }

    fun removeFirstTimer(callback: () -> Unit) {
        val updatedList = currentList.toMutableList()
        updatedList.removeFirst()
        submitList(updatedList) {
            callback.invoke()
        }
    }

    override fun onBindViewHolder(holder: TimerItemViewHolder, position: Int) {
        val timer = getItem(position)
        val timerType = timerTypeMap[timer]

        if (timerType != null) {
            val bgColour =
                if (timerType == TimerType.WORK) R.color.workColor else R.color.breakColor
            (holder.itemView as MaterialCardView).setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, bgColour)
            )
        }
    }

    private class TimerDiffCallback : DiffUtil.ItemCallback<Timer>() {
        override fun areItemsTheSame(oldItem: Timer, newItem: Timer): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Timer, newItem: Timer): Boolean {
            return oldItem == newItem
        }
    }

}
