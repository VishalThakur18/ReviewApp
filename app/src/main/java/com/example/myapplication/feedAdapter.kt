package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
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
        // Using Glide to load the image with circular crop
        holder.cardProfile.setImageResource(currentItem.cardProfile)
        holder.cardImage.setImageResource(currentItem.cardImage)
        holder.cardTitle.text = currentItem.cardTitle
//        holder.cardDislikes.text = currentItem.cardDislikes

        holder.cardPrice.text = currentItem.cardPrice
        holder.cardRating.rating = currentItem.cardRating.toFloat()
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardProfile : ImageView = itemView.findViewById(R.id.profileOnCard)
        val cardImage : ImageView = itemView.findViewById(R.id.imageOnCard)
        val cardTitle : TextView = itemView.findViewById(R.id.titleofCard)
        val cardPrice : TextView = itemView.findViewById(R.id.priceOnCard)
        val cardRating : RatingBar = itemView.findViewById(R.id.rating)
//        val cardDislikes : TextView = itemView.findViewById(R.id.dislikesonCard)
    }
}