package com.edvardas.basictasksapp

import android.util.Log
import java.io.Serializable
import java.util.*

class Timing(var task: Task) : Serializable {
    var id: Long = 0
    var startTime: Long
    var duration: Long = 0
        private set

    companion object {
        private const val TAG = "Timing"
        private const val serialVersionUID = 7325210872163737169L
    }

    init {
        val currentTime = Date()
        startTime = currentTime.time / 1000
    }

    fun setDuration() {
        val currentTime = Date()
        duration = (currentTime.time / 1000) - startTime
        Log.d(TAG, "setDuration: ")
    }
}