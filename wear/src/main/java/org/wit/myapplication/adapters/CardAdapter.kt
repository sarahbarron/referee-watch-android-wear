package org.wit.myapplication.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.myapplication.R
import org.wit.myapplication.models.CardModel



interface CardListener{
    fun onCardClick(card: CardModel)
}
class CardAdapter constructor(
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
        Log.i(TAG, "onBindViewHolderbinding: $card");
        holder.bind(card, listener)
    }

    override fun getItemCount(): Int = cards.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(card: CardModel, listener: CardListener) {



            itemView.setOnClickListener{listener.onCardClick(card)}
        }
    }




    companion object {
        private const val TAG = "Card Adapter"
    }
}

