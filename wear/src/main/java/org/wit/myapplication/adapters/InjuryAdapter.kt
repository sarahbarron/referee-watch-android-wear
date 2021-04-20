package org.wit.myapplication.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.myapplication.R
import org.wit.myapplication.models.InjuryModel


interface InjuryListener{
    fun onInjuryClick(injury: InjuryModel)
}
class InjuryAdapter constructor(
    val injuries: ArrayList<InjuryModel>,
    val listener: InjuryListener
)
    : RecyclerView.Adapter<InjuryAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        Log.i(TAG, "onCreateViewHolder: ");
        return MainHolder(
            LayoutInflater.from(parent?.context).inflate
                (R.layout.card_injury, parent, false)
        )
    }



    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val injury = injuries[holder.adapterPosition]
        Log.i(TAG, "onBindViewHolderbinding: $injuries");
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

