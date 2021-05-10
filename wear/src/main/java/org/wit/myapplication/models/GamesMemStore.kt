package org.wit.myapplication.models
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import org.wit.myapplication.models.*

class GamesMemStore : GamesStore {
    val games = ArrayList<GameModel>()
    val teams = ArrayList<TeamModel>()
    val scores = ArrayList<ScoreModel>()



    override fun findAllGames():ArrayList<GameModel>? {
        TODO("Not yet implemented")
    }

    override fun findGameById(id: String): GameModel? {
        TODO("Not yet implemented")
    }


    override fun findAllScores(): ArrayList<ScoreModel>? {
        TODO("Not yet implemented")
    }

    override fun findAllCards(): ArrayList<CardModel>? {
        TODO("Not yet implemented")
    }

    override fun findAllSubstitutes(): ArrayList<SubstituteModel>? {
        TODO("Not yet implemented")
    }

    override fun findAllInjuries(): ArrayList<InjuryModel>? {
        TODO("Not yet implemented")
    }

    override fun findTeam(id: String): TeamModel? {
        TODO("Not yet implemented")
    }

    override fun findMember(id: String): MemberModel? {
        TODO("Not yet implemented")
    }

    override fun findMemberByJerseyNum(team: String, jerseyNum: Int): MemberModel? {
        TODO("Not yet implemented")
    }

    override fun saveScore(scoreModel: ScoreModel) : Boolean {
        TODO("Not yet implemented")
    }

    override fun saveCard(cardModel: CardModel): Boolean {
        TODO("Not yet implemented")
    }

    override fun checkIfPlayerHasABlackOrYellowCard(memberDocRef: DocumentReference): Boolean {
        TODO("Not yet implemented")
    }


    override fun isPlayerOnTheField(team: String, jerseyNum: Int):Boolean {
        TODO("Not yet implemented")
    }

    fun logAllGames(){
        TODO("Not yet implemented")
    }

}