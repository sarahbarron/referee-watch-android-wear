package org.wit.myapplication.models
import android.util.Log
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

    fun logAllGames(){
        TODO("Not yet implemented")
    }

}