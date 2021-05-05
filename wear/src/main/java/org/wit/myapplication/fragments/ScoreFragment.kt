package org.wit.myapplication.fragments

import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_score.view.*
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
        layout.saveScoreBtn.setOnClickListener {
            if(teamA == null && teamB ==null)Toast.makeText(context, "Select A Team", Toast.LENGTH_LONG)
            if(goal==0 && point==0)Toast.makeText(context, "Select Goal\nor Point", Toast.LENGTH_LONG)
            var memberDocRef: DocumentReference? = null
            if (layout.player_number_input.inputType > 0) {
                if(teamA != null) {
                    member = app.firebasestore.findMemberByJerseyNum("teamA", layout.player_number_input.inputType)!!
                }
                else if(teamB !=null) {
                    member = app.firebasestore.findMemberByJerseyNum("teamB", layout.player_number_input.inputType)!!

                }
                memberDocRef = db.collection("Member").document(member.id!!)

            }

            score.game = db.collection("Game").document(app.firebasestore.game.id!!)
            score.goal = goal
            score.point = point
            score.member = memberDocRef
            score.timestamp = Date()
            doAsync {
                app.firebasestore.saveScore(score)
//                Toast.makeText(context, "Score Saved", Toast.LENGTH_LONG)

                uiThread {
                    val fm: FragmentManager = requireActivity().supportFragmentManager
                    fm.beginTransaction().replace(R.id.content_frame, StopwatchFragment()).commit()
                }
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

