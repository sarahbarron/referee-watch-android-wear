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
        root.injury_team1.text = app.firebasestore.teamA.name
        root.injury_team2.text = app.firebasestore.teamB.name
        root.injury_team1.textOn = app.firebasestore.teamA.name
        root.injury_team1.textOff = app.firebasestore.teamA.name
        root.injury_team2.textOn = app.firebasestore.teamB.name
        root.injury_team2.textOff = app.firebasestore.teamB.name
        return root
    }

}

