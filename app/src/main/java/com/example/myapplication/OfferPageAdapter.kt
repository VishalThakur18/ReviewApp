package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import model.Offer

class OfferPageAdapter(private val offerList: MutableList<Offer>) :
    RecyclerView.Adapter<OfferPageAdapter.OfferViewHolder>() {

    class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.OfferTitle)
        val cardDesc: TextView = itemView.findViewById(R.id.offersDesc)
        val cardImage: ImageView = itemView.findViewById(R.id.imageOnCard)
        val cardName: TextView = itemView.findViewById(R.id.nameOfPlace)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_review, parent, false)
        return OfferViewHolder(itemView)
    }

    override fun getItemCount(): Int = offerList.size

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        val currentOffer = offerList[position]
        holder.cardTitle.text = currentOffer.offerDescription
        holder.cardDesc.text = createBoldText("Coupon expires on ", currentOffer.expireDate)
        holder.cardName.text = currentOffer.restaurantName

        // Use Glide to load the image URL
        Glide.with(holder.itemView.context)
            .load(currentOffer.imageUrl)
            .into(holder.cardImage)
    }

    private fun createBoldText(prefix: String, boldText: String): CharSequence {
        val formattedText = "$prefix<b>$boldText</b>"
        return android.text.Html.fromHtml(formattedText, android.text.Html.FROM_HTML_MODE_LEGACY)
    }

    // Add a method to update the list dynamically
    fun updateOffers(newOffers: List<Offer>) {
        offerList.clear()
        offerList.addAll(newOffers)
        notifyDataSetChanged()
    }
}
