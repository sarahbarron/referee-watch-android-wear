package org.wit.myapplication.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_sub.view.*
import org.wit.myapplication.R
import org.wit.myapplication.models.MemberModel
import org.wit.myapplication.models.SubstituteModel
import org.wit.myapplication.models.TeamModel
import java.text.SimpleDateFormat


interface SubstituteListener{
    fun onSubstituteClick(substitute: SubstituteModel)
}
class SubstituteAdapter constructor(
        val teamA: TeamModel,
        val teamB: TeamModel,
        val players: ArrayList<MemberModel>,
        val substitutes: ArrayList<SubstituteModel>,
        val listener: SubstituteListener
)
    : RecyclerView.Adapter<SubstituteAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        Log.i(TAG, "onCreateViewHolder: ")
        return MainHolder(
            LayoutInflater.from(parent.context).inflate
                (R.layout.card_sub, parent, false)
        )
    }



    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val substitute= substitutes[holder.adapterPosition]
        val teamAname = teamA.name!!
        val teamBname = teamB.name!!
        val teamBid = teamB.id!!
        val players = players

        var bloodsub = ""
        var time =""
        var playerOnName=""
        var playerOffName=""
        var player = MemberModel()
        var team = teamAname

        if(substitute.timestamp!=null) {
            var seconds = substitute.timestamp
            val pattern ="HH.mm"
            val simpleDateFormat = SimpleDateFormat(pattern)
            time = simpleDateFormat.format(seconds)
        }
        if (players.size != 0) {
            player = players.find{ p->p.id == substitute.playerOn?.id}!!
            if (player.firstName !=null || player.lastName !=null ) playerOnName = "Player ON : ${player.firstName} ${player.lastName}"
            player = players.find{ p->p.id == substitute.playerOff?.id}!!
            if (player.firstName !=null || player.lastName !=null ) playerOffName = "Player OFF: ${player.firstName} ${player.lastName}"
        }

        val teamId = substitute.team?.id
        val teamAid = teamA.id
        if(teamId == teamAid){
            team = teamAname
        }
        else{
            team = teamBname
        }
        if(substitute.bloodsub) bloodsub = "Bloodsub"

        holder.itemView.subTime.text = time
        holder.itemView.bloodsub.text = bloodsub
        holder.itemView.subTeam.text = team
        holder.itemView.playerOn.text = playerOnName
        holder.itemView.playerOff.text = playerOffName


        Log.i(TAG, "onBindViewHolderbinding: $substitute")
        holder.bind(substitute, listener)
    }

    override fun getItemCount(): Int = substitutes.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(substitute: SubstituteModel, listener: SubstituteListener) {

            itemView.setOnClickListener{listener.onSubstituteClick(substitute)}
        }
    }




    companion object {
        private const val TAG = "Substitute Adapter"
    }
}

