package com.example.myapplication

import Restaurant
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class RestaurantAdapter(
    private val restaurants: List<Restaurant>
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
        holder.restaurantImage.setImageResource(restaurant.imageResId)

        // Handle vote-up button click
        holder.voteUpButton.setOnClickListener {
            restaurant.votes++
            holder.voteCount.text = "${restaurant.votes} Votes" // Update UI
        }
    }

    override fun getItemCount(): Int = restaurants.size

    // ViewHolder for Restaurant item
    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantName: TextView = itemView.findViewById(R.id.restaurantName)
        val voteCount: TextView = itemView.findViewById(R.id.voteCount)
        val restaurantImage: ImageView = itemView.findViewById(R.id.restaurantImage)
        val voteUpButton: CardView = itemView.findViewById(R.id.voteUpButton)
    }
}
