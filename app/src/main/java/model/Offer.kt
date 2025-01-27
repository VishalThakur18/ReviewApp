package model

import com.google.firebase.firestore.GeoPoint

data class  Offer(
    val expireDate: String = "",
    val imageUrl: String = "",
    val offerDescription: String = "",
    val restaurantName: String = "",
    val location: GeoPoint? = null
)
