package org.wit.myapplication.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_stopwatch.*
import kotlinx.android.synthetic.main.fragment_stopwatch.view.*
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.stopwatch.LiveDataViewModel

import androidx.fragment.app.activityViewModels


class StopwatchFragment : Fragment() {

    //stopwatch

    lateinit var app: MainApp
    lateinit var root: View
    private val model: LiveDataViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        app = activity?.application as MainApp
        root =inflater.inflate(R.layout.fragment_stopwatch, container, false)
        model.time.observe(viewLifecycleOwner, androidx.lifecycle.Observer{ item ->
            timer.text = item
        })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        runTimer()
    }

    fun arguments(putString: Unit) {

    }


    companion object {
        private const val TAG = "StopwatchFragment"
    }



    fun setStopwatchStartListener(view: View) {
        view.btnStart.setOnClickListener{
//            app.stopwatchstore.setStopwatchRunning(true)
//            stopwatchRunning = true
        }
    }
//    fun setStopwatchPauseListener(view: View) {
//        view.btnPause.setOnClickListener{
//            app.stopwatchstore.setStopwatchRunning(false)
//            stopwatchRunning = false
//        }
//    }
//
    fun runTimer(){
        val seconds = app.stopwatchstore.getTime()
        val hours = (seconds!! / 3600)
        val minutes = (seconds!! % 3600) / 60
        val secs = seconds!! % 60

        val time = String.format("%d:%02d:%02d", hours, minutes, secs)
        timer.text = time

    }

//    companion object{
//        fun setAlarm(context: Context, currentSeconds: Int, gameTime: Long = 35): Long{
//            val alarmTime: Long = ((gameTime*60)*1000)
//            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            val intent = Intent(context, TimerExpiredReceiver::class.java)
//            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0 )
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent )
//            PrefUtil.setAlarmSetTime(nowSeconds, context)
//            return alarmTime
//        }
//
//          fun removeAlarm(context: Context){
//              val intent = Intent(context, TimerExpiredReceiver:class.java)
//              val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0 )
//              val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//              alarmManager.cancel(pendingIntent)
//              PrefUtil.setAlarmSetTime(0, context)
//          }
//        val nowSeconds: long
//        get() = Calendar.getInstance().timeInMillis/1000
//    }
//
//    private val ALARM_SET_TIME_ID = "com.resocoder.timer.background_time"
//    fun getAlarmSetTime(context: Context): Long {
//        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
//        return preferences.getLong(ALARM_SET_TIME_ID,0)
//
//    }
//
//    fun setAlarmSetTime(time: Long, context: Context){
//        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
//        editor.putLong(ALARM_SET_TIME_ID,time)
//        editor.apply()
//    }
}
