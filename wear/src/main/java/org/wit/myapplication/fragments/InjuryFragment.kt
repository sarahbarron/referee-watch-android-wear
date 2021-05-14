package org.wit.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_injury.view.*
import kotlinx.android.synthetic.main.fragment_score.view.*
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.LiveDataViewModel

class InjuryFragment : Fragment() {
    lateinit var app: MainApp
    lateinit var root: View
    private val model: LiveDataViewModel by activityViewModels()

    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {


        app = activity?.application as MainApp
        root = inflater.inflate(R.layout.fragment_injury, container, false)

        val teamAName = app.firebasestore.teamA.name
        val teamBName = app.firebasestore.teamB.name
        if(teamAName !=null && teamBName!=null) {
            root.injury_team1.text = teamAName
            root.injury_team2.text = teamBName
            root.injury_team1.textOn = teamAName
            root.injury_team1.textOff = teamAName
            root.injury_team2.textOn = teamBName
            root.injury_team2.textOff = teamBName
        }
        return root
    }

}

