package org.wit.myapplication.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_injury.view.*
import org.wit.myapplication.R
import org.wit.myapplication.models.InjuryModel
import org.wit.myapplication.models.MemberModel
import org.wit.myapplication.models.TeamModel
import java.text.SimpleDateFormat


interface InjuryListener{
    fun onInjuryClick(injury: InjuryModel)
}
class InjuryAdapter constructor(
        val teamA: TeamModel,
        val teamB: TeamModel,
        val players: ArrayList<MemberModel>,
        val injuries: ArrayList<InjuryModel>,
        val listener: InjuryListener
)
    : RecyclerView.Adapter<InjuryAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        Log.i(TAG, "onCreateViewHolder: ")
        return MainHolder(
            LayoutInflater.from(parent.context).inflate
                (R.layout.card_injury, parent, false)
        )
    }



    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val injury = injuries[holder.adapterPosition]

        val teamAname = teamA.name!!
        val teamBname = teamB.name!!
        val teamBid = teamB.id!!
        val players = players

        var time =""
        var team: String = teamAname
        var player = MemberModel()
        var playerName = ""

        if(injury.timestamp!=null) {
            var seconds = injury.timestamp
            val pattern ="HH.mm"
            val simpleDateFormat = SimpleDateFormat(pattern)
            time = simpleDateFormat.format(seconds)

        }
        if (players.size != 0) player = players.find{ p->p.id == injury.member?.id}!!
        if (player.firstName !=null || player.lastName !=null ) playerName = "${player.firstName} ${player.lastName}"

        val teamId = injury.team?.id
        val teamAid = teamA.id
        if(teamId == teamAid){
            team = teamAname
        }
        else{
            team = teamBname
        }

        holder.itemView.injuryTime.text = time
        holder.itemView.injuryTeam.text = team
        holder.itemView.injuredPlayer.text = playerName

        Log.i(TAG, "onBindViewHolderbinding: $injuries")
        holder.bind(injury, listener)
    }

    override fun getItemCount(): Int = injuries.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(injury: InjuryModel, listener: InjuryListener) {

            itemView.setOnClickListener{listener.onInjuryClick(injury)}
        }
    }




    companion object {
        private const val TAG = "Injury Adapter"
    }
}

