package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.GeoPoint
import model.Offer


class OfferPageAdapter(
    private val offerList: MutableList<Offer>,
    private val onItemClick : (Offer)-> Unit
    ) : RecyclerView.Adapter<OfferPageAdapter.OfferViewHolder>() {

    class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.OfferTitle)
        val cardDesc: TextView = itemView.findViewById(R.id.offersDesc)
        val cardImage: ImageView = itemView.findViewById(R.id.imageOnCard)
        val cardName: TextView = itemView.findViewById(R.id.nameOfPlace)
        val locationText: TextView = itemView.findViewById(R.id.locationText)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_review, parent, false)
        return OfferViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return offerList.size
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        val currentOffer = offerList[position]
        holder.cardTitle.text = currentOffer.offerDescription
        holder.cardDesc.text = createBoldText("Coupon expires : ", currentOffer.expireDate)
        holder.cardName.text = currentOffer.restaurantName


        // Use Glide to load the image URL
        Glide.with(holder.itemView.context)
            .load(currentOffer.imageUrl)
            .into(holder.cardImage)

        // Display location from GeoPoint
        val location = currentOffer.location
        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude
            holder.locationText.text = "Lat: $latitude, Long: $longitude"
        } else {
            holder.locationText.text = "Location not available"
        }

        // Set click listener for the entire card
        holder.itemView.setOnClickListener {
            // Navigate to OfferDetailActivity with Intent
            val context = holder.itemView.context
            val intent = Intent(context, OfferDetailActivity::class.java)
            intent.putExtra("title", currentOffer.offerDescription)
            //intent.putExtra("desc", currentOffer.offerDetails) // Include details
            intent.putExtra("image", currentOffer.imageUrl)
            //intent.putExtra("dist", currentOffer.restaurantDistance)
            intent.putExtra("name", currentOffer.restaurantName)
            intent.putExtra("date", currentOffer.expireDate)
            intent.putExtra("location", "${location?.latitude},${location?.longitude}")
            context.startActivity(intent)
        }
    }

    private fun createBoldText(prefix: String, boldText: String): CharSequence {
        val formattedText = "$prefix<b>$boldText</b>"
        return android.text.Html.fromHtml(formattedText, android.text.Html.FROM_HTML_MODE_LEGACY)
    }


}
