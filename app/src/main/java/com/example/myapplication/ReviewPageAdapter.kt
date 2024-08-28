package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewPageAdapter(private val cardList: MutableList<ReviewCards>):RecyclerView.Adapter<ReviewPageAdapter.MyViewHolder>() {
    class MyViewHolder (itemView: View):RecyclerView.ViewHolder(itemView){
        val cardTitle: TextView = itemView.findViewById(R.id.OfferTitle)
        val cardDesc: TextView = itemView.findViewById(R.id.offersDesc)
        val cardImage: ImageView = itemView.findViewById(R.id.ImageOnCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.card_review, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = cardList[position]
        holder.cardTitle.text = currentItem.cardTitle
        holder.cardDesc.text = currentItem.cardDesc
        holder.cardImage.setImageResource(currentItem.cardImage)

    }
}