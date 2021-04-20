package org.wit.myapplication.adapters


import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_games_list.*
import kotlinx.android.synthetic.main.card_games.view.*
import org.wit.myapplication.R
import org.wit.myapplication.models.GameModel
import org.wit.myapplication.models.ScoreModel
import org.wit.myapplication.models.TeamModel
import org.wit.myapplication.models.firebase.GamesFireStore
import java.text.SimpleDateFormat


interface ScoreListener{
    fun onScoreClick(score: ScoreModel)
}
class ScoreAdapter constructor(
    val scores: ArrayList<ScoreModel>,
    val listener: ScoreListener
)
    : RecyclerView.Adapter<ScoreAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        Log.i(TAG, "onCreateViewHolder: ");
        return MainHolder(
            LayoutInflater.from(parent?.context).inflate
                (R.layout.card_score, parent, false)
        )
    }



    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val score = scores[holder.adapterPosition]
        Log.i(TAG, "onBindViewHolderbinding: $score");
        holder.bind(score, listener)
    }

    override fun getItemCount(): Int = scores.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(score: ScoreModel, listener: ScoreListener) {



            itemView.setOnClickListener{listener.onScoreClick(score)}
        }
    }




    companion object {
        private const val TAG = "Scores Adapter"
    }
}

