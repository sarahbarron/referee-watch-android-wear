package org.wit.myapplication.service

import android.app.*
import org.wit.myapplication.R
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.fragment_stopwatch.view.*
import org.wit.myapplication.R.string.stopwatch_current_time
import org.wit.myapplication.activities.MainActivity
import org.wit.myapplication.main.MainApp
import java.util.*


class StopwatchService : Service(){

    private val CHANNEL_ID = "NotificationChannelID"
    var stopAfterHours = 2
    var clubGame = 30 * 60
    var countyGame = 35 *60

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int{

        val currentTime = arrayOf<Int>(intent!!.getIntExtra("TimeValue", 0))
        var running = intent!!.getBooleanExtra("Running", false)
        val timer = Timer()
        if(running) {
            timer.scheduleAtFixedRate(object : TimerTask() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun run() {
                    val intent1local = Intent()
                    intent1local.setAction("Counter")
                    currentTime[0]++
                    intent1local.putExtra("Time", currentTime[0])

                    sendBroadcast(intent1local)

                    // if the time is up send a notification to the watch
                    if (currentTime[0] == clubGame) NotificationUpdate(currentTime[0])
                    // if the timer is still running after 2 hours cancel the timer running in the background
                    if (currentTime[0] == (3600 * stopAfterHours)) timer.cancel()
                }
            }, 0, 1000)
        }
        return super.onStartCommand(intent, flags, startId)

    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    // Notification when time is up on a matchs half
    @RequiresApi(Build.VERSION_CODES.O)
    fun NotificationUpdate(time:Int) {
        try
        {
            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
            val notification = arrayOf<Notification>(NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Stop Watch")
                    .setContentText("Time Up: " + time.toString())
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)// this wakes up the wear and makes it vibrate
                    .build())
            startForeground(1, notification[0])
            NotificationManagerCompat.from(this).apply {
                notify(1, notification[0])
            }

        }
        catch (e:Exception) {
            e.printStackTrace()
        }
    }


}