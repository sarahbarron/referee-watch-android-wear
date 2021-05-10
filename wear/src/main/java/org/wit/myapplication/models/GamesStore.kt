package org.wit.myapplication.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Job
import java.lang.reflect.Member

interface GamesStore{
    fun findAllGames(): ArrayList<GameModel>?
    fun findGameById(id: String): GameModel?
    fun findAllScores(): ArrayList<ScoreModel>?
    fun findAllCards(): ArrayList<CardModel>?
    fun findAllSubstitutes(): ArrayList<SubstituteModel>?
    fun findAllInjuries(): ArrayList<InjuryModel>?
    fun findTeam(id: String): TeamModel?
    fun findMember(id: String): MemberModel?
    fun findMemberByJerseyNum(team: String, jerseyNum: Int): MemberModel?
    fun saveScore(scoreModel: ScoreModel) : Boolean
    fun saveCard(cardModel: CardModel): Boolean
    fun checkIfPlayerHasABlackOrYellowCard(memberDocRef: DocumentReference): Boolean
    fun isPlayerOnTheField(team: String, jerseyNum: Int): Boolean
}