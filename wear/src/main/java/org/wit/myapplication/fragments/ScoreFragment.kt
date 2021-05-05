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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_score.*
import kotlinx.android.synthetic.main.fragment_score.view.*
import kotlinx.android.synthetic.main.fragment_stopwatch.*
import kotlinx.coroutines.delay
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.MemberModel
import org.wit.myapplication.models.ScoreModel
import org.wit.myapplication.models.TeamModel
import org.wit.myapplication.models.stopwatch.LiveDataViewModel
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


        val teamAbtn: ToggleButton = root.team1
        val teamBbtn: ToggleButton = root.team2
        val goalBtn: ToggleButton = root.goal
        val pointBtn: ToggleButton = root.point

        teamAbtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                teamBbtn.isChecked = false
                teamA = app.firebasestore.teamA
            } else {
                teamA = null
            }
        }
        teamBbtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                teamAbtn.isChecked = false
                teamB = app.firebasestore.teamB
            } else {
                teamB = null
            }
        }


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
        saveScoreListener(root)

        return root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun saveScoreListener(layout: View) {

        val db = FirebaseFirestore.getInstance()
        var team = ""
        var memberDocRef: DocumentReference? = null
        var jerseyInput = 0

        Log.i("Score Fragment", "input text ${layout.player_number_input.text} ::")
        if(layout.player_number_input.text.toString() != "" )
        {
            jerseyInput = Integer.parseInt(layout.player_number_input.text.toString())

        }

        layout.saveScoreBtn.setOnClickListener {
            if(teamA == null && teamB ==null){
                Log.i("ScoreFragment", "Select A Team")
                Toast.makeText(context, "Select A Team", Toast.LENGTH_LONG).show()
            }
            else if(goal==0 && point==0){
                Log.i("ScoreFragment", "Select A Score Type")
                Toast.makeText(context, "Select Goal\nor Point", Toast.LENGTH_LONG).show()
            }
            else if ( jerseyInput > 0 ) {
                if(teamA != null) {
                    team="teamA"
                    member = app.firebasestore.findMemberByJerseyNum("teamA", jerseyInput)!!
                    Log.i("ScoreFragment","Number inputted: ${jerseyInput}Member: ${member.lastName} ${member.lastName} ${member.id}" )
                }
                else if(teamB !=null) {
                    team="teamB"
                    member = app.firebasestore.findMemberByJerseyNum("teamB", jerseyInput)!!
                    Log.i("ScoreFragment","Number inputted: ${jerseyInput}: Member: ${member.firstName} ${member.lastName} ${member.id}" )

                }
                memberDocRef = db.collection("Member").document(member.id!!)
            }

            if((teamA!=null || teamB!=null) && (goal!=0 || point!=0)) {
                score.game = db.collection("Game").document(app.firebasestore.game.id!!)
                score.goal = goal
                score.point = point
                score.member = memberDocRef
                score.timestamp = Date()
                updateLiveDataScore(team, score)
                doAsync {
                    app.firebasestore.saveScore(score)

                    uiThread {
                        Toast.makeText(context, "Score Saved", Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }

    fun updateLiveDataScore(team: String, scoreModel: ScoreModel){
        if(team === "teamA") {
            if (scoreModel.goal == 1) {
                val currentGoals = model.teamAtotalGoals.value
                val newGoals = currentGoals?.plus(1)
                model.teamAtotalGoals.value = newGoals
            }
            if (scoreModel.point == 1) {
                val currentPoints = model.teamAtotalPoints.value
                val newPoints = currentPoints?.plus(1)
                model.teamAtotalGoals.value = newPoints
            }
        }
        else if(team === "teamB") {
            if (scoreModel.goal == 1) {
                val currentGoals = model.teamBtotalGoals.value
                val newGoals = currentGoals?.plus(1)
                model.teamBtotalGoals.value = newGoals
            }
            if (scoreModel.point == 1) {
                val currentPoints = model.teamBtotalPoints.value
                val newPoints = currentPoints?.plus(1)
                model.teamBtotalGoals.value = newPoints
            }
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



