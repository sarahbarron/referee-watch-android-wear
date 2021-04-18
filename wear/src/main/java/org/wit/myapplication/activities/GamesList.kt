package org.wit.myapplication.activities

import android.app.Activity
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.Gravity.apply
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.solver.ArrayLinkedVariables
import androidx.core.view.GravityCompat.apply
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import com.google.firebase.firestore.FirebaseFirestore
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp
import androidx.wear.widget.WearableRecyclerView
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_games_list.*
import kotlinx.coroutines.awaitAll
import org.jetbrains.anko.startActivity
import org.wit.myapplication.adapters.GamesAdapter
import org.wit.myapplication.adapters.GamesListener
import kotlin.reflect.typeOf


class GamesList: FragmentActivity(), GamesListener,
        AmbientModeSupport.AmbientCallbackProvider, MenuItem.OnMenuItemClickListener,
        WearableNavigationDrawerView.OnItemSelectedListener {

        lateinit var app: MainApp
        lateinit var db : FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games_list)

        app = application as MainApp
        db = FirebaseFirestore.getInstance()

        val layoutManager = WearableLinearLayoutManager(this)
        recycler_games_list.layoutManager = layoutManager
        recycler_games_list.setHasFixedSize(true)
        recycler_games_list.isEdgeItemsCenteringEnabled = true

        recycler_games_list.adapter = GamesAdapter(getListOfGames(), this)







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
        TODO("Not yet implemented")
    }

    override fun onItemSelected(pos: Int) {
        TODO("Not yet implemented")
    }
    companion object {
        private const val TAG = "Game List Activity"
    }

    fun getListOfGames() :ArrayList<ArrayList<String>>{

        var listOfGames = ArrayList<ArrayList<String>>()
        var singleGame = ArrayList<String>()

        val user = app.auth.currentUser
        val referee = db.collection("Member").document(user.uid)
        Log.d(TAG,"UID : "+user.uid+ ", Referee: " + referee)


        db!!.collection("Game").whereEqualTo("referee", referee)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {


                        for (document in task.result!!) {
                            var teamA: Any? =null
                            var teamB: Any? =null
                            var a = document.getDocumentReference("teamA")?.id
                            var b = document.getDocumentReference("teamB")?.id
                            var gameId = document.id
                            Log.w(TAG,"gameId: "+gameId)

                            singleGame.add(gameId)


                            // return teamA name
                            if(a!=null && b!=null) {
                                db.collection("Team").document(a)
                                        .get()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val document = task.result!!
                                                if (document != null) {
                                                    teamA = document.data?.get("name")!!
                                                    Log.w(TAG,"TeamA"+teamA , task.exception)

                                                    db.collection("Team").document(b)
                                                            .get()
                                                            .addOnCompleteListener { task ->
                                                                if (task.isSuccessful) {
                                                                    val document = task.result!!
                                                                    if (document != null) {
                                                                        teamB = document.data?.get("name")!!
                                                                        Log.w(TAG,"TeamB"+teamB , task.exception)
                                                                        val game = ( ""+teamA+" V " + teamB)
                                                                        singleGame.add(game)
                                                                        listOfGames.add(singleGame)
                                                                        Log.w(TAG, "Game: "+ game)
                                                                    } else {
                                                                        Log.w(TAG, "Error getting documents.", task.exception)
                                                                        Toast.makeText(
                                                                                this,
                                                                                "No Team B",
                                                                                Toast.LENGTH_LONG
                                                                        ).show()

                                                                    }

                                                                }

                                                            }

                                                } else {
                                                    Log.w(TAG, "Error getting documents.", task.exception)
                                                    Toast.makeText(
                                                            this,
                                                            "No Team A",
                                                            Toast.LENGTH_LONG
                                                    ).show()

                                                }
                                            }
                                        }

                            }


                        }

                    }
                    else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }

                }
        return(listOfGames)
    }

    override fun onGameClick(game: String) {

    }


}


