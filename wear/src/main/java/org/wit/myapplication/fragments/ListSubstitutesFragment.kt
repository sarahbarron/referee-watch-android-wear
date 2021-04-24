package org.wit.myapplication.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.wear.widget.WearableLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list_cards.view.*
import kotlinx.android.synthetic.main.fragment_list_scores.*
import kotlinx.android.synthetic.main.fragment_list_scores.view.*
import kotlinx.android.synthetic.main.fragment_list_substitutes.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.myapplication.R
import org.wit.myapplication.adapters.*
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.CardModel
import org.wit.myapplication.models.ScoreModel
import org.wit.myapplication.models.SubstituteModel
import java.lang.Exception
import java.util.ArrayList

class ListSubstitutesFragment : Fragment(), SubstituteListener {

    lateinit var app: MainApp
    lateinit var root: View

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        Log.i(TAG, "ListSubstituteFragment")

        app = activity?.application as MainApp
        root = inflater.inflate(R.layout.fragment_list_substitutes, container, false)
        root.fragment_list_substitutes.layoutManager = WearableLinearLayoutManager(activity)
        root.fragment_list_substitutes.setHasFixedSize(true)
        root.fragment_list_substitutes.isEdgeItemsCenteringEnabled = false

        // Get Scores
        app.firebasestore.game.id?.let { getSubs(it) }


        return root
    }

    // Scores Recycler View
    private fun getSubs(gameId: String) {
        Log.i(TAG, "getSubs")

        try{
            var substitutes: ArrayList<SubstituteModel>?
            doAsync {
                substitutes = app.firebasestore.findAllSubstitutes()
                Log.i(TAG,"GetSubstitutes: $substitutes")
                uiThread {
                    Log.i(TAG, "GetSubstitues UiThread")
                    showSubs()
                }
            }

        }catch(e: Exception){
            Log.i(TAG, "Error returning list of subs: $e")
        }
    }

    fun showSubs(){
        Log.i(TAG, "showSubs")
        val substitutes = app.firebasestore.substitutes
        val teamA = app.firebasestore.teamA
        val teamB = app.firebasestore.teamB
        val players = app.firebasestore.allPlayers
        if(substitutes != null) {
            root.fragment_list_substitutes.adapter = SubstituteAdapter(teamA, teamB, players,substitutes, this)
            root.fragment_list_substitutes.adapter?.notifyDataSetChanged()
        }
    }


    companion object {
        private const val TAG = "Substitute Fragment"
    }

    override fun onSubstituteClick(substitute: SubstituteModel) {
        TODO("Not yet implemented")
    }
}