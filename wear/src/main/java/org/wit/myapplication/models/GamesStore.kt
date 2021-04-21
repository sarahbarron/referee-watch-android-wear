package org.wit.myapplication.models

import com.google.firebase.firestore.QuerySnapshot

interface GamesStore{
    fun findAllGames(): ArrayList<GameModel>?
    fun findGameById(id: String): GameModel?
    fun findAllScores(): ArrayList<ScoreModel>?

}