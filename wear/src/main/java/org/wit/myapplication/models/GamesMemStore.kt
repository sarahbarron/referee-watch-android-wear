package org.wit.myapplication.models
import android.util.Log
import com.google.firebase.firestore.QuerySnapshot
import org.wit.myapplication.models.*

class GamesMemStore : GamesStore {
    val games = ArrayList<Map<String, Any>>()
    val teams = ArrayList<Map<String,Any>>()



    override fun findReferee(id: String): Map<String, Any>? {
        TODO(reason = "Not yet implemented")
    }

    override fun findAllGames():ArrayList<GameModel>? {
        TODO("Not yet implemented")
    }

    override fun findGameById(id: String): Map<String,Any>? {
        return games.find{id == id}

    }

    override fun findTeam(id: String): Map<String,Any>? {
        return teams.find{id == id}
    }

    fun logAllGames(){
        Log.v("Game", "Games List")
        games.forEach{Log.v("logAllGames", "${it}")}
    }

}