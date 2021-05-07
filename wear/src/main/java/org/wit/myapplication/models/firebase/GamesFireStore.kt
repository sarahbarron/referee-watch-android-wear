package org.wit.myapplication.models.firebase

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_stopwatch.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.wit.myapplication.models.*
import org.wit.myapplication.models.LiveDataViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList


class GamesFireStore(val context: Context) : GamesStore {

    var games = ArrayList<GameModel>()
    var game = GameModel()

    var teamA = TeamModel()
    var teamB = TeamModel()

    var teamAPlayers = ArrayList<TeamsheetPlayerModel>()
    var teamBPlayers = ArrayList<TeamsheetPlayerModel>()

    var allPlayers = ArrayList<MemberModel>()

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
        game = games.find { p -> p.id == id }!!
        return game
    }


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


    override fun findTeam(id: String): TeamModel? {
        if (teamA?.id == id) {
            return teamA
        } else if (teamB?.id == id) {
            return teamB
        } else return null
    }

    override fun findMember(id: String): MemberModel? {
        val player = allPlayers.find { p -> p.id == id }!!
        return player

    }

    override fun findMemberByJerseyNum(team: String, jerseyNum: Int): MemberModel? {
        var member = MemberModel()
        if (team === "teamA") {
            val teamsheetPlayer = teamAPlayers.find { p -> p.jerseyNumber == jerseyNum }
            member = allPlayers.find { p -> p.id == teamsheetPlayer?.id }!!
        }
        if (team === "teamB") {
            val teamsheetPlayer = teamBPlayers.find { p -> p.jerseyNumber == jerseyNum }
            member = allPlayers.find { p -> p.id == teamsheetPlayer?.id }!!
        }
        return member
    }

    override fun saveScore(scoreModel: ScoreModel) {
        db.collection("Scores").document().set(scoreModel)
        scores.add(scoreModel)
        Log.i(TAG, "firestore save score scoreModel = $scoreModel\nscores = $scores")

    }


    // FETCHES FROM FIRESTORE

    //    Fetch Users
    fun fetchUser() {
        try {
            userId = FirebaseAuth.getInstance().currentUser!!.uid
            db.collection("Game").document(userId)
                .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                    if (e != null) {
                        Log.i(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        Log.i(TAG, "User: ${snapshot}")
                    } else {
                        Log.i(TAG, "User = null")
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Fetch User Exception: $e")
        }


    }

    //    Fetch Games
    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchGames() {
        try {
            db = FirebaseFirestore.getInstance()
            userId = FirebaseAuth.getInstance().currentUser!!.uid
            val referee = db.collection("Member").document(userId)
            Log.i(TAG, "UserID: " + userId)

            // Get the local date
            var localdate = LocalDate.now()
            // get the date from the start of day
            val today = Date.from(localdate.atStartOfDay(ZoneId.systemDefault()).toInstant())
            // Simply plus 1 day to make it tomorrows date
            localdate = localdate.plusDays(1)
            // convert it to type Date at the start of tomorrows date
            val tomorrow = Date.from(localdate.atStartOfDay(ZoneId.systemDefault()).toInstant())

            db.collection("Game")
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
                    if (snapshot != null) {
                        games.clear()
                        snapshot.documents.mapNotNullTo(games) {
                            it.toObject(GameModel::class.java)
                        }
                        Log.i(TAG, "AFTER FETCH GAMES : $games")

                    } else {
                        Log.i(TAG, "Games = null")
                    }
                }
        } catch (e: Exception) {

            Log.w(TAG, "Fetch Games Exception: $e")

        }
    }


    // Fetch A Scores
    fun fetchScores(gameId: String) {
        try {
            db = FirebaseFirestore.getInstance()
            Log.i(TAG, "Fetch Scores game Id $gameId")
            var gameDoc = db.collection("Game").document(gameId)
            db.collection("Scores")
                .whereEqualTo("game", gameDoc)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                    Log.i(TAG, " Number Of Scores : " + (snapshot?.size() ?: null))
                    if (e != null) {
                        Log.i(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        scores.clear()
                        snapshot.documents.mapNotNullTo(scores) {
                            it.toObject(ScoreModel::class.java)
                        }
                    } else {
                        Log.i(TAG, "Scores = null")
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Fetch Team Exception: $e")
        }
    }

    //    Fetch Cards
    fun fetchCards(gameRef: String) {
        try {
            db = FirebaseFirestore.getInstance()
            var gameDoc = db.collection("Game").document(gameRef)
            db.collection("Cards")
                .whereEqualTo("game", gameDoc)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                    Log.i(TAG, " Number Of Cards: " + (snapshot?.size() ?: null))
                    if (e != null) {
                        Log.i(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        cards.clear()
                        snapshot.documents.mapNotNullTo(cards) {
                            it.toObject(CardModel::class.java)
                        }
                    } else {
                        Log.i(TAG, "Card = null")
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Fetch Cards Exception: $e")
        }

    }

    //    Fetch Injuries
    fun fetchInjuries(gameRef: String) {
        try {
            db = FirebaseFirestore.getInstance()
            var gameDoc = db.collection("Game").document(gameRef)

            db.collection("Injury")
                .whereEqualTo("game", gameDoc)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                    Log.i(TAG, " Number Of Injuries: " + (snapshot?.size() ?: null))
                    if (e != null) {
                        Log.i(TAG, "Injury Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        injuries.clear()
                        snapshot.documents.mapNotNullTo(injuries) {
                            it.toObject(InjuryModel::class.java)
                        }
                    } else {
                        Log.i(TAG, "Injury = null")
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Fetch Injuries Exception: $e")
        }

    }

    //    Fetch Substitutes
    fun fetchSubstitutes(gameRef: String) {
        try {
            db = FirebaseFirestore.getInstance()
            var gameDoc = db.collection("Game").document(gameRef)

            db.collection("Substitute")
                .whereEqualTo("game", gameDoc)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                    Log.i(TAG, " Number Of Subs: " + (snapshot?.size() ?: null))
                    if (e != null) {
                        Log.i(TAG, "Listen failed. $e", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        substitutes.clear()
                        snapshot.documents.mapNotNullTo(substitutes) {
                            it.toObject(SubstituteModel::class.java)
                        }
                        Log.i(TAG, "fetch Subs : $substitutes")
                    } else {
                        Log.i(TAG, "Substitutes = null")
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Fetch Substitutes Exception: $e")
        }

    }

    //    Fetch A Team
    fun fetchTeam(gameref: String, teamref: String, team: String) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db = FirebaseFirestore.getInstance()
                Log.i(TAG, "fetchTeam $teamref, $team")

                val data = db.collection("Team").document(teamref).get().await()
                if (team == "teamA") {
                    teamA = data.toObject(TeamModel::class.java)!!
                    Log.i(TAG, "LiveData postValue Team A = ${teamA}")
                    fetchTeamsheetPlayers(gameref, teamref, "teamA")
                }

                if (team == "teamB") {
                    teamB =data.toObject(TeamModel::class.java)!!
                    Log.i(TAG, "LiveData postValue Team B = ${teamB}")
                    fetchTeamsheetPlayers(gameref, teamref, "teamB")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Fetch Team Exception: $e")
            }

        }


    //    Fetch Teamsheet Players
    fun fetchTeamsheetPlayers(gameId: String, teamId: String, team: String) =
        CoroutineScope(Dispatchers.IO).launch {

            try {
                var db: FirebaseFirestore = FirebaseFirestore.getInstance()

                val teamsheetplayers = db.collection("Game")
                    .document(gameId)
                    .collection("teamsheet")
                    .document(teamId)
                    .collection("players")
                    .get().await()

                if (team == "teamA") {
                    teamAPlayers.clear()
                    teamsheetplayers.documents.mapNotNullTo(teamAPlayers) {
                        it.toObject(TeamsheetPlayerModel::class.java)
                    }
                    fetchPlayers(teamAPlayers)
                    Log.i(TAG, "FETCH TEAM A PLAYERS $teamAPlayers")
                }
                if (team == "teamB") {
                    teamBPlayers.clear()
                    teamsheetplayers.documents.mapNotNullTo(teamBPlayers) {
                        it.toObject(TeamsheetPlayerModel::class.java)
                    }
                    fetchPlayers(teamBPlayers)
                    Log.i(TAG, "FETCH TEAM B PLAYERS $teamBPlayers")
                }
            } catch (e: java.lang.Exception) {
                Log.w(TAG, "Fetch Teamsheet Players exception $e")
            }
        }

    //    Fetch Teamsheet Players
    fun fetchPlayers(teamsheet: ArrayList<TeamsheetPlayerModel>) =
        CoroutineScope(Dispatchers.IO).launch {
            var db: FirebaseFirestore = FirebaseFirestore.getInstance()
            Log.i(TAG, "FetchPlayers \nTeamsheet: $teamsheet")

            for (player in teamsheet) {
                try {
                    val member = db.collection("Member").document(player.id!!).get().await()
                    val p = member.toObject(MemberModel::class.java)!!
                    allPlayers.add(p)
                    Log.i(TAG, "Player $p")
                } catch (e: Exception) {
                    Log.w(TAG, "fetch players exception: $e")
                }
            }
        }

    companion object {
        private const val TAG = "Firestore"
    }


}

