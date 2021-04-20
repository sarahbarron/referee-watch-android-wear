package org.wit.myapplication.models
import android.util.Log
import com.google.firebase.firestore.QuerySnapshot
import org.wit.myapplication.models.*

class GamesMemStore : GamesStore {
    val games = ArrayList<Map<String, Any>>()
    val teams = ArrayList<Map<String,Any>>()



    override fun findAllGames():ArrayList<GameModel>? {
        TODO("Not yet implemented")
    }

    override fun findGameById(id: String): GameModel? {
        TODO("Not yet implemented")
    }

    override fun findTeam(id: String): TeamModel? {
        TODO("Not yet implemented")
    }

    fun logAllGames(){
        TODO("Not yet implemented")
    }

}