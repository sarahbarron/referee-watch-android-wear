package org.wit.myapplication.adapters
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_teamsheet.view.*
import org.wit.myapplication.R
import org.wit.myapplication.models.CardModel
import org.wit.myapplication.models.MemberModel
import org.wit.myapplication.models.TeamsheetPlayerModel
import org.wit.myapplication.models.TeamModel
import java.lang.reflect.Member


interface teamsheetListener{
    fun onPlayerClick(teamsheetPlayerModel: TeamsheetPlayerModel)
}
class TeamsheetAdapter constructor(
    val team: TeamModel,
    val teamsheet: ArrayList<TeamsheetPlayerModel>,
    val players: ArrayList<MemberModel>,
    val listener: teamsheetListener
)

    : RecyclerView.Adapter<CardAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardAdapter.MainHolder {
        Log.i(TeamsheetAdapter.TAG, "onCreateViewHolder: ")
        return CardAdapter.MainHolder(
            LayoutInflater.from(parent.context).inflate
                (R.layout.card_teamsheet, parent, false)
        )
    }



    companion object {
        private const val TAG = "TeamsheeB Adapter"
    }

    override fun onBindViewHolder(holder: CardAdapter.MainHolder, position: Int) {
        val teamsheet_player = teamsheet[holder.adapterPosition]
        val teamName = team.name!!
        val players = players

        var jerseyNumb = teamsheet_player.jerseyNumber.toString()
        var fieldPosition = teamsheet_player.fieldPosition.toString()
        var playerName=""
        var player = MemberModel()

        if(teamsheet.size!=0) player = players.find{p->p.id == teamsheet_player?.id}!!
        if(player.firstName!=null || player.lastName!=null) playerName = "${player.firstName} ${player.lastName}"
        if(fieldPosition == "null" || fieldPosition =="" || fieldPosition == "undefined")
        {
            fieldPosition = "Sub"
        }
        holder.itemView.team.text = teamName
        holder.itemView.jersey_num.text = "Jersey: $jerseyNumb"
        holder.itemView.position_num.text = "Position $fieldPosition"
        holder.itemView.teamsheet_player.text = playerName

    }

    override fun getItemCount(): Int = teamsheet.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(teamsheet_player: TeamsheetPlayerModel, listener: teamsheetListener) {
            itemView.setOnClickListener{listener.onPlayerClick(teamsheet_player)}
        }
    }


}