package org.wit.myapplication.adapters


import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.card_games.view.*
import org.wit.myapplication.R
import org.wit.myapplication.models.GameModel
import org.wit.myapplication.models.TeamModel
import org.wit.myapplication.models.firebase.GamesFireStore
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
        Log.i(TAG, "onCreateViewHolder: ")
        return MainHolder(
            LayoutInflater.from(parent.context).inflate
                (R.layout.card_games, parent, false)
        )
    }



    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val game = games[holder.adapterPosition]
        Log.i(TAG, "onBindViewHolderbinding: $game")
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

            //
            lateinit var db: FirebaseFirestore
            db = FirebaseFirestore.getInstance()
            if(game.teamA?.id != null) {
                db.collection("Team").document(game.teamA!!.id)
                    .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                        if (e != null) {
                            Log.i(ContentValues.TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        if (snapshot != null) {
                            snapshot.toObject(TeamModel::class.java)
                            itemView.teamA.text = snapshot.get("name").toString()
                        } else {
                            Log.i(ContentValues.TAG, "Team = null")
                        }
                    }
            }


            if(game.teamB?.id != null ) {
                db.collection("Team").document(game.teamB!!.id)
                    .addSnapshotListener addSnapshotListener@{ snapshot, e ->
                        if (e != null) {
                            Log.i(ContentValues.TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        if (snapshot != null) {
                            snapshot.toObject(TeamModel::class.java)
                            itemView.teamB.text = snapshot.get("name").toString()
                        } else {
                            Log.i(ContentValues.TAG, "Team = null")
                        }
                    }
            }

            itemView.setOnClickListener{listener.onGameClick(game)}
        }
    }




    companion object {
        private const val TAG = "Games Adapter"
    }
}

