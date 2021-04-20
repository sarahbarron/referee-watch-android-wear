package org.wit.myapplication.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.myapplication.R
import org.wit.myapplication.models.SubstituteModel


interface SubstituteListener{
    fun onSubstituteClick(substitute: SubstituteModel)
}
class SubstituteAdapter constructor(
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
        Log.i(TAG, "onBindViewHolderbinding: $substitute");
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

