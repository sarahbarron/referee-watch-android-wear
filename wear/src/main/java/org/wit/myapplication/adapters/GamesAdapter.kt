package org.wit.myapplication.adapters


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_games_list.*
import kotlinx.android.synthetic.main.card_games.view.*
import org.wit.myapplication.R
import org.wit.myapplication.models.GameModel
import java.text.SimpleDateFormat


interface GamesListener{
    fun onGameClick(game: GameModel)
}
class GamesAdapter constructor(
    val games: ArrayList<GameModel>,
    val listener: GamesListener
)
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

            var seconds = game.dateTime

            // Convert the date to just show the time
            val pattern ="HH.mm"
            val simpleDateFormat = SimpleDateFormat(pattern)
            val matchTime: String = simpleDateFormat.format(seconds)
            Log.i(TAG, "time $matchTime")

            itemView.gameTime.text = matchTime
            itemView.teamA.text = game.id
        }
    }




    companion object {
        private const val TAG = "Games Adapter"
    }
}

