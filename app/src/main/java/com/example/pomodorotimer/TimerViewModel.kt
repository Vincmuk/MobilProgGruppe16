package com.example.pomodorotimer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timerx.Timer

class TimerViewModel : ViewModel() {
    private val _timerList = MutableLiveData<List<Timer>>()
    val timerList: LiveData<List<Timer>> get() = _timerList

    fun setTimers(newTimers: List<Timer>, onComplete: () -> Unit) {
        if (newTimers.isNotEmpty()) {
            _timerList.value = newTimers
            onComplete()
        }
    }
    fun clearTimers(onComplete: () -> Unit) {
        if (_timerList.value?.isNotEmpty() == true) {
            _timerList.value = emptyList()
            onComplete()
        }
    }
}