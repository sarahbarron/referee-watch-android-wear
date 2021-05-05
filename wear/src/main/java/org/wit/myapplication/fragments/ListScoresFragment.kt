package org.wit.myapplication.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.wear.widget.WearableLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list_scores.*
import kotlinx.android.synthetic.main.fragment_list_scores.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.myapplication.R
import org.wit.myapplication.adapters.ScoreAdapter
import org.wit.myapplication.adapters.ScoreListener
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.ScoreModel
import java.lang.Exception
import java.util.ArrayList

class ListScoresFragment : Fragment(), ScoreListener {

    lateinit var app: MainApp
    lateinit var root: View

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        Log.i(TAG, "ListScoresFragment")

        app = activity?.application as MainApp
        root = inflater.inflate(R.layout.fragment_list_scores, container, false)
        root.fragment_list_scores.layoutManager = WearableLinearLayoutManager(activity)
        root.fragment_list_scores.setHasFixedSize(true)
        root.fragment_list_scores.isEdgeItemsCenteringEnabled = false

        // Get Scores
        getScores()

        return root
    }

    // Scores Recycler View
    private fun getScores() {
        Log.i(TAG, "getScores")

        try{

            var scores: ArrayList<ScoreModel>?
            doAsync {
                scores = app.firebasestore.findAllScores()
                uiThread {
                    Log.i(TAG, "GetScores UiThread")
                    showScores(scores!!)
                }
            }

        }catch(e: Exception){
            Log.i(TAG, "Error returning scores: $e")
        }
    }

    fun showScores(scores: ArrayList<ScoreModel>){
        Log.i(TAG, "showScores")
        val scores = scores
        Log.i(TAG, "Scores in showScores :  ${scores.size}")
        val teamA = app.firebasestore.teamA
        val teamB = app.firebasestore.teamB
        val players = app.firebasestore.allPlayers
        if(scores != null) {
            root.fragment_list_scores.adapter = ScoreAdapter(teamA, teamB, players, scores, this)
            root.fragment_list_scores.adapter?.notifyDataSetChanged()
        }
    }


    override fun onScoreClick(score: ScoreModel) {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "Score Fragment"
    }
}