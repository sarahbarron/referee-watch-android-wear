package org.wit.myapplication.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.myapplication.R
import android.util.Log

import kotlinx.android.synthetic.main.activity_games_list.*
import kotlinx.android.synthetic.main.card_games.view.*
import kotlinx.coroutines.Deferred
import org.wit.myapplication.models.GameModel
import java.util.logging.Level.INFO


interface GamesListener{
    fun onGameClick(game: GameModel)
}
class GamesAdapter constructor(val games: ArrayList<GameModel>,
                   val listener: GamesListener)
    : RecyclerView.Adapter<GamesAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        Log.i(TAG, "onCreateViewHolder: ");
        return MainHolder(
                LayoutInflater.from(parent?.context).inflate
                    (R.layout.card_games, parent, false)
        )
    }



    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val game = games[holder.adapterPosition]
        Log.i(TAG, "onBindViewHolderbinding: $game");
        holder.bind(game, listener)
    }

    override fun getItemCount(): Int = games.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(game: GameModel, listener: GamesListener) {
            val x = 0
            itemView.gameTime.text = game.dateTime.toString()
            itemView.teamA.text = game.id
        }
    }


    companion object {
        private const val TAG = "Games Adapter"
    }
}

