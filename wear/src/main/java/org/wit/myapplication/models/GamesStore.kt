package org.wit.myapplication.models

import com.google.firebase.firestore.QuerySnapshot

interface GamesStore{
    fun findReferee(id: String): Map<String,Any>?
    fun findAllGames(): ArrayList<GameModel>?
    fun findGameById(id: String): Map<String,Any>?
    fun findTeam(id: String): Map<String,Any>?

}