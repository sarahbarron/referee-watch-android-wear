package org.wit.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_injury.view.*
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

        model.teamA.observe(viewLifecycleOwner, { item-> root.injury_team1.text = item.name})
        model.teamB.observe(viewLifecycleOwner, { item-> root.injury_team2.text = item.name})
        return root
    }

}

