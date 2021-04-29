package org.wit.myapplication.models.stopwatch

import android.content.Context

class Stopwatch(val context: Context) : StopwatchStore{
    var seconds: Int = 0
    var stopwatchRunning: Boolean = false

    override fun getTime(): Int {
       return seconds
    }

    override fun setTime(int: Int?) {
        seconds = int!!
    }


    override fun getStopwatchRunning(): Boolean {
        return stopwatchRunning
    }

    override fun setStopwatchRunning(running: Boolean?){
        stopwatchRunning = running!!
    }


}