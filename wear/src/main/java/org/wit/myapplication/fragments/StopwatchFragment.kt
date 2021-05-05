package org.wit.myapplication.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_score.*
import kotlinx.android.synthetic.main.fragment_stopwatch.*
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.stopwatch.LiveDataViewModel


class StopwatchFragment : Fragment() {

    //stopwatch

    lateinit var app: MainApp
    lateinit var root: View
//    private val model: LiveDataViewModel by activityViewModels()

     private val model: LiveDataViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        app = activity?.application as MainApp
        root =inflater.inflate(R.layout.fragment_stopwatch, container, false)

        model.mSelectedTopNav.value=0


        val timer = Observer<String>{ item -> timer.text = item}
        val teamAGoals = Observer<Int> { item -> teamAGoals.text = item.toString() }
        val teamBGoals = Observer<Int> { item -> teamBGoals.text = item.toString() }
        val teamAPoints = Observer<Int> { item -> teamAPoints.text = item.toString() }
        val teamBPoints = Observer<Int> { item -> teamBPoints.text = item.toString() }
        val teamATotal = Observer<Int> { item -> teamAtotal.text = item.toString() }
        val teamBTotal = Observer<Int> { item -> teamBtotal.text = item.toString() }

        model.time.observe(viewLifecycleOwner, timer)
        model.teamAtotalGoals.observe(viewLifecycleOwner, teamAGoals)
        model.teamBtotalGoals.observe(viewLifecycleOwner, teamBGoals)
        model.teamAtotalPoints.observe(viewLifecycleOwner, teamAPoints)
        model.teamBtotalPoints.observe(viewLifecycleOwner, teamBPoints)
        model.teamAtotal.observe(viewLifecycleOwner, teamATotal)
        model.teamBtotal.observe(viewLifecycleOwner, teamBTotal)



//        model.time.observe(viewLifecycleOwner, { item -> timer.text = item })
//
//        model.teamAtotalGoals.observe(viewLifecycleOwner, { item-> teamAGoals.text = item.toString()})
//        model.teamBtotalGoals.observe(viewLifecycleOwner, { item-> teamBGoals.text = item.toString()})
//        model.teamAtotalPoints.observe(viewLifecycleOwner, { item-> teamAPoints.text = item.toString()})
//        model.teamBtotalPoints.observe(viewLifecycleOwner, { item-> teamBPoints.text = item.toString()})
//        model.teamAtotal.observe(viewLifecycleOwner, { item-> teamAtotal.text = item.toString()})
//        model.teamBtotal.observe(viewLifecycleOwner, { item-> teamBtotal.text = item.toString()})

        return root
    }


    override fun onResume() {
        super.onResume()
        var mWearableNavigationDrawer = root.top_navigation_drawer
        mWearableNavigationDrawer?.setCurrentItem(0,true)
        model.mSelectedTopNav.value=0
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    companion object {
        private const val TAG = "StopwatchFragment"
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