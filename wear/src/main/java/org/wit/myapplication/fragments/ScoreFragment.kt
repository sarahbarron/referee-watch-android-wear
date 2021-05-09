package org.wit.myapplication.fragments


import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_score.view.*
import kotlinx.android.synthetic.main.fragment_score.view.team1
import kotlinx.android.synthetic.main.fragment_score.view.team2
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.LiveDataViewModel
import org.wit.myapplication.models.MemberModel
import org.wit.myapplication.models.ScoreModel
import org.wit.myapplication.models.TeamModel
import java.util.*


class ScoreFragment() : Fragment(), Parcelable {

    lateinit var app: MainApp
    lateinit var root: View
    private val model: LiveDataViewModel by activityViewModels()
    var member = MemberModel()
    var score = ScoreModel()
    var teamA: TeamModel? = null
    var teamB: TeamModel? = null
    var goal: Int? = 0
    var point: Int? = 0
    var playerNumber: Int = 0

    lateinit var teamAbtn: ToggleButton
    lateinit var teamBbtn: ToggleButton
    lateinit var goalBtn: ToggleButton
    lateinit var pointBtn: ToggleButton


    constructor(parcel: Parcel) : this() {
        member = parcel.readParcelable(MemberModel::class.java.classLoader)!!
        score = parcel.readParcelable(ScoreModel::class.java.classLoader)!!
        teamA = parcel.readParcelable(TeamModel::class.java.classLoader)
        teamB = parcel.readParcelable(TeamModel::class.java.classLoader)
        playerNumber = parcel.readInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        app = activity?.application as MainApp
        root = inflater.inflate(R.layout.fragment_score, container, false)
        teamSelectedListener(root)
        scoreSelectedListener(root)
        saveScoreListener(root)

        return root
    }


