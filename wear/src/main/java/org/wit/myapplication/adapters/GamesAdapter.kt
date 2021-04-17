package org.wit.myapplication.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.attr
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp

import kotlinx.android.synthetic.main.activity_games_list.*
import kotlinx.android.synthetic.main.card_games.view.*

interface GamesListener{
    fun onGameClick(game: String)
}
class GamesAdapter(val games: ArrayList<ArrayList<String>>,
                   val listener: GamesListener)
    : RecyclerView.Adapter<GamesAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
                LayoutInflater.from(parent.context).inflate
                    (R.layout.card_games, parent, false)
        )
    }



    override fun onBindViewHolder(holder: MainHolder, position: Int) {
       val game = games[holder.adapterPosition]
        holder.bind(game, listener)
    }

    override fun getItemCount(): Int = games.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(game: ArrayList<String>, listener: GamesListener) {
            itemView.game.text = game[1]
        }
    }
}

