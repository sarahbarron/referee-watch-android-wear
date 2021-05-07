package org.wit.myapplication.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.wear.widget.WearableLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list_cards.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.myapplication.R
import org.wit.myapplication.adapters.CardAdapter
import org.wit.myapplication.adapters.CardListener
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.CardModel
import org.wit.myapplication.models.LiveDataViewModel
import java.lang.Exception
import java.util.ArrayList

class ListCardsFragment : Fragment(), CardListener {

    lateinit var app: MainApp
    lateinit var root: View
    private val model: LiveDataViewModel by activityViewModels()


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        Log.i(TAG, "ListCardsFragment")

        app = activity?.application as MainApp
        root = inflater.inflate(R.layout.fragment_list_cards, container, false)
        root.fragment_list_cards.layoutManager = WearableLinearLayoutManager(activity)
        root.fragment_list_cards.setHasFixedSize(true)
        root.fragment_list_cards.isEdgeItemsCenteringEnabled = false

        // Get Scores
        app.firebasestore.game.id?.let { getCards(it) }


        return root
    }

    // Scores Recycler View
    private fun getCards(gameId: String) {
        Log.i(TAG, "getScores")

        try{
            var cards: ArrayList<CardModel>?
            doAsync {
                cards = app.firebasestore.findAllCards()
                Log.i(TAG,"GetCards: $cards")
                uiThread {
                    Log.i(TAG, "GetCards UiThread")
                    showCards()
                }
            }

        }catch(e: Exception){
            Log.i(TAG, "Error returning list of cards: $e")
        }
    }

    fun showCards(){
        Log.i(TAG, "showCards")
        val cards = app.firebasestore.cards
        val teamA = model.teamA.value
        val teamB = model.teamB.value
        val players = app.firebasestore.allPlayers
        if(cards != null) {
            root.fragment_list_cards.adapter = CardAdapter(teamA!!, teamB!!, players,cards, this)
            root.fragment_list_cards.adapter?.notifyDataSetChanged()
        }
    }


    override fun onCardClick(card: CardModel) {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "Score Fragment"
    }
}