package org.wit.myapplication.models.firebase

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.wit.myapplication.activities.GamesList
import org.wit.myapplication.models.*
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
    val scores = ArrayList<ScoreModel>()
    val cards = ArrayList<CardModel>()
    val injuries = ArrayList<InjuryModel>()
    val substitutes = ArrayList<SubstituteModel>()
    lateinit var userId: String
    lateinit var db: FirebaseFirestore



    override fun findAllGames(): ArrayList<GameModel> {
        return games
    }

    override fun findGameById(id: String): GameModel? {
       val foundGame: GameModel? = games.find{p->p.id == id}
        return foundGame
    }

    override fun findTeam(id: String): TeamModel? {
        val foundTeam: TeamModel? = teams.find{p->p.id==id}
        return foundTeam
    }

    override fun findAllScores(): ArrayList<ScoreModel>? {
        return scores
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

    fun fetchTeam(ref: DocumentReference){
        db = FirebaseFirestore.getInstance()
        db!!.collection("Team").document(ref.id)
            .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                if (e != null) {
                    Log.i(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if(snapshot !=null)
                {
                        snapshot.toObject(TeamModel::class.java)
                }
                else{
                    Log.i(TAG, "Team = null")
                }
            }
    }


    fun fetchScores(gameRef: DocumentReference) {
        db = FirebaseFirestore.getInstance()
        scores.clear()

        db!!.collection("Scores")
            .whereEqualTo("game", gameRef)
            .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                Log.i(TAG, " Number Of Scores : " + (snapshot?.size() ?: null))
                if (e != null) {
                    Log.i(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if(snapshot !=null)
                {
                    snapshot!!.documents.mapNotNullTo(scores){
                        it.toObject(ScoreModel::class.java)
                    }
                }
                else{
                    Log.i(TAG, "Scores = null")
                }
            }
    }

    fun fetchCards(gameRef: DocumentReference) {
        db = FirebaseFirestore.getInstance()
        cards.clear()

        db!!.collection("Cards")
            .whereEqualTo("game", gameRef)
            .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                Log.i(TAG, " Number Of Cards: " + (snapshot?.size() ?: null))
                if (e != null) {
                    Log.i(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if(snapshot !=null)
                {
                    snapshot!!.documents.mapNotNullTo(cards){
                        it.toObject(CardModel::class.java)
                    }
                }
                else{
                    Log.i(TAG, "Card = null")
                }
            }
    }

    fun fetchInjuries(gameRef: DocumentReference) {
        db = FirebaseFirestore.getInstance()
        injuries.clear()

        db!!.collection("Injury")
            .whereEqualTo("game", gameRef)
            .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                Log.i(TAG, " Number Of Cards: " + (snapshot?.size() ?: null))
                if (e != null) {
                    Log.i(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if(snapshot !=null)
                {
                    snapshot!!.documents.mapNotNullTo(injuries){
                        it.toObject(InjuryModel::class.java)
                    }
                }
                else{
                    Log.i(TAG, "Injury = null")
                }
            }
    }
    fun fetchSubstitutes(gameRef: DocumentReference) {
        db = FirebaseFirestore.getInstance()
        substitutes.clear()

        db!!.collection("Substitute")
            .whereEqualTo("substitute", gameRef)
            .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                Log.i(TAG, " Number Of Subs: " + (snapshot?.size() ?: null))
                if (e != null) {
                    Log.i(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if(snapshot !=null)
                {
                    snapshot!!.documents.mapNotNullTo(substitutes){
                        it.toObject(SubstituteModel::class.java)
                    }
                }
                else{
                    Log.i(TAG, "Substitute = null")
                }
            }
    }


}