package org.wit.myapplication.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.wear.widget.WearableLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list_injuries.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.myapplication.R
import org.wit.myapplication.adapters.*
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.InjuryModel
import java.lang.Exception
import java.util.ArrayList

class ListInjuriesFragment : Fragment(), InjuryListener {

    lateinit var app: MainApp
    lateinit var root: View

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        Log.i(TAG, "ListInjuriesFragment")

        app = activity?.application as MainApp
        root = inflater.inflate(R.layout.fragment_list_injuries, container, false)
        root.fragment_list_injuries.layoutManager = WearableLinearLayoutManager(activity)
        root.fragment_list_injuries.setHasFixedSize(true)
        root.fragment_list_injuries.isEdgeItemsCenteringEnabled = false

        // Get Scores
        app.firebasestore.game.id?.let { getInjuries(it) }


        return root
    }

    // Scores Recycler View
    private fun getInjuries(gameId: String) {
        Log.i(TAG, "getInjuries")

        try{
            var injuries: ArrayList<InjuryModel>?
            doAsync {
                injuries = app.firebasestore.findAllInjuries()
                Log.i(TAG,"GetInjuries: $injuries")
                uiThread {
                    Log.i(TAG, "GetInjuries UiThread")
                    showInjuries()
                }
            }

        }catch(e: Exception){
            Log.i(TAG, "Error returning list of injuries: $e")
        }
    }

    fun showInjuries(){
        Log.i(TAG, "showInjuries")
        val injuries = app.firebasestore.injuries
        val teamA = app.firebasestore.teamA
        val teamB = app.firebasestore.teamB
        val players = app.firebasestore.allPlayers
        if(injuries != null) {
            root.fragment_list_injuries.adapter = InjuryAdapter(teamA, teamB, players,injuries, this)
            root.fragment_list_injuries.adapter?.notifyDataSetChanged()
        }
    }


    override fun onInjuryClick(injury: InjuryModel) {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "Score Fragment"
    }
}