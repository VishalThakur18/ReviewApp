package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class OfferDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private val TAG = "OfferDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_detail)

        // Bind UI components
        val offerTitle: TextView = findViewById(R.id.offerDesc)
        mapView = findViewById(R.id.mapView)
        val offerImage: ImageView = findViewById(R.id.restImage)
        val restaurantName: TextView = findViewById(R.id.restName)
        val expirationDate: TextView = findViewById(R.id.expirationText)


        // Initialize map
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Set up the ImageView as a back button
        val backButton: ImageView = findViewById(R.id.backBtn_offerDesc)
        backButton.setOnClickListener { finish() }

        // Get data passed from the intent
        val title = intent.getStringExtra("title")
        val imageResId = intent.getStringExtra("image")
        val name = intent.getStringExtra("name")
        val expiry = intent.getStringExtra("date")
        val locationString = intent.getStringExtra("location") // Location passed as "latitude,longitude"

        // Set data to the UI components
        offerTitle.text = title
        restaurantName.text = name
        expirationDate.text = expiry


        Glide.with(this)
            .load(imageResId)
            .into(offerImage)

        // Parse the location string and add a marker
        if (locationString != null) {
            val locationParts = locationString.split(",") // Split "latitude,longitude"
            if (locationParts.size == 2) {
                try {
                    val latitude = locationParts[0].trim().toDouble()
                    val longitude = locationParts[1].trim().toDouble()
                    val location = LatLng(latitude, longitude)

                    Log.d(TAG, "Parsed Location: Lat=$latitude, Lng=$longitude")
                    mapView.getMapAsync { map ->
                        googleMap = map
                        addMarkerOnMap(location)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing location: ${e.message}")
                }
            } else {
                Log.e(TAG, "Invalid location format: $locationString")
            }
        } else {
            Log.e(TAG, "Location string is null")
        }
    }

    private fun addMarkerOnMap(location: LatLng) {
        if (::googleMap.isInitialized) {
            Log.d(TAG, "Adding marker at: $location")
            googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Restaurant Location")
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        } else {
            Log.e(TAG, "GoogleMap is not initialized")
        }
    }

    override fun onMapReady(map: GoogleMap) {
        Log.d(TAG, "Map is ready")
        googleMap = map
    }

    // MapView lifecycle methods
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
