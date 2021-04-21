package org.wit.myapplication.adapters



import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.myapplication.R
import org.wit.myapplication.models.ScoreModel


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

