package org.wit.myapplication.fragments


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.database.snapshot.BooleanNode
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import convertJerseyNumToInt
import kotlinx.android.synthetic.main.fragment_cards.view.*
import kotlinx.android.synthetic.main.fragment_score.view.*
import kotlinx.android.synthetic.main.fragment_sub.*
import kotlinx.android.synthetic.main.fragment_sub.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.LiveDataViewModel
import org.wit.myapplication.models.MemberModel
import org.wit.myapplication.models.SubstituteModel
import org.wit.myapplication.models.TeamModel
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule

@Suppress("DEPRECATION")
class SubFragment : Fragment() {

    lateinit var app: MainApp
    lateinit var root: View
    private val model: LiveDataViewModel by activityViewModels()

    var sub = SubstituteModel()
    var member = MemberModel()
    var teamA: TeamModel? = null
    var teamB: TeamModel? = null
    lateinit var teamAbtn: ToggleButton
    lateinit var teamBbtn: ToggleButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        app = activity?.application as MainApp
        root = inflater.inflate(R.layout.fragment_sub, container, false)

        val teamAName = app.firebasestore.teamA.name
        val teamBName = app.firebasestore.teamB.name
        if (teamAName != null && teamBName != null) {
            root.sub_team1.text = teamAName
            root.sub_team2.text = teamBName
            root.sub_team1.textOn = teamAName
            root.sub_team1.textOff = teamAName
            root.sub_team2.textOn = teamBName
            root.sub_team2.textOff = teamBName
        }
        teamButtonListener(root)
        saveSubListener(root)
        blackCardCheckedListner(root)

