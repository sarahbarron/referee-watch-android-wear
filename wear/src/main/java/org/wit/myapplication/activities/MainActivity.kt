package org.wit.myapplication.activities

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.drawer.WearableActionDrawerView
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import org.jetbrains.anko.intentFor
import org.wit.myapplication.R
import org.wit.myapplication.TopNav
import org.wit.myapplication.main.MainApp
import java.util.*
import org.jetbrains.anko.startActivity
import org.wit.myapplication.fragments.*
import org.wit.myapplication.models.*


/**
 * GAA Referee Main Activity control of navigation
 */
class MainActivity :AppCompatActivity(),
    AmbientModeSupport.AmbientCallbackProvider, MenuItem.OnMenuItemClickListener,
    WearableNavigationDrawerView.OnItemSelectedListener {
    private var mWearableNavigationDrawer: WearableNavigationDrawerView? = null
    private var mWearableActionDrawer: WearableActionDrawerView? = null
    private var mTopNav: ArrayList<TopNav>? = null
    private var mSelectedTopNav = 0
    private var subFragment: SubFragment? = null
    private var cardsFragment: CardFragment? = null
    private var scoreFragment: ScoreFragment? = null
    private var watchFragment: StopwatchFragment? = null
    private var listScoresFragment: ListScoresFragment? =null


    lateinit var app: MainApp
    var edit: Boolean = false
    var game = GameModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        setContentView(R.layout.activity_main)
        app = application as MainApp
        game = app.firebasestore.game

        Log.i(TAG, "Game Id: $game.id")
        // fetch team A and players
        game.teamA?.let { app.firebasestore.fetchTeam(it.id, "teamA") }
        game.id?.let { game.teamA?.let { it1 -> app.firebasestore.fetchTeamsheetPlayers(it, it1.id, "teamA") } }
        Log.i(TAG, "Team A: ${app.firebasestore.teamA} : ${app.firebasestore.teamAPlayers}")
        // fetch team B
        game.teamB?.let { app.firebasestore.fetchTeam(it.id, "teamB") }
        game.id?.let { game.teamB?.let { it1 -> app.firebasestore.fetchTeamsheetPlayers(it, it1.id, "teamB") } }
        Log.i(TAG, "Team B: ${app.firebasestore.teamB}: ${app.firebasestore.teamBPlayers}")

        // fetch scores
        game.id?.let { app.firebasestore.fetchScores(it) }
        // Enables Ambient mode.
        AmbientModeSupport.attach(this)
        mTopNav = initializeTopNav()
        mSelectedTopNav = 0

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
        mWearableActionDrawer?.controller?.peekDrawer()
        mWearableActionDrawer?.setOnMenuItemClickListener(this)

    }

    // Initialize the top navigation
    private fun initializeTopNav(): ArrayList<TopNav> {
        val topNavSystem: ArrayList<TopNav> = ArrayList<TopNav>()
        val topNavArrayNames = resources.getStringArray(R.array.topnav_array_names)
        for (item in topNavArrayNames) {
            Log.d(TAG, "ITEM :$item")
            var itemResourceId = resources.getIdentifier(item, "array", packageName)
            Log.d(TAG, "ItemResourceID :$itemResourceId")
            var itemInformation = resources.getStringArray(itemResourceId)
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

            R.id.menu_home -> startActivity(intentFor<GamesList>())
            R.id.menu_end_half -> {}
            R.id.bottom_menu_scores -> {
                //startActivity(intentFor<ScoresList>())
                listScoresFragment = ListScoresFragment()
                val args = Bundle()
                listScoresFragment!!.arguments = args
                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.content_frame, listScoresFragment!!).commit()
            }
            R.id.bottom_menu_cards -> toastMessage = mTopNav!![mSelectedTopNav].name
            R.id.bottom_menu_subs -> toastMessage = mTopNav!![mSelectedTopNav].name
            R.id.bottom_menu_injuries -> toastMessage = mTopNav!![mSelectedTopNav].name
            R.id.bottom_menu_reset_stopwatch -> toastMessage = mTopNav!![mSelectedTopNav].name
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
        mSelectedTopNav = position
        val selectedItemName: String = mTopNav!![mSelectedTopNav].name
        Log.d(TAG, "SelectedItem: $selectedItemName")
        when (selectedItemName) {
            "Home" -> {
                watchFragment = StopwatchFragment()
                val args = Bundle()
                watchFragment!!.arguments = args
                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.content_frame, watchFragment!!).commit()
            }

            "Score" -> {
                scoreFragment = ScoreFragment()
                val args = Bundle()
                scoreFragment!!.arguments = args
                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.content_frame, scoreFragment!!).commit()
            }
            "Card" -> {
                cardsFragment = CardFragment()
                val args = Bundle()
                cardsFragment!!.arguments = args
                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.content_frame, cardsFragment!!).commit()
            }
            "Sub" -> {
                subFragment = SubFragment()
                val args = Bundle()
                subFragment!!.arguments = args
                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.content_frame, subFragment!!).commit()
            }

            else -> HomeFragment()
        }

    }

    private inner class NavigationAdapter  /* package */ internal constructor(private val mContext: Context) :
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


    // Cards Recycler View
    private fun fetchCards(gameId: String)
    {
        app.firebasestore.fetchCards(gameId)
    }
    private fun fetchSubstitutes(gameId: String)
    {
        app.firebasestore.fetchCards(gameId)
    }
    private fun fetchScores(gameId: String)
    {
        app.firebasestore.fetchSubstitutes(gameId)
    }
    private fun fetchInjuries(gameId: String)
    {
        app.firebasestore.fetchInjuries(gameId)
    }



}