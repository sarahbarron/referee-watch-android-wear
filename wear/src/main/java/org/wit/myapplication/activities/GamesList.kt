package org.wit.myapplication.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import com.google.firebase.firestore.FirebaseFirestore
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.game_recycler_layout.*
import org.jetbrains.anko.startActivity
import org.wit.myapplication.adapters.GamesAdapter
import org.wit.myapplication.adapters.GamesListener
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.uiThread
import org.wit.myapplication.models.GameModel


class GamesList: FragmentActivity(), GamesListener,
        AmbientModeSupport.AmbientCallbackProvider, MenuItem.OnMenuItemClickListener,
        WearableNavigationDrawerView.OnItemSelectedListener {

    lateinit var app: MainApp
    lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_recycler_layout)

        app = this.application as MainApp
        db = FirebaseFirestore.getInstance()

        val layoutManager = WearableLinearLayoutManager(this)
        game_recycler_layout.layoutManager = layoutManager
        game_recycler_layout.setHasFixedSize(true)
        game_recycler_layout.isEdgeItemsCenteringEnabled = false
        app.firebasestore.allPlayers.clear()
        getGames()

    }

    private fun updateUi(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, R.string.google_signin_successful, Toast.LENGTH_SHORT).show()

            //mSignOutButton!!.visibility = View.VISIBLE

            startActivity<MainActivity>()
        } else {

            //  mSignOutButton!!.visibility = View.GONE
        }
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return true
    }

    override fun onItemSelected(pos: Int) {
        TODO("Not yet implemented")
    }
    companion object {
        private const val TAG = "Game List Activity"
    }

    fun getGames(){
        var games: ArrayList<GameModel>
        doAsync {
            games = app.firebasestore.findAllGames()
            Log.i(TAG, "GetGames: "+games)
            uiThread {
                Log.i(TAG, "GetGames UiThread")
                showGames(games)
            }
        }
    }


    fun showGames(games:ArrayList<GameModel> ){
        Log.i(TAG, "showGames")
        game_recycler_layout.adapter = GamesAdapter(games, this)
        game_recycler_layout.adapter?.notifyDataSetChanged()
    }



    override fun onGameClick(game: GameModel) {
        app.firebasestore.game = game
        startActivity(intentFor<MainActivity>().putExtra("game_edit", game.id))
    }


}
