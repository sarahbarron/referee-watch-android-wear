package org.wit.myapplication.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_stopwatch.*
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.LiveDataViewModel


class StopwatchFragment : Fragment() {

    //stopwatch

    lateinit var app: MainApp
    private lateinit var root: View
    private val model: LiveDataViewModel by activityViewModels()
   // private var mWearableNavigationDrawer: WearableNavigationDrawerView? =null


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        app = activity?.application as MainApp
        // Inflate the layout for this fragment
        container?.removeAllViews();
        root =inflater.inflate(R.layout.fragment_stopwatch, container, false)
       // model.mSelectedTopNav.value=0
        //mWearableNavigationDrawer?.setCurrentItem(0,true)


        model.teamA.observe(viewLifecycleOwner, { item-> teamAName.text = item.name})
        Log.i(TAG, "Team A : ${model.teamA.value?.name}")
        model.teamB.observe(viewLifecycleOwner, { item-> teamBName.text = item.name})
        Log.i(TAG, "Team B : ${model.teamB.value?.name}")
        model.time.observe(viewLifecycleOwner, { item -> timer.text = item })
        model.teamAtotalGoals.observe(viewLifecycleOwner, { item-> teamAGoals.text = item.toString()})
        model.teamBtotalGoals.observe(viewLifecycleOwner, { item-> teamBGoals.text = item.toString()})
        model.teamAtotalPoints.observe(viewLifecycleOwner, { item-> teamAPoints.text = item.toString()})
        model.teamBtotalPoints.observe(viewLifecycleOwner, { item-> teamBPoints.text = item.toString()})
        model.teamAtotal.observe(viewLifecycleOwner, { item-> teamAtotal.text = item.toString()})
        model.teamBtotal.observe(viewLifecycleOwner, { item-> teamBtotal.text = item.toString()})

        return root
    }


    override fun onResume() {
        super.onResume()
        val mWearableNavigationDrawer = root.top_navigation_drawer
        mWearableNavigationDrawer?.setCurrentItem(0,true)
        model.mSelectedTopNav.value=0
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