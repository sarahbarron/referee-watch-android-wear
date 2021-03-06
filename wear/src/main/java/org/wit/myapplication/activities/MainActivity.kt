package org.wit.myapplication.activities

import android.Manifest.permission.FOREGROUND_SERVICE
import android.content.*
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.drawer.WearableActionDrawerView
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import org.wit.myapplication.R
import org.wit.myapplication.TopNav
import org.wit.myapplication.fragments.*
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.GameModel
import org.wit.myapplication.models.LiveDataViewModel
import org.wit.myapplication.service.StopwatchService


/**
 * GAA Referee Main Activity control of navigation
 */
class MainActivity :AppCompatActivity(),
    AmbientModeSupport.AmbientCallbackProvider, MenuItem.OnMenuItemClickListener,
    WearableNavigationDrawerView.OnItemSelectedListener {

    private val model: LiveDataViewModel by viewModels()
    private var mWearableNavigationDrawer: WearableNavigationDrawerView? = null
    private var mWearableActionDrawer: WearableActionDrawerView? = null
    private var mTopNav: ArrayList<TopNav>? = null
    private var subFragment: SubFragment? = null
    private var cardsFragment: CardFragment? = null
    private var scoreFragment: ScoreFragment? = null
    private var watchFragment: StopwatchFragment? = null
    private var injuryFragment: InjuryFragment?=  null
    private var listScoresFragment: ListScoresFragment? =null
    private var listCardsFragment: ListCardsFragment? =null
    private var listSubstitutesFragment: ListSubstitutesFragment? = null
    private var listInjuriesFragment: ListInjuriesFragment? = null
    private var additionalCommentsFragment: AdditionalCommentsFragment? = null
    private var listTeamsheetAFragment: ListTeamsheetAFragment?= null
    private var listTeamsheetBFragment: ListTeamsheetBFragment?= null
    lateinit var app: MainApp
    var edit: Boolean = false
    var game = GameModel()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // permission needed for notifications
        ActivityCompat.requestPermissions(this, arrayOf<String>(FOREGROUND_SERVICE), PackageManager.PERMISSION_GRANTED)


        // Broadcast Reciever
        val intentFilter = IntentFilter()
        intentFilter.addAction("Counter")
        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val seconds = intent.getIntExtra("Time", 0)
                model.seconds.value = seconds
                val hours = (seconds / 3600)
                val minutes = (seconds % 3600) / 60
                val secs = seconds % 60

                val time = String.format("%02d:%02d:%02d", hours, minutes, secs)
                // update the live data with the new time
                model.time.value = time
            }
        }
        registerReceiver(broadcastReceiver, intentFilter)

        model.teamAtotalPoints.value = 0
        model.teamAtotalGoals.value =0
        model.teamBtotalGoals.value=0
        model.teamBtotalPoints.value=0
        model.teamAtotal.value=0
        model.teamBtotal.value=0


        Log.d(TAG, "onCreate()")
        setContentView(R.layout.activity_main)
        app = application as MainApp
        game = app.firebasestore.game


        Log.i(TAG, "Game Id: $game.id")
        fetchDataFromFirebase(game)

        // Enables Ambient mode.
        AmbientModeSupport.attach(this)
        mTopNav = initializeTopNav()

        model.mSelectedTopNav.value = 0

        // Initialize content to home fragment.
        watchFragment = StopwatchFragment()
        val args = Bundle()

        watchFragment!!.arguments = args
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.content_frame, watchFragment!!).commit()

        // Top Navigation Drawer
        mWearableNavigationDrawer = findViewById(R.id.top_navigation_drawer)
        mWearableNavigationDrawer?.setAdapter(NavigationAdapter(this))

        // Peeks navigation drawer on the top.
        mWearableNavigationDrawer?.controller?.peekDrawer()
        mWearableNavigationDrawer?.addOnItemSelectedListener(this)

        // Bottom Action Drawer
        mWearableActionDrawer = findViewById(R.id.bottom_action_drawer)

        // Peeks action drawer on the bottom.
        mWearableActionDrawer?.controller?.closeDrawer()
        mWearableActionDrawer?.setOnMenuItemClickListener(this)

    }


    // Initialize the top navigation
    private fun initializeTopNav(): ArrayList<TopNav> {
        val topNavSystem: ArrayList<TopNav> = ArrayList<TopNav>()
        val topNavArrayNames = resources.getStringArray(R.array.topnav_array_names)
        for (item in topNavArrayNames) {
            Log.d(TAG, "ITEM :$item")
            val itemResourceId = resources.getIdentifier(item, "array", packageName)
            Log.d(TAG, "ItemResourceID :$itemResourceId")
            val itemInformation = resources.getStringArray(itemResourceId)
            Log.d(TAG, "ItemInformation :$itemInformation")
            topNavSystem.add(
                    TopNav(
                            itemInformation[0],  // Name
                            itemInformation[1],  // Navigation icon
                    )
            ) // Surface area
        }
        return topNavSystem
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        Log.d(TAG, "onMenuItemClick(): $menuItem")
        val itemId = menuItem.itemId
        var toastMessage = ""

        when (itemId) {

            R.id.menu_home -> {
                mWearableNavigationDrawer?.setCurrentItem(0, true)
                model.mSelectedTopNav.value = 0
                watchFragment = StopwatchFragment()
                val args = Bundle()
                watchFragment!!.arguments = args
                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.content_frame, watchFragment!!)
                        .commit()

            }
//            Add additional comments for the match report
            R.id.menu_additional_comments ->{
                mWearableNavigationDrawer?.setCurrentItem(0, true)
                model.mSelectedTopNav.value = 0
                additionalCommentsFragment = AdditionalCommentsFragment()
                val args = Bundle()
                additionalCommentsFragment!!.arguments = args
                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.content_frame, additionalCommentsFragment!!)
                    .commit()
            }
