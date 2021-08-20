package org.wit.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.wear.widget.WearableLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list_teamsheet.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.myapplication.R
import org.wit.myapplication.adapters.TeamsheetAdapter
import org.wit.myapplication.adapters.teamsheetListener
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.LiveDataViewModel
import org.wit.myapplication.models.TeamsheetPlayerModel
import java.lang.Exception
import java.util.ArrayList

class ListTeamsheetAFragment: Fragment(), teamsheetListener {

    lateinit var app: MainApp
    lateinit var root: View
    private val model: LiveDataViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        Log.i(TAG, "ListCardsFragment")

        app = activity?.application as MainApp
        root = inflater.inflate(R.layout.fragment_list_teamsheet, container, false)
        root.fragment_list_teamsheet.layoutManager = WearableLinearLayoutManager(activity)
        root.fragment_list_teamsheet.setHasFixedSize(true)
        root.fragment_list_teamsheet.isEdgeItemsCenteringEnabled = false

        // Get Scores
        app.firebasestore.game.id?.let { getTeamsheet(it) }


        return root
    }

    // Scores Recycler View
    private fun getTeamsheet(gameId: String) {
        Log.i(TAG, "getTeamsheet")

        try{
            var teamsheet: ArrayList<TeamsheetPlayerModel>?
            doAsync {
                teamsheet = app.firebasestore.findAllTeamsheetA()
                Log.i(TAG,"GetTeamsheet: $teamsheet")
                uiThread {
                    Log.i(TAG, "GetTeamsheet UiThread")
                    showTeamsheet()
                }
            }

        }catch(e: Exception){
            Log.i(TAG, "Error returning list of teamsheet: $e")
        }
    }

    fun showTeamsheet(){
        Log.i(TAG, "showCards")
        val teamsheet = app.firebasestore.teamAPlayers
        val team = model.teamA.value
        val players = app.firebasestore.allPlayers
        if(teamsheet != null) {
            root.fragment_list_teamsheet.adapter = TeamsheetAdapter(team!!, teamsheet!!, players, this)
            root.fragment_list_teamsheet.adapter?.notifyDataSetChanged()
        }
    }


    override fun onPlayerClick(teamsheetPlayerModel: TeamsheetPlayerModel) {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "Teamsheet A Fragment"
    }


}