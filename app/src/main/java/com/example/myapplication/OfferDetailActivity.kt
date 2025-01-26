package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class OfferDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_detail)

        // Bind UI components
//        val cardTitle: TextView = findViewById(R.id.detailCardTitle)
//        val cardDesc: TextView = findViewById(R.id.detailCardDesc)
//        val cardImage: ImageView = findViewById(R.id.detailCardImage)
//        val cardDist: TextView = findViewById(R.id.detailCardDist)
//        val cardName: TextView = findViewById(R.id.detailCardName)

        // Get data passed from the intent
        val title = intent.getStringExtra("title")
        val desc = intent.getStringExtra("desc")
        val imageResId = intent.getStringExtra("image") // URL or drawable ID
        val dist = intent.getStringExtra("dist")
        val name = intent.getStringExtra("name")

//        // Set data to the UI components
//        cardTitle.text = title
//        cardDesc.text = desc
//        Glide.with(this)
//            .load(imageResId)
//            .into(cardImage)
//        cardDist.text = dist
//        cardName.text = name
    }
}