//          Start the game
            R.id.menu_start_game ->{
                mWearableNavigationDrawer?.setCurrentItem(0, true)
                model.mSelectedTopNav.value = 0

                val teamsheetA = app.firebasestore.teamAPlayers
                val teamsheetB = app.firebasestore.teamBPlayers
                if(teamsheetA.size>0 && teamsheetB.size>0) {
                    //set the time in firestore
                    app.firebasestore.setStartTimeOFGame()
                    // start the watch
                    if (mBound) {
                        // if stopwatch is aready running reset it
                        resetStopWatch()
                        // call method within the service to start the stopwatch
                        mService.updateServiceRunning(true)
                        app.firebasestore.gameStarted=true
                    } else startStopwatchListener()
                    toastMessage = "Game Started"
                }
                else{
                    toastMessage = "Cant start without teamsheets"
                }
                watchFragment = StopwatchFragment()
                val args = Bundle()
                watchFragment!!.arguments = args
                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.content_frame, watchFragment!!)
                    .commit()
            }

            R.id.menu_end_game -> {
                if(app.firebasestore.gameStarted) {
                    mWearableNavigationDrawer?.setCurrentItem(0, true)
                    model.mSelectedTopNav.value = 0
                    app.firebasestore.setEndTimeOFGame()
                    resetStopWatch()
                    toastMessage = "Game Ended"
                }
                else{
                    toastMessage = "Game hasn't started yet"
                }
            }

            R.id.menu_teamA_took_to_field_at -> {
                    mWearableNavigationDrawer?.setCurrentItem(0, true)
                    model.mSelectedTopNav.value = 0
                    val saved = app.firebasestore.setTimeTeamATookToField()
                    if(saved) {
                        toastMessage = "Time Recorded"
                    }
                    else{
                        toastMessage = "Problem Saving Time"
                    }
            }
            R.id.menu_teamB_took_to_field_at -> {
                mWearableNavigationDrawer?.setCurrentItem(0, true)
                model.mSelectedTopNav.value = 0
                val saved = app.firebasestore.setTimeTeamBTookToField()
                if(saved) {
                    toastMessage = "Time Recorded"
                }
                else{
                    toastMessage = "Problem Saving Time"
                }
            }

            R.id.menu_teamA_teamsheet ->{
                if(app.firebasestore.teamAPlayers.size>0) {
                    mWearableNavigationDrawer?.setCurrentItem(0, true)
                    model.mSelectedTopNav.value = 0
                    listTeamsheetAFragment = ListTeamsheetAFragment()
                    val args = Bundle()
                    listTeamsheetAFragment!!.arguments = args
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, listTeamsheetAFragment!!)
                        .commit()
                }
                else{
                    toastMessage = "No Teamsheet Available"
                }

            }

            R.id.menu_teamB_teamsheet->{
                if(app.firebasestore.teamBPlayers.size>0) {
                    mWearableNavigationDrawer?.setCurrentItem(0, true)
                    model.mSelectedTopNav.value = 0
                    listTeamsheetBFragment = ListTeamsheetBFragment()
                    val args = Bundle()
                    listTeamsheetBFragment!!.arguments = args
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, listTeamsheetBFragment!!)
                        .commit()
                }  else{
                    toastMessage = "No Teamsheet Available"
                }

            }

            R.id.bottom_menu_scores -> {
                if(app.firebasestore.scores.size>0) {
                    mWearableNavigationDrawer?.setCurrentItem(0, true)
                    model.mSelectedTopNav.value = 0
                    listScoresFragment = ListScoresFragment()
                    val args = Bundle()
                    listScoresFragment!!.arguments = args
                    val fragmentManager = supportFragmentManager
                    mWearableActionDrawer?.controller?.closeDrawer()
                    fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, listScoresFragment!!)
                        .commit()
                }
                else{
                    toastMessage = "No Scores To View"
                }

            }
            R.id.bottom_menu_cards -> {
                if(app.firebasestore.cards.size>0) {
                    mWearableNavigationDrawer?.setCurrentItem(0, true)
                    model.mSelectedTopNav.value = 0
                    listCardsFragment = ListCardsFragment()
                    val args = Bundle()
                    listCardsFragment!!.arguments = args
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, listCardsFragment!!)
                        .commit()
                }
                else{
                    toastMessage = "No Cards To View"
                }
            }
            R.id.bottom_menu_subs -> {
                if(app.firebasestore.substitutes.size>0) {
                    mWearableNavigationDrawer?.setCurrentItem(0, true)
                    model.mSelectedTopNav.value = 0
                    listSubstitutesFragment = ListSubstitutesFragment()
                    val args = Bundle()
                    listSubstitutesFragment!!.arguments = args
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction().replace(
                        R.id.content_frame,
                        listSubstitutesFragment!!
                    ).commit()
                }
                else{
                    toastMessage = "No Substitutes To View"
                }
            }
            R.id.bottom_menu_injuries -> {
                if(app.firebasestore.injuries.size>0) {
                    mWearableNavigationDrawer?.setCurrentItem(0, true)
                    model.mSelectedTopNav.value = 0
                    listInjuriesFragment = ListInjuriesFragment()
                    val args = Bundle()
                    listInjuriesFragment!!.arguments = args
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction().replace(
                        R.id.content_frame,
                        listInjuriesFragment!!
                    ).commit()
                }
                else{
                    toastMessage = "No Injuries To View"
                }

            }
            R.id.bottom_menu_reset_stopwatch -> {
                mWearableNavigationDrawer?.setCurrentItem(0, true)
                model.mSelectedTopNav.value = 0
                resetStopWatch()
            }
            R.id.bottom_menu_gameslist -> startActivity(intentFor<GamesList>())
            R.id.menu_sign_out -> signOut()

        }
        mWearableActionDrawer!!.controller.closeDrawer()

        return if (toastMessage.length > 0) {
            val toast = Toast.makeText(
                    applicationContext,
                    toastMessage,
                    Toast.LENGTH_SHORT
            )
            toast.show()
            true
        } else {
            false
        }
    }


    // Updates content when user changes between items in the top navigation drawer.
    override fun onItemSelected(position: Int) {
        Log.d(
                TAG,
                "WearableNavigationDrawerView triggered onItemSelected(): $position"
        )

        model.mSelectedTopNav.value = position
        val selectedItemName: String = mTopNav!![model.mSelectedTopNav.value!!].name
        Log.d(TAG, "SelectedItem: $selectedItemName")


        when (selectedItemName) {
            "Stopwatch" -> {
                mWearableNavigationDrawer?.setCurrentItem(0, true)
                model.mSelectedTopNav.value = 0
                watchFragment = StopwatchFragment()
                val args = Bundle()
                watchFragment!!.arguments = args
                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.content_frame, watchFragment!!)
                        .commit()
            }

            "Score" -> {

                    scoreFragment = ScoreFragment()
                    val args = Bundle()
                    scoreFragment!!.arguments = args
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.content_frame, scoreFragment!!)
                        .commit()



            }
            "Card" -> {

                    cardsFragment = CardFragment()
                    val args = Bundle()
                    cardsFragment!!.arguments = args
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.content_frame, cardsFragment!!)
                        .commit()

            }
            "Sub" -> {

                    subFragment = SubFragment()
                    val args = Bundle()
                    subFragment!!.arguments = args
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.content_frame, subFragment!!)
                        .commit()

            }
            "Injury" -> {
                injuryFragment = InjuryFragment()
                val args = Bundle()
                injuryFragment!!.arguments = args
                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.content_frame, injuryFragment!!)
                        .commit()
            }

            else -> HomeFragment()
        }
    }

    private inner class NavigationAdapter (private val mContext: Context) :
        WearableNavigationDrawerView.WearableNavigationDrawerAdapter() {
        override fun getCount(): Int {
            return mTopNav!!.size
        }

        override fun getItemText(pos: Int): String {
            return mTopNav!![pos].name
        }

        override fun getItemDrawable(pos: Int): Drawable {
            val navigationIcon: String = mTopNav!![pos].navigationIcon
            val drawableNavigationIconId = resources.getIdentifier(
                    navigationIcon, "drawable",
                    packageName
            )
            return mContext.getDrawable(drawableNavigationIconId)!!
        }

    }

    /**
     * Fragment that appears in the "content_frame"
     */
    class HomeFragment : Fragment() {

        override fun onCreateView(
                inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            val rootView: View = inflater.inflate(R.layout.fragment_home, container, false)
            return rootView
        }

//        /* Ambient */
//        fun onEnterAmbientInFragment(ambientDetails: Bundle) {
//            Log.d(
//                TAG,
//                "HomeFragment.onEnterAmbient() $ambientDetails"
//            )
//
//            // Convert to grayscale for ambient mode.
//            val matrix = ColorMatrix()
//            matrix.setSaturation(0f)
//
//        }
//
//        /**
//         * Restores the UI to active (non-ambient) mode.
//         */
//        /* package */
//        fun onExitAmbientInFragment() {
//            Log.d(TAG, "HomeFragment.onExitAmbient()")
//
//        }

    }


    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return MyAmbientCallback()
    }

    private inner class MyAmbientCallback : AmbientModeSupport.AmbientCallback() {
        /**
         * Prepares the UI for ambient mode.
         */
        override fun onEnterAmbient(ambientDetails: Bundle) {
            super.onEnterAmbient(ambientDetails)
            Log.d(TAG, "onEnterAmbient() $ambientDetails")
    //        mFragment!!.onEnterAmbientInFragment(ambientDetails)
            mWearableNavigationDrawer!!.controller.closeDrawer()
            mWearableActionDrawer!!.controller.closeDrawer()
        }

        /**
         * Restores the UI to active (non-ambient) mode.
         */
        override fun onExitAmbient() {
            super.onExitAmbient()
            Log.d(TAG, "onExitAmbient()")
      //      mFragment!!.onExitAmbientInFragment()
            mWearableActionDrawer!!.controller.peekDrawer()
        }
    }

    private fun signOut(){
        app.auth.signOut()
        startActivity<Auth>()
    }


    companion object {
        private const val TAG = "MainActivity"
    }


    fun fetchDataFromFirebase(game: GameModel) {
        doAsync {
            app.firebasestore.fetchTeam(game.id!!, game.teamA?.id!!, "teamA")
            app.firebasestore.fetchTeam(game.id!!, game.teamB?.id!!, "teamB")
//            app.firebasestore.fetchScores(game.id!!)
            app.firebasestore.fetchCards(game.id!!)
            app.firebasestore.fetchSubstitutes(game.id!!)
            app.firebasestore.fetchInjuries(game.id!!)
            Thread.sleep(1000)
            app.firebasestore.sport = (app.firebasestore.teamA.sportType?.id ?: "").trim()
            uiThread {
                model.teamA.value = app.firebasestore.teamA
                model.teamB.value = app.firebasestore.teamB
                // add the team name to the bottom menu teamsheet
                val menu: Menu? = mWearableActionDrawer?.menu
                menu?.findItem(R.id.menu_teamA_teamsheet)?.title = "${model.teamA.value?.name} team sheet"
                menu?.findItem(R.id.menu_teamB_teamsheet)?.title = "${model.teamB.value?.name} team sheet"
                menu?.findItem(R.id.menu_teamA_took_to_field_at)?.title = "${model.teamA.value?.name} took to the field"
                menu?.findItem(R.id.menu_teamB_took_to_field_at)?.title = "${model.teamB.value?.name} took to the field"

            }
        }

    }




    private lateinit var mService:StopwatchService
    private var mBound: Boolean = false

    //  binder connection to the service
    private val connection = object: ServiceConnection {
        override fun onServiceConnected(className:ComponentName, service:IBinder) {
           val binder = service as StopwatchService.LocalBinder
            mService = binder.getService()
            mBound = true
        }
        // when the service is unexpectedly disconnected
        override fun onServiceDisconnected(name:ComponentName) {
            mBound = false
        }
    }


    private fun startStopwatchListener(){
        val intentService = Intent(this, StopwatchService::class.java)
        val integerTimeSet = Integer.parseInt(app.stopwatchstore.getTime().toString())
        intentService.putExtra("TimeValue", integerTimeSet).putExtra("Running", true)
        intentService.also{intent->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)}
        startService(intentService)
    }

    override fun onStop(){
        super.onStop()
        if(mBound) {
            model.time.value ="00:00:00"
            unbindService(connection)
            mBound = false
        }
    }

    private fun resetStopWatch() {
        model.time.value="00:00:00"
        if(mBound){
            mService.updateServiceTime(0)
            mService.updateServiceRunning(false)
        }
    }

