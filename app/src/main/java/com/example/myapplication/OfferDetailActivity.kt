package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.MapView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityOfferDetailBinding

class OfferDetailActivity : AppCompatActivity() {
    private lateinit var mapView: MapView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_detail)

        // Bind UI components
        val offerTitle: TextView = findViewById(R.id.offerDesc)
//        val offerDetails: TextView = findViewById(R.id.textView5)
        val offerImage: ImageView = findViewById(R.id.restImage)
        val restaurantName: TextView = findViewById(R.id.restName)
        val expirationDate : TextView = findViewById(R.id.expirationText)

        // Set up the ImageView as a button
        val backButton: ImageView = findViewById(R.id.backBtn_offerDesc) // Replace with your actual ImageView ID
        backButton.setOnClickListener {
            // Finish the current activity to return to OffersFragment
            finish()
        }



        // Get data passed from the intent
        val title = intent.getStringExtra("title")
        val desc = intent.getStringExtra("desc")
        val imageResId = intent.getStringExtra("image")
        val name = intent.getStringExtra("name")
        val expiry = intent.getStringExtra("date")

        // Set data to the UI components
        offerTitle.text = title
//        offerDetails.text = desc
        restaurantName.text = name
        expirationDate.text = expiry

        Glide.with(this)
            .load(imageResId)
            .into(offerImage)

        // Initialize Google Maps
//        mapView = findViewById(R.id.mapView2)
//        mapView.onCreate(savedInstanceState)
//        mapView.getMapAsync { googleMap ->
//            // Customize the map as needed (e.g., add markers)
        }
    }


//    override fun onResume() {
//        super.onResume()
//        mapView.onResume()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        mapView.onPause()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mapView.onDestroy()
//    }
//
//    override fun onLowMemory() {
//        super.onLowMemory()
//        mapView.onLowMemory()
//    }
//}
