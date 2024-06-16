package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class feedAdapter (private val cardList: ArrayList<feedCards>) : RecyclerView.Adapter<feedAdapter.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_feed,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = cardList[position]
        holder.cardImage.setImageResource(currentItem.cardImage)
        holder.cardTitle.text = currentItem.cardTitle
//        holder.cardDislikes.text = currentItem.cardDislikes
        holder.cardLikes.text = currentItem.cardLikes
        holder.cardPrice.text = currentItem.cardPrice
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardImage : ImageView = itemView.findViewById(R.id.imageOnCard)
        val cardTitle : TextView = itemView.findViewById(R.id.titleofCard)
        val cardPrice : TextView = itemView.findViewById(R.id.priceOnCard)
        val cardLikes : TextView = itemView.findViewById(R.id.ratings)
//        val cardDislikes : TextView = itemView.findViewById(R.id.dislikesonCard)
    }
}