    fun teamSelectedListener(view: View){
        teamAbtn = view.team1
        teamBbtn = view.team2
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

    fun scoreSelectedListener(view:View){
        goalBtn = view.goal
        pointBtn = view.point
        // Toogle Button Listening for a Score Type to be selected
        goalBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                pointBtn.isChecked = false
                goal = 1
            } else {
                goal = 0
            }
        }
        pointBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                goalBtn.isChecked = false
                point = 1
            } else {
                point = 0
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveScoreListener(layout: View) {

        val db = FirebaseFirestore.getInstance()
        var team =""
        var teamDocRef: DocumentReference? = null
        var memberDocRef: DocumentReference? = null
        var jerseyInput = 0
        var onField = false

        layout.saveScoreBtn.setOnClickListener {
            try {
                Log.i("Score Fragment", "input text ${root.player_number_input.text} ::")
                if (layout.player_number_input.text.toString() != "") {
                    jerseyInput = Integer.parseInt(root.player_number_input.text.toString())
                    Log.i("Score Fragment", "JerseyInput $jerseyInput")
                }

                if (teamA == null && teamB == null) {
                    Log.i("ScoreFragment", "Select A Team")
                    Toast.makeText(context, "Select A Team", Toast.LENGTH_LONG).show()
                } else if (goal == 0 && point == 0) {
                    Log.i("ScoreFragment", "Select A Score Type")
                    Toast.makeText(context, "Select Goal\nor Point", Toast.LENGTH_LONG).show()
                } else if (jerseyInput > 0) {
                    if (teamA != null) {
                        onField = app.firebasestore.isPlayerOnTheField("teamA", jerseyInput)

                        if (onField) {
                            member = app.firebasestore.findMemberByJerseyNum("teamA", jerseyInput)!!
                            memberDocRef = db.collection("Member").document(member.id!!)
                            Log.i(
                                "ScoreFragment",
                                "Number inputted: ${jerseyInput}Member: ${member.lastName} ${member.lastName} ${member.id}"
                            )
                        } else Toast.makeText(
                            context,
                            "Player $jerseyInput \nNot On The Field",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (teamB != null) {
                        onField = app.firebasestore.isPlayerOnTheField("teamB", jerseyInput)
                        if (onField) {
                            member = app.firebasestore.findMemberByJerseyNum("teamB", jerseyInput)!!
                            memberDocRef = db.collection("Member").document(member.id!!)
                            Log.i(
                                "ScoreFragment",
                                "Number inputted: ${jerseyInput}: Member: ${member.firstName} ${member.lastName} ${member.id}"
                            )
                        }
                        else Toast.makeText(
                            context,
                            "Player $jerseyInput \nNot On The Field",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                if (teamA != null) {
                    team = "teamA"
                    teamDocRef = db.collection("Team").document(model.teamA.value!!.id.toString())
                } else if (teamB != null) {
                    team = "teamB"
                    teamDocRef = db.collection("Team").document(model.teamB.value!!.id.toString())
                }

                Log.i("Score Fragment", "TeamDocRef: $teamDocRef.id")
                if ((teamA != null || teamB != null) && (goal != 0 || point != 0) && (jerseyInput === 0 || onField)) {
                    score.game = db.collection("Game").document(app.firebasestore.game.id!!)
                    score.goal = goal
                    score.point = point
                    score.member = memberDocRef
                    score.team = teamDocRef
                    score.timestamp = Date()
                    Log.i("Score Fragment", "Update Live Data Score ${team.length} : $score")
                    updateLiveDataScore(team, score)
                    doAsync {
                        val scoreSaved = app.firebasestore.saveScore(score)

                        uiThread {
                            if (scoreSaved)
                                Toast.makeText(context, "Score Saved", Toast.LENGTH_LONG).show()
                            else
                                Toast.makeText(
                                    context,
                                    "Error Saving\nTry Again",
                                    Toast.LENGTH_LONG
                                ).show()
                        }
                    }
                }
            }catch (e: Exception){
                Log.w("SCORE Fragment", "Error Saving Score: $e")
                Toast.makeText(
                    context,
                    "Error Saving\nTry Again",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun updateLiveDataScore(team: String, scoreModel: ScoreModel){

        try {
            if (team === "teamA") {
                var scoreValue = 0
                if (scoreModel.goal == 1) {
                    val currentGoals = model.teamAtotalGoals.value
                    val newGoals = currentGoals?.plus(1)
                    model.teamAtotalGoals.value = newGoals
                    scoreValue = 3
                }
                if (scoreModel.point == 1) {
                    val currentPoints = model.teamAtotalPoints.value
                    val newPoints = currentPoints?.plus(1)
                    model.teamAtotalPoints.value = newPoints
                    scoreValue = 1
                }
                val currentTotal = model.teamAtotal.value
                val newTotal = currentTotal?.plus(scoreValue)
                model.teamAtotal.value = newTotal
            } else if (team === "teamB") {
                var scoreValue = 0
                if (scoreModel.goal == 1) {
                    val currentGoals = model.teamBtotalGoals.value
                    val newGoals = currentGoals?.plus(1)
                    model.teamBtotalGoals.value = newGoals
                    scoreValue = 3
                }
                if (scoreModel.point == 1) {
                    val currentPoints = model.teamBtotalPoints.value
                    val newPoints = currentPoints?.plus(1)
                    model.teamBtotalPoints.value = newPoints
                    scoreValue = 1
                }
                val currentTotal = model.teamBtotal.value
                val newTotal = currentTotal?.plus(scoreValue)
                model.teamBtotal.value = newTotal
            }
        }catch (e:Exception){
            Log.i("SCORE Fragment", "Error updating Live scores")
            Toast.makeText(
                context,
                "Error: updating\nlive scores",
                Toast.LENGTH_LONG
            ).show()
        }

    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(member, flags)
        parcel.writeParcelable(score, flags)
        parcel.writeParcelable(teamA, flags)
        parcel.writeParcelable(teamB, flags)
        parcel.writeInt(playerNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScoreFragment> {
        override fun createFromParcel(parcel: Parcel): ScoreFragment {
            return ScoreFragment(parcel)
        }

        override fun newArray(size: Int): Array<ScoreFragment?> {
            return arrayOfNulls(size)
        }
    }


}

private fun WearableNavigationDrawerView.notifyDataSetChanged() {
    TODO("Not yet implemented")
}



