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
    fun saveSub(substituteModel: SubstituteModel):Boolean
    fun saveInjury(injuryModel: InjuryModel):Boolean
    fun setPlayerOnField(team: String, jerseyNum: Int)
    fun setPlayerOffField(team: String, jerseyNum:Int)
    fun updateBlackCardSubs(team: String)
    fun isTeamAllowedFootballBlackCardSubs(team: String):Boolean
    fun isTeamAllowedNormalSubs(team:String):Boolean
    fun updateNormalSubs(team: String)
    fun checkIfPlayerIsOnASecondCard(memberDocRef: DocumentReference): Boolean
    fun isPlayerOnTheField(team: String, jerseyNum: Int): Boolean
    fun isPlayerOnTheTeamSheet(team:String, jerseyNum: Int):Boolean
    fun updateTeamAGoalTotal()
    fun updateTeamBGoalTotal()
    fun updateTeamAPointsTotal()
    fun updateTeamBPointsTotal()
    fun setStartTimeOFGame(): Boolean
    fun setEndTimeOFGame(): Boolean
    fun saveAdditionalComments(comments: AdditionalCommentsModel):Boolean
    fun findAllTeamsheetB(): ArrayList<TeamsheetPlayerModel>?
    fun findAllTeamsheetA(): ArrayList<TeamsheetPlayerModel>?
    fun setTimeTeamBTookToField(): Boolean
    fun setTimeTeamATookToField(): Boolean
}