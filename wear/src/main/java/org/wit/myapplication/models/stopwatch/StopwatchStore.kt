package org.wit.myapplication.models.stopwatch

interface StopwatchStore {

    fun getTime():Int?
    fun setTime(int: Int?)
    fun getStopwatchRunning(): Boolean?
    fun setStopwatchRunning(running: Boolean?)
}