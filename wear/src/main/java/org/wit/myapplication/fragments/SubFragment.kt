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
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
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

        teamButtonListener(root)
        saveSubListener(root)
        blackCardCheckedListner(root)

        val sport = app.firebasestore.sport
        if(sport == "Hurling"){
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
        var team = ""
        var memberOnDocRef: DocumentReference? = null
        var memberOffDocRef: DocumentReference? = null
        var playerOnJerseyInput = 0
        var playerOffJerseyInput = 0
        var playerOnOnField: Boolean? = null
        var playerOffOnField: Boolean? = null
        var jerseyOn: String
        var jerseyOff: String
        var bloodsub: Boolean
        var blackcard: Boolean
        var allowedSubs: Boolean = true

        view.btnSaveSub.setOnClickListener {
            try {
                // get the input jersey numbers
                jerseyOn = view.sub_player_on_number_input.text.toString()
                jerseyOff = view.sub_player_off_number_input.text.toString()

                // get the input for bloodsub and blackcard checkboxes
                bloodsub = view.sub_bloodsub_checkbox.isChecked
                blackcard = view.sub_blackcard_checkbox.isChecked
                Log.i(TAG, "Blackcard = $blackcard, Bloodsub=$bloodsub")

                // if their is jersey number inputs convert the string to an int
                if (jerseyOn != "") playerOnJerseyInput = convertJerseyNumToInt(jerseyOn)
                if (jerseyOff != "") playerOffJerseyInput = convertJerseyNumToInt(jerseyOff)

                // assign the team document to either teamA or teamB
                if (teamA != null) {
                    teamDocRef = db.collection("Team").document(model.teamA.value!!.id.toString())
                    team = "teamA"
                } else if (teamB != null) {
                    teamDocRef = db.collection("Team").document(model.teamB.value!!.id.toString())
                    team = "teamB"
                }


                // if user doesn't select a team prompt for selection
                if (teamA === null && teamB === null) {
                    Log.i(TAG, "Select A Team")
                    Toast.makeText(context, "Select A Team", Toast.LENGTH_LONG).show()
                }
                // Once the ref has selected a team & if the substitution is not a bloodsub
                //  Check is the team allowed a substitution or are they at max
                else if (!bloodsub && !blackcard) {

                        allowedSubs = checkIfTeamIsAllowedMoreNormalSubs(team)
                        if (!allowedSubs) {
                            Toast.makeText(
                                context,
                                "Substitution NOT Allowed\nMax Subs\nalready used",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            resetSubstitute()
                        }

                }
                if (allowedSubs) {
                    Log.i(TAG, "Substitues Are Allowed")
                    // Check for jersey number inputs for both player going off and player coming on
                    if (jerseyOn == "" || jerseyOff == "" || playerOnJerseyInput === 0 || playerOffJerseyInput === 0) {
                        if (jerseyOn == "" && jerseyOff == "" && playerOnJerseyInput === 0 && playerOffJerseyInput === 0) {
                            Log.i(TAG, "Input Jersey Numbers")
                            Toast.makeText(
                                context,
                                "Input Player Jersey Numbers",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        } else if (playerOnJerseyInput === 0 || jerseyOn == "") {
                            Log.i(TAG, "Input Player On Jersey Number")
                            Toast.makeText(
                                context,
                                "Input Player On\nJersey Number",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        } else if (playerOffJerseyInput === 0 || jerseyOff == "") {
                            Log.i(TAG, "Input Player Off Jersey Number")
                            Toast.makeText(
                                context,
                                "Input Player Off\nJersey Number",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                    /* if player on has been inputted and player off has been inputted
                    check that the player going off is on the field and team sheet,
                    check if the player coming on
                    is not on the field and is on the team sheet.
                 */
                    else if (playerOnJerseyInput > 0 && playerOffJerseyInput > 0) {

                        var onDocRef: DocumentReference? = getMember(team, playerOnJerseyInput)
                        var offDocRef: DocumentReference? = getMember(team, playerOffJerseyInput)

                        Log.i(TAG, "DocRef ON : $onDocRef, DocRef OFF : $offDocRef")

                        /* if the member is on the teamsheet otherwise send a message to tell the
                        referee the player is not on the team sheet
                        if the player is on the teamsheet check if the player is on the field
                        or a substitute
                    * */
                        if (onDocRef != null) {
                            playerOnOnField =
                                app.firebasestore.isPlayerOnTheField(team, playerOnJerseyInput)

                        } else {
                            Toast.makeText(
                                context,
                                "A player with\nJersey Number: $playerOnJerseyInput\nis not on the teamsheet",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                        if (offDocRef != null) {
                            playerOffOnField =
                                app.firebasestore.isPlayerOnTheField(team, playerOffJerseyInput)

                        } else {
                            Toast.makeText(
                                context,
                                "A player with\nJersey Number: $playerOnJerseyInput\nis not on the teamsheet",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                        /* If the player coming on is on the field already send a message to the
                    referee to to tell them the player is already on the field of play
                    * */
                        if (playerOnOnField != null && playerOffOnField != null) {

                            if (playerOnOnField!!) {
                                Toast.makeText(
                                    context,
                                    "Player coming ON:\nNumber $playerOnJerseyInput\nIs already on the field",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }

                            /*
                        If the player going off the field is not on the field send a message to the
                        referee to tell them the player is not currently on the field of play
                         */
                            else if (!playerOffOnField!!)
                                Toast.makeText(
                                    context,
                                    "Player going Off:\nNumber $playerOffJerseyInput\n Is not on the field",
                                    Toast.LENGTH_LONG
                                )
                                    .show()

                            // otherwise assign the document references
                            else {
                                memberOnDocRef = onDocRef
                                memberOffDocRef = offDocRef
                                Log.i(
                                    TAG,
                                    "MemberDocRef On : $memberOnDocRef,  Off : $memberOffDocRef"
                                )

                            }
                        }
                    }
                }

                /* If the Player coming on and Player going off have been inputted correctly
                    and the team has been selected save the substitution
                * */
                if (memberOnDocRef != null && memberOffDocRef != null && (teamA != null || teamB != null)) {
                    sub.game = db.collection("Game").document(app.firebasestore.game.id!!)
                    sub.playerOn = memberOnDocRef
                    sub.playerOff = memberOffDocRef
                    sub.team = teamDocRef
                    sub.bloodsub = bloodsub
                    sub.blackcard = blackcard
                    sub.timestamp = Date()

                    Log.i(TAG, "Sub: $sub")

                    doAsync {

                        val subSaved = app.firebasestore.saveSub(sub)

                        // Update the number of substitutes used
                        if(blackcard) app.firebasestore.updateBlackCardSubs(team)
                        else app.firebasestore.updateNormalSubs(team)


                        uiThread {
                            if (subSaved) {
                                Toast.makeText(context, "Sub Saved", Toast.LENGTH_LONG).show()
                                resetSubstitute()
                            } else
                                Toast.makeText(
                                    context,
                                    "Error Saving\nTry Again",
                                    Toast.LENGTH_LONG
                                ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error saving Substitute: $e")
                Toast.makeText(
                    context,
                    "Error Saving\nTry Again",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    //    Return the member document reference  from teams heet A
    fun getMember(team: String, jerseyInput: Int): DocumentReference? {

        val db = FirebaseFirestore.getInstance()
        var memberDocRef: DocumentReference?
        member = app.firebasestore.findMemberByJerseyNum(team, jerseyInput)!!
        memberDocRef = db.collection("Member").document(member.id!!)
        Log.i(
            TAG,
            "Number inputted: ${jerseyInput}Member: ${member.firstName} ${member.lastName} ${member.id}"
        )
        return memberDocRef
    }


    // Convert the Jersey Number input String value to an integer
    fun convertJerseyNumToInt(jerseyNum: String): Int {
        return Integer.parseInt(jerseyNum)
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

    fun blackCardCheckedListner(view:View) {
        view.sub_blackcard_checkbox.setOnClickListener {
            var team: String? = null
            var allowedBlackCardSubs: Boolean? = null
            if (teamA != null) team = "teamA"
            if (teamB != null) team = "teamB"
            if (view.sub_blackcard_checkbox.isChecked) {
                Log.i(TAG,"Black Card Ticked, team $team")

                if (team != null) {
                    allowedBlackCardSubs = checkIfTeamIsAllowedMoreBlackCardSubs(team)
                    Log.i(TAG,"AllowedBlackCardSubs should = false : $allowedBlackCardSubs")

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
                        Log.i(TAG,"Print a message to say sub not allowed")
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

        Log.i(TAG,"$numberOfSubsUsed < $numberOfSubsAllowed")
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
