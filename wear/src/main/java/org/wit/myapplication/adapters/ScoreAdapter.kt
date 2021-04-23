package org.wit.myapplication.adapters



import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_score.view.*
import org.wit.myapplication.R
import org.wit.myapplication.models.MemberModel
import org.wit.myapplication.models.ScoreModel
import org.wit.myapplication.models.TeamModel
import java.lang.reflect.Member
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


interface ScoreListener{
    fun onScoreClick(score: ScoreModel)
}
class ScoreAdapter constructor(
        val teamA: TeamModel,
        val teamB: TeamModel,
        val players: ArrayList<MemberModel>,
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
        val teamAname = teamA.name!!
        val teamBname = teamB.name!!
        val teamBid = teamB.id!!
        val players = players!!
        Log.i(TAG, "onBindViewHolderbinding: $score, \nteamBid $teamBid " +
                "\nteamAName: $teamAname \nteamBName: $teamBname \nplayers: $players");
        holder.bind(teamBid, teamAname, teamBname, players, score, listener)
    }

    override fun getItemCount(): Int = scores.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(teamBid:String, teamAname: String, teamBname: String, players: ArrayList<MemberModel>, score: ScoreModel, listener: ScoreListener) {

            var scoretime: String = ""
            var scoretype: String = ""
            var playerName: String = ""
            var team: String = teamAname!!
            var player = MemberModel()
            if (score.timestamp != null) {
                var seconds = score.timestamp
                val pattern ="HH.mm"
                val simpleDateFormat = SimpleDateFormat(pattern)
                scoretime = simpleDateFormat.format(seconds)
            }
            if (score!!.goal == 1) scoretype = "Goal"
            else if(score.point == 1) scoretype ="Point"
            if (players.size != 0) player = players.find{ p->p.id == score.member?.id}!!
            if (player.firstName !=null || player.lastName !=null ) playerName = "${player.firstName} ${player.lastName}"
            if (player.ownClub?.id == teamBid) team = teamBname!!

            itemView.scoreTime.text = scoretime
            itemView.score.text = scoretype
            itemView.scoreTeam.text = team
            itemView.scorePlayer.text = playerName

            itemView.setOnClickListener{listener.onScoreClick(score)}
        }
    }



    companion object {
        private const val TAG = "Scores Adapter"
    }
}

