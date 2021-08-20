package org.wit.myapplication.models.firebase

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_stopwatch.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.wit.myapplication.models.*
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

    var teamASubs = 5
    var teamBSubs = 0
    var teamABlackCardSubs = 0
    var teamBBlackCardSubs = 3
    var sport = ""
    var hurlingSubsAllowed = 5
    var footballSubsAllowed = 6
    var footballBlackCardSubsAllowed = 3

    var gameStarted = false;

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

    override fun findAllTeamsheetA(): ArrayList<TeamsheetPlayerModel>?{
        return teamAPlayers
    }
    override fun findAllTeamsheetB(): ArrayList<TeamsheetPlayerModel>?{
        return teamBPlayers
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

        if (team == "teamA") {
            var teamsheetPlayer = teamAPlayers.find { p -> p.jerseyNumber == jerseyNum }
            if(teamsheetPlayer != null)
                member = allPlayers.find { p -> p.id == teamsheetPlayer?.id }!!
        }
        if (team == "teamB") {
            var teamsheetPlayer = teamBPlayers.find { p -> p.jerseyNumber == jerseyNum }
            if(teamsheetPlayer !=null)
                member = allPlayers.find { p -> p.id == teamsheetPlayer?.id }!!
        }
        return member
    }

    override fun saveScore(scoreModel: ScoreModel): Boolean {
        try {
            db.collection("Scores").document().set(scoreModel)
          //  scores.add(scoreModel)
            Log.i(TAG, "firestore save score scoreModel = $scoreModel\nscores = $scores")
            return true
        } catch (e: java.lang.Exception) {
            Log.w(TAG, "Error Saving Score: $e")

        }
        return false
    }

    override fun saveCard(cardModel: CardModel): Boolean {
        try {
            db.collection("Cards").document().set(cardModel)
            cards.add(cardModel)
        } catch (e: Exception) {
            Log.w(TAG, "Error saving Card : $e")
            return false
        }
        return true
    }

    override fun saveSub(substituteModel: SubstituteModel): Boolean {
        try {
            db.collection("Substitute").document().set(substituteModel)
            substitutes.add(substituteModel)
        } catch (e: Exception) {
            Log.w(TAG, "Error saving  Substitute : $e")
            return false
        }
        return true
    }

    override fun saveInjury(injuryModel: InjuryModel): Boolean {
        try{
            db.collection("Injury").document().set(injuryModel)
            injuries.add(injuryModel)
        }catch (e:java.lang.Exception){
            Log.w(TAG, "Error Saving Injury")
            return false
        }
        return true
    }

    override fun setPlayerOnField(team: String, jerseyNum: Int) {
        var teamPlayers = ArrayList<TeamsheetPlayerModel>()
        var teamId: String? = null
        var gameId = game.id
        if (team == "teamA") {
            teamPlayers = teamAPlayers
            teamId = teamA.id
        } else if (team == "teamB") {
            teamPlayers = teamBPlayers
            teamId = teamB.id
        }
        val teamsheetPlayerOn = teamPlayers.find { p -> p.jerseyNumber == jerseyNum }
        if (teamsheetPlayerOn != null) {
            teamsheetPlayerOn.onField = true
        }
        if (teamsheetPlayerOn != null && teamId != null) {
            try {
                if (gameId != null) {
                    db.collection("Game").document(gameId).collection("teamsheet").document(teamId)
                        .collection("players").document(teamsheetPlayerOn.id!!)
                        .update("onField", true)
                }

            } catch (e: Exception) {
                Log.w(TAG, "Exception when setting player on field: $e")
            }
        }


    }

    override fun setPlayerOffField(team: String, jerseyNum: Int) {
        var teamPlayers = ArrayList<TeamsheetPlayerModel>()
        var teamId:String? = null
        var gameId = game.id
        if (team == "teamA") {
            teamPlayers = teamAPlayers
            teamId = teamA.id
        }
        else if (team == "teamB") {
            teamPlayers = teamBPlayers
            teamId = teamB.id

        }
        val teamsheetPlayerOff = teamPlayers.find { p -> p.jerseyNumber == jerseyNum }

        if (teamsheetPlayerOff != null) {
            teamsheetPlayerOff.onField = false
        }
        if (teamsheetPlayerOff != null && teamId != null) {
            try {
                if (gameId != null) {
                    db.collection("Game").document(gameId).collection("teamsheet").document(teamId)
                        .collection("players").document(teamsheetPlayerOff.id!!)
                        .update("onField", false)
                }

            } catch (e: Exception) {
                Log.w(TAG, "Exception when setting player on field: $e")
            }
        }
    }


    override fun updateBlackCardSubs(team: String) {
        if (team == "teamA") teamABlackCardSubs += 1
        else if (team == "teamB") teamBBlackCardSubs += 1
        Log.i(TAG, "BLACK CARD team $team: $teamABlackCardSubs, $teamBBlackCardSubs ")
    }

    override fun isTeamAllowedFootballBlackCardSubs(team: String): Boolean {
        if (team == "teamA") {
            if (teamABlackCardSubs >= footballBlackCardSubsAllowed)
                return false
        } else if (team == "teamB") {
            if (teamBBlackCardSubs >= footballBlackCardSubsAllowed)
                return false
        }
        return true
    }

    override fun isTeamAllowedNormalSubs(team: String): Boolean {
        if (sport == "Hurling") {
            if (team == "teamA") {
                if (teamASubs >= hurlingSubsAllowed)
                    return false
            } else if (team == "teamB") {
                if (teamBSubs >= hurlingSubsAllowed)
                    return false
            }
        } else if (sport == "Gaelic Football") {
            if (team == "teamA") {
                if (teamASubs >= footballSubsAllowed)
                    return false
            } else if (team == "teamB") {
                if (teamBSubs >= footballSubsAllowed)
                    return false
            }
        }
        return false
    }

    override fun updateNormalSubs(team: String) {
        if (team == "teamA") teamASubs += 1
        else if (team == "teamB") teamBSubs += 1
        Log.i(TAG, "NORMAL CARDS team $team: $teamASubs, $teamBSubs")

    }

    override fun checkIfPlayerIsOnASecondCard(memberDocRef: DocumentReference): Boolean {
        val memberCards = cards.filter { p -> p.member == memberDocRef }
        Log.i(TAG, "$memberDocRef: $memberCards")
        if (memberCards.size > 1) return true

        return false
    }


    override fun isPlayerOnTheField(team: String, jerseyNum: Int): Boolean {
        try {
            var player: TeamsheetPlayerModel? = null
            if (team == "teamA") player = teamAPlayers.find { p -> p.jerseyNumber == jerseyNum }
            if (team == "teamB") player = teamBPlayers.find { p -> p.jerseyNumber == jerseyNum }

            if (player != null) if (player.onField) return true


        } catch (e: Exception) {
            Log.i(TAG, "Error finding if player is on the field")
        }
        return false
    }

    override fun isPlayerOnTheTeamSheet(team: String, jerseyNum: Int): Boolean {
        try {
            var player: TeamsheetPlayerModel? = null
            if (team == "teamA") player = teamAPlayers.find { p -> p.jerseyNumber == jerseyNum }
            if (team == "teamB") player = teamBPlayers.find { p -> p.jerseyNumber == jerseyNum }

            if (player != null) return true
        }catch (e:java.lang.Exception){
            Log.i(TAG, "Error finding player on the teamsheet")
        }
        return false
    }

    override fun updateTeamAGoalTotal() {
        game.id?.let { db.collection("Game").document(it) }
            ?.update("teamATotalGoals", FieldValue.increment(1));
    }

    override fun updateTeamBGoalTotal() {
        game.id?.let { db.collection("Game").document(it) }
            ?.update("teamBTotalGoals", FieldValue.increment(1));
    }

    override fun updateTeamAPointsTotal() {
        game.id?.let { db.collection("Game").document(it) }
            ?.update("teamATotalPoints", FieldValue.increment(1));
    }

    override fun updateTeamBPointsTotal() {
        game.id?.let { db.collection("Game").document(it) }
            ?.update("teamBTotalPoints", FieldValue.increment(1));
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
//    fun fetchScores(gameId: String)  {
//        try {
//            db = FirebaseFirestore.getInstance()
//            Log.i(TAG, "Fetch Scores game Id $gameId")
//            var gameDoc = db.collection("Game").document(gameId)
//            db.collection("Scores")
//                .whereEqualTo("game", gameDoc)
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .addSnapshotListener addSnapshotListener@{ snapshot, e ->
//                    Log.i(TAG, " Number Of Scores : " + (snapshot?.size() ?: null))
//                    if (e != null) {
//                        Log.i(TAG, "Listen failed.", e)
//                        return@addSnapshotListener
//                    }
//                    if (snapshot != null) {
//                        scores.clear()
//                        snapshot.documents.mapNotNullTo(scores) {
//                            it.toObject(ScoreModel::class.java)
//                        }
//                    } else {
//                        Log.i(TAG, "Scores = null")
//                    }
//                }
//        } catch (e: Exception) {
//            Log.w(TAG, "Fetch Team Exception: $e")
//        }
//    }

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
                    teamB = data.toObject(TeamModel::class.java)!!
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
                    .addSnapshotListener{teamsheetplayers, e ->
                        if(teamsheetplayers!=null && !teamsheetplayers.isEmpty) {
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
                        }
                    }
            } catch (e: java.lang.Exception) {
                Log.w(TAG, "Fetch Teamsheet Players exception $e")
            }
        }

    //    Fetch Teamsheet Players
    fun fetchPlayers(teamsheet: ArrayList<TeamsheetPlayerModel>) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
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
            } catch (e: Exception) {
                Log.i(TAG, "Error fetching players")
            }
        }


    override fun setStartTimeOFGame():Boolean{
        try{
            var gameId = game.id
            var time = Date();
            if(gameId!=null) {
                db.collection("Game").document(gameId).update("matchStarted", time);
            }
            gameStarted=true;
            return true
        }
        catch (e: java.lang.Exception){
            Log.w(GamesFireStore.TAG,
                "Error setting start time of the game");
        }
        return false
    }
    override fun setEndTimeOFGame():Boolean{
        try{
            var gameId = game.id
            var time = Date();
            if(gameId!=null) {
                db.collection("Game").document(gameId).update("matchEnded", time);
            }
            return true
        }
        catch (e: java.lang.Exception){
            Log.w(GamesFireStore.TAG, "Error setting end time of the game");
        }
        return false
    }



    override  fun saveAdditionalComments(comments: AdditionalCommentsModel):Boolean{
        try{
            var gameId = game.id
            var matchProgramme = comments.matchProgramme
            var pitchMarked= comments.pitchMarked
            var grassCut = comments.grassCut
            var jerseyNumbered = comments.jerseyNumbered
            var linesmenAttire = comments.linesmenAttire
            var delayInStart = comments.delayInStart
            var extraComments = comments.extraComments
            if(gameId!=null){
                db.collection("Game").document(gameId).update("matchProgramme", matchProgramme)
                db.collection("Game").document(gameId).update("pitchMarked", pitchMarked)
                db.collection("Game").document(gameId).update("grassCut", grassCut)
                db.collection("Game").document(gameId).update("jerseyNumbered", jerseyNumbered)
                db.collection("Game").document(gameId).update("linesmenAttire", linesmenAttire)
                db.collection("Game").document(gameId).update("delayInStart",delayInStart)
                db.collection("Game").document(gameId).update("extraComments",extraComments)
                return true
            }
        } catch (e: java.lang.Exception){
            Log.w(GamesFireStore.TAG, "Error saving additional notes");
        }
        return false
    }

    companion object {
        private const val TAG = "Firestore"
    }



}
