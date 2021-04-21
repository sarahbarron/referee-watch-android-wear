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

    var games = ArrayList<GameModel>()
    var game = GameModel()

  //  var teams = ArrayList<TeamModel>()
    var teamA = TeamModel()
    var teamB = TeamModel()

    var teamAPlayers = ArrayList<TeamsheetPlayerModel>()
    var teamBPlayers = ArrayList<TeamsheetPlayerModel>()

    var scores = ArrayList<ScoreModel>()
    var cards = ArrayList<CardModel>()
    var injuries = ArrayList<InjuryModel>()
    var substitutes = ArrayList<SubstituteModel>()

    lateinit var userId: String
    lateinit var db: FirebaseFirestore



    override fun findAllGames(): ArrayList<GameModel> {
        return games
    }

    override fun findGameById(id: String): GameModel? {
       game = games.find{ p->p.id == id}!!
        return game
    }

//    override fun findTeam(id: String): TeamModel? {
//        val foundTeam: TeamModel? = teams.find{p->p.id==id}
//        return foundTeam
//    }

    override fun findAllScores(): ArrayList<ScoreModel>? {
        return scores
    }

    override fun findAllCards(): ArrayList<CardModel>? {
        return cards
    }

    override fun findAllSubstitutes(): ArrayList<SubstituteModel>? {
        return substitutes
    }

    override fun findAllInjuries(): ArrayList<InjuryModel>? {
        return injuries
    }


    // FETCHES FROM FIRESTORE

//    Fetch Users
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

//    Fetch Games
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

//    Fetch A Team
    fun fetchTeam(ref: String, team:String){
        db = FirebaseFirestore.getInstance()
        Log.i(TAG, "fetchTeam $ref, $team")
        db!!.collection("Team").document(ref)
            .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                if (e != null) {
                    Log.i(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if(snapshot !=null)
                {
                    if(team == "teamA"){
                        teamA = snapshot.toObject(TeamModel::class.java)!!
                        Log.i(TAG, "Team A = $teamA")
                    }
                    if(team == "teamB"){
                        teamB = snapshot.toObject(TeamModel::class.java)!!
                        Log.i(TAG, "Team B = $teamB")

                    }

                }
                else{
                    Log.i(TAG, "Team = null")
                }
            }
    }

// Fetch A Scores
    fun fetchScores(gameId: String) {
        db = FirebaseFirestore.getInstance()
        scores.clear()
        Log.i(TAG, "Fetch Scores game Id $gameId")
        var gameDoc = db!!.collection("Game").document(gameId)
        db!!.collection("Scores")
            .whereEqualTo("game", gameDoc)
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

//    Fetch Cards
    fun fetchCards(gameRef: String) {
        db = FirebaseFirestore.getInstance()
        cards.clear()
        var gameDoc = db!!.collection("Game").document(gameRef)
        db!!.collection("Cards")
            .whereEqualTo("game", gameDoc)
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

//    Fetch Injuries
    fun fetchInjuries(gameId: String) {
        db = FirebaseFirestore.getInstance()
        injuries.clear()
        var gameDoc = db!!.collection("Game").document(gameId)
        db!!.collection("Injury")
            .whereEqualTo("game", gameDoc)
            .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                Log.i(TAG, " Number Of Injuries: " + (snapshot?.size() ?: null))
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

//    Fetch Substitutes
    fun fetchSubstitutes(gameRef: String) {
        db = FirebaseFirestore.getInstance()
        substitutes.clear()
        var gameDoc = db!!.collection("Game").document(gameRef)
        db!!.collection("Substitute")
            .whereEqualTo("game", gameDoc)
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
                    Log.i(TAG, "Substitutes = null")
                }
            }
    }

//    Fetch Teamsheet Players
    fun fetchTeamsheetPlayers(gameId: String, teamId: String, team: String){
        var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    db!!.collection("Game")
            .document(gameId)
            .collection("teamsheet")
            .document(teamId)
            .collection("players")
            .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                Log.i(TAG, " Number Of Players : " + (snapshot?.size() ?: null))
                if (e != null) {
                    Log.i(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if(snapshot !=null)
                {
                    if(team == "teamA") {
                        teamAPlayers.clear()
                        snapshot!!.documents.mapNotNullTo(teamAPlayers) {
                            it.toObject(TeamsheetPlayerModel::class.java)
                        }
                    }
                    if(team == "teamB") {
                        teamBPlayers.clear()
                        snapshot!!.documents.mapNotNullTo(teamBPlayers) {
                            it.toObject(TeamsheetPlayerModel::class.java)
                        }
                    }

                }
                else{
                    Log.i(TAG, "Games = null")
                }
            }

    }

    companion object {
        private const val TAG = "Firestore"
    }


}