package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import model.Restaurant

class RestaurantAdapter(
    private val restaurants: List<Restaurant>,// MutableList to allow changes
    private val onVoteClick: (Restaurant) -> Unit // Callback passes the entire Restaurant object
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_restaurant_voting, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurants[position]

        // Bind data to views
        holder.restaurantName.text = restaurant.name
        holder.voteCount.text = "${restaurant.votes} Votes"


        // Load the restaurant image using Glide
        Glide.with(holder.itemView.context)
            .load(restaurant.imageUrl)
            .into(holder.restaurantImage)

        // Set background color based on ranking (top 3)
        when (position) {
            0 -> holder.cardView.setBackgroundResource(R.drawable.gold_encircle_bg) // Gold outline for first place
            1 -> holder.cardView.setBackgroundResource(R.drawable.silver_encircle_bg) // Silver outline for second place
            2 -> holder.cardView.setBackgroundResource(R.drawable.bronze_bg_voting)
        }

        // Handle vote-up button click
        holder.voteUpButton.setOnClickListener {
            onVoteClick(restaurant) // Pass the entire Restaurant object
        }
    }

    override fun getItemCount(): Int = restaurants.size

    // ViewHolder for Restaurant item
    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantName: TextView = itemView.findViewById(R.id.restaurantName)
        val voteCount: TextView = itemView.findViewById(R.id.voteCount)
        val restaurantImage: ImageView = itemView.findViewById(R.id.restaurantImage)
        val voteUpButton: CardView = itemView.findViewById(R.id.voteUpButton)
        val cardView: LinearLayout = itemView.findViewById(R.id.cardView) // The main container for the restaurant item
    }
}