//    Start/Play the stopwatch
    fun onClickStart(view: View) {

        val teamsheetA = app.firebasestore.teamAPlayers
        val teamsheetB = app.firebasestore.teamBPlayers
        val gameStarted = app.firebasestore.gameStarted
//        If the game hasn't already started start the game
        if(teamsheetA.size>0 && teamsheetB.size>0 && !gameStarted) {
            //set the time in firestore
            app.firebasestore.setStartTimeOFGame();
            // start the watch
            if (mBound) {
                // if stopwatch is aready running reset it
                resetStopWatch();
                // call method within the service to start the stopwatch
                mService.updateServiceRunning(true)
                app.firebasestore.gameStarted=true;
            } else startStopwatchListener()

        }

//        If the game is already started keep running
        else if(teamsheetA.size>0 && teamsheetB.size>0 && gameStarted){
            if(mBound) {
                Log.i(TAG, "Button Start")
                // call method within the service
                mService.updateServiceRunning(true)
            }
            else startStopwatchListener()
        }
        else if(teamsheetA.size==0 || teamsheetB.size==0){
            val toast = Toast.makeText(
                applicationContext,
                "Need teamsheets to start game",
                Toast.LENGTH_SHORT
            )
            toast.show()

        }
        else startStopwatchListener()
    }



    fun onClickPause(view: View) {
        if(mBound) {
            Log.i(TAG, "Button Pause")
            // call method within the service
            mService.updateServiceRunning(false)
        }
    }

    //   fun onClickStart(view: View) {
//        val intentService = Intent(this, StopwatchService::class.java)
//        val integerTimeSet = Integer.parseInt(app.stopwatchstore.getTime().toString())
//        intentService.putExtra("TimeValue", integerTimeSet).putExtra("Running", true)
//        startService(intentService)
//        stopwatchService.doServiceStuff(true)
//    }
//    fun onClickPause(view: View) {
//        stopwatchService.doServiceStuff(false)
//    }


}


