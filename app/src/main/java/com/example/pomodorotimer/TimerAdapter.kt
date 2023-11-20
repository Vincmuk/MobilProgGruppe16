package com.example.pomodorotimer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import timerx.Timer

class TimerAdapter(
    private val timerList: MutableList<Timer>,
    private val timerTypeMap: Map<Timer, TimerType> // Pass timerTypeMap as a parameter
) : RecyclerView.Adapter<TimerItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.timer_item_layout, parent, false)

        return TimerItemViewHolder(itemView)
    }

    fun addTimer(timer: Timer) {
        timerList.add(timer)
        notifyItemInserted(timerList.size - 1)
    }

    fun removeTimer(position: Int) {
        timerList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clearTimers() {
        timerList.clear()
        notifyDataSetChanged()
    }


    fun updateTimer(position: Int, updatedTimer: Timer) {
        timerList[position] = updatedTimer
        notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder: TimerItemViewHolder, position: Int) {
        val timer = timerList[position]
        val timerType = timerTypeMap[timer]

        if (timerType != null) {
            val bgColour =
                if (timerType == TimerType.WORK) R.color.workColor else R.color.breakColor
            (holder.itemView as MaterialCardView).setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, bgColour)
            )
        }
    }



    override fun getItemCount(): Int {
        return timerList.size
    }
}
