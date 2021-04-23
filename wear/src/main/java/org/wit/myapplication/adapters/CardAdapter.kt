package org.wit.myapplication.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_card.view.*
import org.wit.myapplication.R
import org.wit.myapplication.models.CardModel
import org.wit.myapplication.models.MemberModel
import org.wit.myapplication.models.TeamModel
import java.text.SimpleDateFormat


interface CardListener{
    fun onCardClick(card: CardModel)
}
class CardAdapter constructor(
        val teamA: TeamModel,
        val teamB: TeamModel,
        val players: ArrayList<MemberModel>,
        val cards: ArrayList<CardModel>,
        val listener: CardListener
)
    : RecyclerView.Adapter<CardAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        Log.i(TAG, "onCreateViewHolder: ");
        return MainHolder(
            LayoutInflater.from(parent?.context).inflate
                (R.layout.card_card, parent, false)
        )
    }



    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val card = cards[holder.adapterPosition]
        val teamAname = teamA.name!!
        val teamBname = teamB.name!!
        val teamBid = teamB.id!!
        val players = players!!


        if(card.color == "yellow"){
            Log.i(TAG, "CARD COLOR YELLOW ")
            holder.itemView.setBackgroundColor(Color.YELLOW)

        }
        else if(card.color == "black"){
            Log.i(TAG, "CARD COLOR BLACK")
            holder.itemView.cardTime.setTextColor(Color.WHITE)
            holder.itemView.cardTeam.setTextColor(Color.WHITE)
            holder.itemView.cardPlayer.setTextColor(Color.WHITE)


        }
        else if(card.color =="red"){
            Log.i(TAG, "CARD COLOR RED")
            holder.itemView.setBackgroundColor(Color.RED)
        }
        Log.i(TAG, "onBindViewHolderbinding: $card");
        holder.bind(teamBid, teamAname, teamBname, players,card, listener)
    }

    override fun getItemCount(): Int = cards.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(teamBid:String, teamAname: String, teamBname: String, players: ArrayList<MemberModel>, card: CardModel, listener: CardListener) {
            var time: String=""
            var playerName = ""
            var team: String = teamAname!!
            var player = MemberModel()

            if(card.timestamp!=null) {
                var seconds = card.timestamp
                val pattern ="HH.mm"
                val simpleDateFormat = SimpleDateFormat(pattern)
                time = simpleDateFormat.format(seconds)

            }
            if (players.size != 0) player = players.find{ p->p.id == card.member?.id}!!
            if (player.firstName !=null || player.lastName !=null ) playerName = "${player.firstName} ${player.lastName}"
            if (player.ownClub?.id == teamBid) team = teamBname!!


            itemView.cardTime.text = time
            itemView.cardTeam.text = team
            itemView.cardPlayer.text = playerName

            itemView.setOnClickListener{listener.onCardClick(card)}
        }
    }




    companion object {
        private const val TAG = "Card Adapter"
    }
}