        val sport = app.firebasestore.sport
        if (sport == "Hurling") {
            //  root.sub_blackcard_checkbox.isEnabled = false

        }
        return root
    }

    //    Listen for a click on a team
    fun teamButtonListener(view: View) {

        teamAbtn = view.sub_team1
        teamBbtn = view.sub_team2
        // Toogle Button Listening for a Team to be selected
        teamAbtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                teamBbtn.isChecked = false
                teamA = model.teamA.value

            } else {
                teamA = null
            }
        }
        teamBbtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                teamAbtn.isChecked = false
                teamB = model.teamB.value

            } else {
                teamB = null
            }
        }
    }

    //    Listener for a click on the save card button
    fun saveSubListener(view: View) {
        val db = FirebaseFirestore.getInstance()
        var teamDocRef: DocumentReference? = null
        var team: String? = null
        var memberOnDocRef: DocumentReference? = null
        var memberOffDocRef: DocumentReference? = null
        var playerComingOnAlreadyTheOnField: Boolean?
        var playerGoingOffIsTheOnField: Boolean?
        var jerseyNumOn: Int? = null
        var jerseyNumOff: Int? = null
        var bloodsub = false
        var blackcard = false
        var allowedSubs = false

        view.btnSaveSub.setOnClickListener {
            try {
                // Check if a team has been selected and get the selected teams document reference
                if (teamA != null) {
                    teamDocRef = db.collection("Team").document(model.teamA.value!!.id.toString())
                    team = "teamA"
                } else if (teamB != null) {
                    teamDocRef = db.collection("Team").document(model.teamB.value!!.id.toString())
                    team = "teamB"
                } else alertReferee("Select A Team")


                // If a team has been selected check if the team is allowed more subs
                if (team == "teamA" || team == "teamB") {
                    bloodsub = view.sub_bloodsub_checkbox.isChecked
                    blackcard = view.sub_blackcard_checkbox.isChecked
                    if (!bloodsub!! && !blackcard!!)
                        allowedSubs = checkIfTeamIsAllowedMoreNormalSubs(team!!)
                    else if(bloodsub) allowedSubs = true

                    // Check if this team is allowed more subs
                    if (!allowedSubs) {
                        alertReferee("Maximum Subs Used\n Don't Allow Substitution")
                        resetSubstitute()
                    }
                }

                // If subs are allowed continue
                if (allowedSubs) {
                    // Get the Jersey Input, check that its not empty and return the int value or null
                    jerseyNumOn = getJerseyOn(view.sub_player_on_number_input.text.toString())
                    jerseyNumOff = getJerseyOff(view.sub_player_off_number_input.text.toString())

                    if(jerseyNumOn == null && jerseyNumOff == null) alertReferee("Input Jersey Numbers")
                    else if(jerseyNumOn == null) alertReferee("Input Jersey Number\nFor Player Coming On")
                    else if(jerseyNumOff == null)alertReferee("Input Jersey Number\nFor Player Going Off")
                    else{
                        /* get the member document reference of the number inputted or null if
                        the number inputted isn't on the team sheet
                         */
                        memberOnDocRef = getMemberDocRef(team!!, jerseyNumOn!!)
                        memberOffDocRef = getMemberDocRef(team!!, jerseyNumOff!!)

                        if(memberOnDocRef == null && memberOffDocRef == null)alertReferee("Player $jerseyNumOn && Player $jerseyNumOff are not on the teamsheet")
                        else if(memberOnDocRef == null)alertReferee("Player $jerseyNumOn is not on the teamsheet")
                        else if(memberOffDocRef == null)alertReferee("Player $jerseyNumOff is not on the teamsheet")
                        // check if the player is currently on the field
                        else{
                            playerComingOnAlreadyTheOnField =
                                app.firebasestore.isPlayerOnTheField(team!!, jerseyNumOn!!)
                            playerGoingOffIsTheOnField =
                                app.firebasestore.isPlayerOnTheField(team!!, jerseyNumOff!!)

                            if(playerComingOnAlreadyTheOnField!! && !playerGoingOffIsTheOnField!!) {
                                alertReferee("Check Jersey\nNumbers Inputted\nfor Player On \nand Player Off")
                                memberOnDocRef = null
                                memberOffDocRef = null
                            }
                            else if (playerComingOnAlreadyTheOnField!!) {
                                alertReferee("Check Input\nPlayer On: $jerseyNumOn\nIs Already ON the field")
                                memberOnDocRef = null
                            }
                            else if (!playerGoingOffIsTheOnField!!) {
                                alertReferee("Check Input\nPlayer OFF: $jerseyNumOff\nIs NOT on the field")
                                memberOffDocRef = null
                            }
                        }
                    }
                }

                if (teamDocRef != null && memberOnDocRef != null && memberOffDocRef != null && allowedSubs) {
                    sub.game = db.collection("Game").document(app.firebasestore.game.id!!)
                    sub.playerOn = memberOnDocRef
                    sub.playerOff = memberOffDocRef
                    sub.team = teamDocRef
                    sub.bloodsub = bloodsub
                    sub.blackcard = blackcard
                    sub.timestamp = Date()

                    doAsync {

                        val subSaved = app.firebasestore.saveSub(sub)

                        if (subSaved) {
                            // Update the number of substitutes used
                            if (blackcard) app.firebasestore.updateBlackCardSubs(team!!)
                            else app.firebasestore.updateNormalSubs(team!!)
                            app.firebasestore.setPlayerOffField(team!!, jerseyNumOff!!)
                            app.firebasestore.setPlayerOnField(team!!, jerseyNumOn!!)
                            resetSubstitute()
                        }

                        uiThread {

                            if(subSaved) alertReferee("Sub Saved")
                            else alertReferee("Sub NOT Saved")
                        }
                    }
                }

            } catch (e: Exception) {
                Log.w(TAG, "Exception: when saving sub $e")
            }
        }
    }

    fun getJerseyOn(jerseyText: String): Int? {
        if (jerseyText.isBlank()) {
            return null
        }
        return convertJerseyNumToInt(jerseyText)
    }

    fun getJerseyOff(jerseyText: String): Int? {
        if (jerseyText.isBlank()) {
            return null
        }
        return convertJerseyNumToInt(jerseyText)
    }

    //        Return the member document reference  from teams heet A
    fun getMemberDocRef(team: String, jerseyInput: Int): DocumentReference? {

        val db = FirebaseFirestore.getInstance()
        var memberDocRef: DocumentReference?
        member = app.firebasestore.findMemberByJerseyNum(team, jerseyInput)!!
        if (member.id != null) {
            memberDocRef = db.collection("Member").document(member.id!!)
            return memberDocRef
        }
        Log.i(
            TAG,
            "GetMember: Number inputted: ${jerseyInput}Member: ${member.firstName} ${member.lastName} ${member.id}"
        )
        return null
    }


    fun alertReferee(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /* Check if the Team is allowed another normal substitution or are they at max
    return true if the team is allowed another sub or flase if the team is not allowed another sub*/
    fun checkIfTeamIsAllowedMoreNormalSubs(team: String): Boolean {
        var numberOfSubsUsed = 0
        var numberOfSubsAllowed = 0
        val sport = (app.firebasestore.sport)
        if (team == "teamA") numberOfSubsUsed = app.firebasestore.teamASubs
        else if (team == "teamB") numberOfSubsUsed = app.firebasestore.teamBSubs
        if (sport == "Hurling") {
            numberOfSubsAllowed = app.firebasestore.hurlingSubsAllowed
        } else if (sport == "Gaelic Football") numberOfSubsAllowed =
            app.firebasestore.footballSubsAllowed

        Log.i(TAG, "Subs Used: $numberOfSubsUsed < Subs ALlowed $numberOfSubsAllowed")
        if (numberOfSubsUsed < numberOfSubsAllowed) return true

        return false
    }

    fun blackCardCheckedListner(view: View) {
        view.sub_blackcard_checkbox.setOnClickListener {
            var team: String? = null
            var allowedBlackCardSubs: Boolean? = null
            if (teamA != null) team = "teamA"
            if (teamB != null) team = "teamB"
            if (view.sub_blackcard_checkbox.isChecked) {
                Log.i(TAG, "Black Card Ticked, team $team")

                if (team != null) {
                    allowedBlackCardSubs = checkIfTeamIsAllowedMoreBlackCardSubs(team)

                } else {
                    Toast.makeText(
                        context,
                        "Select A Team First",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    view.sub_blackcard_checkbox.isChecked = false
                }

                if (allowedBlackCardSubs != null) {
                    if (!allowedBlackCardSubs) {
                        Log.i(TAG, "Print a message to say sub not allowed")
                        Toast.makeText(
                            context,
                            "Max Black Card Subs\nalready used\n\nPlayer Can't be Replaced",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        resetSubstitute()
                    }
                }
            }

        }
    }

    /* Check if the Team is allowed another black card substitution or are they at max
   return true if the team is allowed another sub or flase if the team is not allowed another sub*/
    fun checkIfTeamIsAllowedMoreBlackCardSubs(team: String): Boolean {
        var numberOfSubsUsed = 0
        var numberOfSubsAllowed = app.firebasestore.footballBlackCardSubsAllowed
        if (team == "teamA") numberOfSubsUsed = app.firebasestore.teamABlackCardSubs
        else if (team == "teamB") numberOfSubsUsed = app.firebasestore.teamBBlackCardSubs

        Log.i(TAG, "$numberOfSubsUsed < $numberOfSubsAllowed")
        if (numberOfSubsUsed < numberOfSubsAllowed) return true

        return false
    }

    //    Reset the form to blank
    fun resetSubstitute() {
        sub = SubstituteModel()
        member = MemberModel()
        teamA = null
        teamB = null
        sub_team1.isChecked = false
        sub_team2.isChecked = false
        sub_player_on_number_input.setText("")
        sub_player_off_number_input.setText("")
        sub_bloodsub_checkbox.isChecked = false
        sub_blackcard_checkbox.isChecked = false
    }

    companion object {
        private const val TAG = "Sub Fragment"
    }
}
