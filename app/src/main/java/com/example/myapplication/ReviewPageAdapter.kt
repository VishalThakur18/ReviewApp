package com.example.myapplication

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
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
        val cardImage: ImageView = itemView.findViewById(R.id.imageOnCard)
        val cardDist: TextView = itemView.findViewById(R.id.distanceOnCard)
        val cardName: TextView = itemView.findViewById(R.id.nameOfPlace)
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
        holder.cardDesc.text = createBoldText("Coupon expires on ", currentItem.cardDesc)
        holder.cardImage.setImageResource(currentItem.cardImage)
        holder.cardDist.text = currentItem.cardDist
        holder.cardName.text = currentItem.cardName
    }
    // This function removes a card from the list at the given position
    fun removeCard(position: Int) {
        // Check if position is valid
        if (position >= 0 && position < cardList.size) {
            cardList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun createBoldText(prefix: String, boldText: String): CharSequence {
        val formattedText = "$prefix<b>$boldText</b>"
        return android.text.Html.fromHtml(formattedText, android.text.Html.FROM_HTML_MODE_LEGACY)
    }
}