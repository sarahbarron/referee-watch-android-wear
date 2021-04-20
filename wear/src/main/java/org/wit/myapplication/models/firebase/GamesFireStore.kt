package org.wit.myapplication.models.firebase

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.wit.myapplication.activities.GamesList
import org.wit.myapplication.models.GameModel
import org.wit.myapplication.models.GamesStore
import org.wit.myapplication.models.MemberModel
import org.wit.myapplication.models.TeamModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalQueries.localDate
import java.util.*
import kotlin.collections.ArrayList


class GamesFireStore(val context: Context): GamesStore {

    val games = ArrayList<GameModel>()
    val user: MemberModel? = null
    val teams = ArrayList<TeamModel>()
    lateinit var userId: String
    lateinit var db: FirebaseFirestore


    override fun findReferee(id: String): Map<String, Any>? {
        var referee: Map<String, Any>? = null
        db.collection("Member").document(id)
            .get()
            .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document  = task.result!!
                referee = document.data
            }
        }
        return referee
    }

    override fun findAllGames(): ArrayList<GameModel> {
        return games
    }

    override fun findGameById(id: String): Map<String, Any>? {
        var game: Map<String, Any>? = null
        db!!.collection("Game").document(id)
            .get()
            .addOnCompleteListener{ task ->
                if(task.isSuccessful)
                {
                    val document = task.result!!
                    game = document.data
                }
            }
        return game
    }

    override fun findTeam(id: String): Map<String, Any>? {
        var team: Map<String, Any>? = null
        db.collection("Team").document(id)
            .get()
            .addOnCompleteListener{ task->
                if(task.isSuccessful){
                    val document = task.result!!
                    team = document.data
                }
            }
        return team
    }

    fun fetchUser(){
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        db!!.collection("Game").document(userId)
            .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                if (e != null) {
                    Log.i(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if(snapshot !=null)
                {
                    Log.i(TAG, "User: ${snapshot}")
                }
                else{
                    Log.i(TAG, "User = null")
                }
            }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchGames() {
        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        val referee = db.collection("Member").document(userId)
        Log.i(TAG, "UserID: " + userId)

        games.clear()


        // Get the local date
        var localdate = LocalDate.now()
        // get the date from the start of day
        val today = Date.from(localdate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        // Simply plus 1 day to make it tomorrows date
        localdate = localdate.plusDays(1)
        // convert it to type Date at the start of tomorrows date
        val tomorrow = Date.from(localdate.atStartOfDay(ZoneId.systemDefault()).toInstant())


        db!!.collection("Game")
            .whereEqualTo("referee", referee)
            .whereLessThan("dateTime", tomorrow)
            .whereGreaterThanOrEqualTo(
                "dateTime",
                today
            ).orderBy("dateTime", Query.Direction.ASCENDING)
            .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                Log.i(TAG, " Number Of Games : " + (snapshot?.size() ?: null))
                if (e != null) {
                    Log.i(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if(snapshot !=null)
                {
                    snapshot!!.documents.mapNotNullTo(games){
                        it.toObject(GameModel::class.java)

                    }

                }
                else{
                    Log.i(TAG, "Games = null")
                }
            }
    }

    fun fetchTeams(ref: String){
        db = FirebaseFirestore.getInstance()
        db!!.collection("Team").document(ref)
            .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                if (e != null) {
                    Log.i(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if(snapshot !=null)
                {
                    Log.i(TAG, "Team: ${snapshot}")
                }
                else{
                    Log.i(TAG, "Team = null")
                }
            }


    }
}